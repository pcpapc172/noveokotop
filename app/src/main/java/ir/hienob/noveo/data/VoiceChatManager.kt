package ir.hienob.noveo.data

import android.content.Context
import io.livekit.android.LiveKit
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.Room
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.participant.RemoteParticipant
import io.livekit.android.room.track.LocalAudioTrack
import io.livekit.android.room.track.LocalAudioTrackOptions
import io.livekit.android.room.track.RemoteAudioTrack
import io.livekit.android.room.track.RemoteVideoTrack
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.TrackPublication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import timber.log.Timber

data class VoiceChatState(
    val connectionState: VoiceConnectionState = VoiceConnectionState.IDLE,
    val currentChatId: String? = null,
    val currentCallId: String? = null,
    val currentRoomName: String? = null,
    val isMuted: Boolean = true,
    val isDeafened: Boolean = false,
    val isMinimized: Boolean = false,
    val activeSpeakers: List<String> = emptyList(),
    val participantIds: List<String> = emptyList(),
    val isScreenSharing: Boolean = false,
    val screenShareOwnerId: String? = null
)

enum class VoiceConnectionState {
    IDLE, CONNECTING, CONNECTED, RECONNECTING
}

class VoiceChatManager(
    private val context: Context,
    private val api: NoveoApi
) {
    private val scope = (context.applicationContext as ir.hienob.noveo.NoveoApplication).applicationScope

    companion object {
        @Volatile
        private var instance: VoiceChatManager? = null

        fun getInstance(context: Context, api: NoveoApi): VoiceChatManager {
            return instance ?: synchronized(this) {
                instance ?: VoiceChatManager(context.applicationContext, api).also { instance = it }
            }
        }
        
        fun getExisting(): VoiceChatManager? = instance
    }

    private var room: Room? = null
    private var localAudioTrack: LocalAudioTrack? = null
    
    private val _state = MutableStateFlow(VoiceChatState())
    val state = _state.asStateFlow()

    fun joinCall(session: Session, chatId: String, callId: String? = null) {
        scope.launch {
            if (_state.value.currentChatId == chatId && _state.value.connectionState == VoiceConnectionState.CONNECTED) return@launch
            
            if (_state.value.currentChatId != null) {
                leaveCall()
            }

            _state.value = _state.value.copy(
                connectionState = VoiceConnectionState.CONNECTING, 
                currentChatId = chatId,
                currentCallId = callId
            )

            try {
                val tokenPayload = withContext(Dispatchers.IO) {
                    api.getVoiceToken(session, chatId, callId)
                }

                val serverUrl = tokenPayload.getString("serverUrl")
                val token = tokenPayload.getString("participantToken")
                val roomName = tokenPayload.optString("roomName")
                val fetchedCallId = tokenPayload.optString("callId")

                val r: Room = LiveKit.create(context)
                room = r
                
                scope.launch {
                    r.events.collect { event ->
                        when (event) {
                            is RoomEvent.Disconnected -> {
                                _state.value = VoiceChatState(connectionState = VoiceConnectionState.IDLE)
                            }
                            is RoomEvent.ParticipantConnected -> {
                                updateParticipants()
                            }
                            is RoomEvent.ParticipantDisconnected -> {
                                updateParticipants()
                            }
                            is RoomEvent.ActiveSpeakersChanged -> {
                                val activeIds = event.speakers.mapNotNull { it.identity?.value?.toString() }
                                _state.value = _state.value.copy(activeSpeakers = activeIds)
                            }
                            is RoomEvent.Reconnecting -> {
                                _state.value = _state.value.copy(connectionState = VoiceConnectionState.RECONNECTING)
                            }
                            is RoomEvent.Reconnected -> {
                                _state.value = _state.value.copy(connectionState = VoiceConnectionState.CONNECTED)
                                updateParticipants()
                            }
                            is RoomEvent.TrackSubscribed -> {
                                if (event.track is RemoteVideoTrack && event.publication.source == Track.Source.SCREEN_SHARE) {
                                    _state.value = _state.value.copy(
                                        isScreenSharing = true,
                                        screenShareOwnerId = event.participant.identity?.value?.toString()
                                    )
                                }
                                updateParticipants()
                            }
                            is RoomEvent.TrackUnsubscribed -> {
                                if (event.track is RemoteVideoTrack && event.publications.source == Track.Source.SCREEN_SHARE) {
                                     if (_state.value.screenShareOwnerId == event.participant.identity?.value?.toString()) {
                                         _state.value = _state.value.copy(
                                             isScreenSharing = false,
                                             screenShareOwnerId = null
                                         )
                                     }
                                }
                                updateParticipants()
                            }
                            else -> {}
                        }
                    }
                }

                r.connect(serverUrl, token)
                
                _state.value = _state.value.copy(
                    connectionState = VoiceConnectionState.CONNECTED,
                    currentCallId = fetchedCallId,
                    currentRoomName = roomName,
                    currentChatId = chatId
                )

                // Stay muted by default
                updateParticipants()

            } catch (e: Exception) {
                Timber.e(e, "VoiceChat: Failed to join call")
                _state.value = VoiceChatState(connectionState = VoiceConnectionState.IDLE)
            }
        }
    }

    fun leaveCall() {
        scope.launch {
            room?.disconnect()
            room = null
            localAudioTrack?.stop()
            localAudioTrack = null
            _state.value = VoiceChatState()
        }
    }

    fun toggleMute() {
        scope.launch {
            val nextMuted = !_state.value.isMuted
            applyMuteState(nextMuted)
        }
    }

    fun toggleMinimize() {
        _state.value = _state.value.copy(isMinimized = !_state.value.isMinimized)
    }

    private suspend fun applyMuteState(muted: Boolean) {
        val r: Room = room ?: return
        if (muted) {
            localAudioTrack?.let { 
                r.localParticipant.unpublishTrack(it)
                it.stop()
            }
            localAudioTrack = null
        } else {
            if (localAudioTrack == null) {
                val track = r.localParticipant.createAudioTrack("audio", LocalAudioTrackOptions())
                localAudioTrack = track
                r.localParticipant.publishAudioTrack(track)
            }
        }
        _state.value = _state.value.copy(isMuted = muted)
    }

    fun toggleDeafen() {
        setDeafened(!_state.value.isDeafened)
    }

    private fun setDeafened(nextDeafened: Boolean) {
        // In LiveKit Android, deafening involves muting all remote audio tracks
        val r: Room = room ?: return
        val remoteParticipants = r.remoteParticipants
        for (participant in remoteParticipants.values) {
            val publications = participant.audioTrackPublications
            for (pubPair in publications) {
                val track = pubPair.second
                if (track is RemoteAudioTrack) {
                    track.enabled = !nextDeafened
                }
            }
        }
        _state.value = _state.value.copy(isDeafened = nextDeafened)
    }

    private fun updateParticipants() {
        val r: Room = room ?: return
        val participantsList = mutableListOf<String>()
        
        // Include local participant
        val lp = r.localParticipant
        lp.identity?.value?.toString()?.let { participantsList.add(it) }
        
        // Include remote participants
        val remoteParticipants = r.remoteParticipants
        for (p in remoteParticipants.values) {
            val identityValue = p.identity?.value?.toString()
            if (identityValue != null && !participantsList.contains(identityValue)) {
                participantsList.add(identityValue)
            }
        }
        _state.value = _state.value.copy(participantIds = participantsList)
    }
}
