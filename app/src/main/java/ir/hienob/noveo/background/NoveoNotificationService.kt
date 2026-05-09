package ir.hienob.noveo.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ProcessLifecycleOwner
import ir.hienob.noveo.MainActivity
import ir.hienob.noveo.IncomingCallActivity
import ir.hienob.noveo.R
import android.media.RingtoneManager
import android.net.Uri
import ir.hienob.noveo.core.notifications.NotificationChannels
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSocket
import ir.hienob.noveo.data.NotificationSettings
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.SessionStore
import ir.hienob.noveo.data.SocketEvent
import ir.hienob.noveo.data.UserSummary
import ir.hienob.noveo.ui.getStrings
import ir.hienob.noveo.ui.NoveoStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.json.JSONObject

class NoveoNotificationService : LifecycleService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var sessionStore: SessionStore
    private val socket = ChatSocket()
    private var socketJob: Job? = null
    
    companion object {
        private const val CALL_NOTIFICATION_ID = 1001
        private val _socketEvents = MutableSharedFlow<SocketEvent>(extraBufferCapacity = 100)
        val socketEvents = _socketEvents.asSharedFlow()
        
        var isAppInForeground = false
            private set
            
        private var activeSession: Session? = null
        private var instance: NoveoNotificationService? = null
        
        private var _voiceChatManager: ir.hienob.noveo.data.VoiceChatManager? = null
        val voiceChatManager: ir.hienob.noveo.data.VoiceChatManager?
            get() = _voiceChatManager

        // Track known users in service for notification name resolution
        private val knownUsers = mutableMapOf<String, ir.hienob.noveo.data.UserSummary>()

        fun updateKnownUsers(users: Map<String, ir.hienob.noveo.data.UserSummary>) {
            knownUsers.putAll(users)
        }

        fun send(payload: JSONObject): Boolean {
            return instance?.socket?.send(payload) ?: false
        }

        fun start(context: Context) {
            val intent = Intent(context, NoveoNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateCallActive(active: Boolean) {
            instance?.updateForegroundType(active)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionStore = SessionStore(this)
        _voiceChatManager = ir.hienob.noveo.data.VoiceChatManager.getInstance(this, ir.hienob.noveo.data.NoveoApi())
        setupForeground()
        
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        isAppInForeground = true
                        updatePresence(true)
                    }
                    Lifecycle.Event.ON_STOP -> {
                        isAppInForeground = false
                        updatePresence(false)
                    }
                    else -> {}
                }
            }
        })
        
        serviceScope.launch {
            sessionStore.read()?.let { session ->
                activeSession = session
                connectSocket(session)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun setupForeground() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NotificationChannels.SERVICE,
                "Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(serviceChannel)
            
            val messagesChannel = NotificationChannel(
                NotificationChannels.MESSAGES,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(messagesChannel)
            
            val callsChannel = NotificationChannel(
                "calls_v3",
                "Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                setSound(ringtoneUri, android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 500, 500)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(callsChannel)
        }

        val notification = NotificationCompat.Builder(this, NotificationChannels.SERVICE)
            .setContentTitle("Noveo is running")
            .setContentText("Listening for messages")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, notification)
        }
    }

    fun updateForegroundType(isCallActive: Boolean) {
        val notification = NotificationCompat.Builder(this, NotificationChannels.SERVICE)
            .setContentTitle("Noveo is running")
            .setContentText(if (isCallActive) "Active call in progress" else "Listening for messages")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var type = android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            if (isCallActive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Only add microphone type if a call is active
                type = type or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            }
            startForeground(1, notification, type)
        } else {
            startForeground(1, notification)
        }
    }

    private fun connectSocket(session: Session) {
        socketJob?.cancel()
        socketJob = serviceScope.launch {
            while (true) {
                try {
                    socket.connect(session) { knownUsers }.collect { event ->
                        _socketEvents.emit(event)
                        when (event) {
                            is SocketEvent.NewMessage -> {
                                if (!isAppInForeground && event.message.senderId != activeSession?.userId) {
                                    val settings = sessionStore.readNotificationSettings()
                                    if (settings.enabled) {
                                        val shouldNotify = when (event.message.chatType) {
                                            "private" -> settings.dms
                                            "group" -> settings.groups
                                            "channel" -> settings.channels
                                            else -> true
                                        }
                                        if (shouldNotify) {
                                            showNotification(event.message)
                                        }
                                    }
                                }
                            }
                            is SocketEvent.IncomingCall -> {
                                // Always show call notification to ensure consistency
                                showCallNotification(event)
                            }
                            is SocketEvent.VoiceCallEnded -> {
                                cancelCallNotification()
                            }
                            else -> {}
                        }
                    }
                } catch (error: Throwable) {
                    if (error is CancellationException) throw error
                    _socketEvents.emit(SocketEvent.ConnectionState(connected = false))
                    delay(3000)
                }
            }
        }
    }

    private fun updatePresence(online: Boolean) {
        val session = activeSession ?: return
        serviceScope.launch(Dispatchers.IO) {
            val payload = JSONObject()
                .put("type", "presence_update")
                .put("online", online)
            socket.send(payload)
        }
    }

    private fun showNotification(message: ChatMessage) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("chatId", message.chatId)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Reply Action
        val remoteInput = RemoteInput.Builder("key_text_reply")
            .setLabel("Reply...")
            .build()
        
        val replyIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ir.hienob.noveo.ACTION_REPLY"
            putExtra("chatId", message.chatId)
            putExtra("messageId", message.id)
        }
        val replyPendingIntent = PendingIntent.getBroadcast(this, message.chatId.hashCode(), replyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        
        val replyAction = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        // Seen Action
        val seenIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ir.hienob.noveo.ACTION_SEEN"
            putExtra("chatId", message.chatId)
            putExtra("messageId", message.id)
        }
        val seenPendingIntent = PendingIntent.getBroadcast(this, message.chatId.hashCode() + 1, seenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
        val seenAction = NotificationCompat.Action.Builder(
            0,
            "Mark as Read",
            seenPendingIntent
        ).build()

        val notification = NotificationCompat.Builder(this, NotificationChannels.MESSAGES)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.senderName)
            .setContentText(message.content.previewText())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(replyAction)
            .addAction(seenAction)
            .build()

        notificationManager.notify(message.chatId.hashCode(), notification)
    }

    private fun showCallNotification(event: SocketEvent.IncomingCall) {
        // Wake up screen
        val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val wakeLock = powerManager.newWakeLock(
            android.os.PowerManager.FULL_WAKE_LOCK or
                    android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    android.os.PowerManager.ON_AFTER_RELEASE,
            "Noveo:IncomingCall"
        )
        wakeLock.acquire(10000)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val caller = knownUsers[event.callerId]
        val callerName = caller?.username ?: "Unknown Caller"
        
        val strings = getStrings(sessionStore.readLanguageCode())

        val fullScreenIntent = Intent(this, IncomingCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("chatId", event.chatId)
            putExtra("callId", event.callId)
            putExtra("callerId", event.callerId)
        }
        
        // Force start activity immediately to bypass notification buttons
        try {
            startActivity(fullScreenIntent)
        } catch (e: Exception) {
            // Fallback to fullScreenIntent in notification if direct start fails
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(this, 10, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val acceptIntent = Intent(this, IncomingCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("chatId", event.chatId)
            putExtra("callId", event.callId)
            putExtra("callerId", event.callerId)
            putExtra("action", "accept")
        }
        val acceptPendingIntent = PendingIntent.getActivity(this, 11, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val declineIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ir.hienob.noveo.ACTION_DECLINE_CALL"
            putExtra("chatId", event.chatId)
            putExtra("callId", event.callId)
        }
        val declinePendingIntent = PendingIntent.getBroadcast(this, 12, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "calls_v3")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(strings.incomingCall)
            .setContentText(callerName)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setOngoing(true)
            .addAction(0, "Accept", acceptPendingIntent)
            .addAction(0, "Decline", declinePendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            .setVibrate(longArrayOf(0, 500, 500, 500))
            .build()

        notificationManager.notify(CALL_NOTIFICATION_ID, notification)
    }

    private fun cancelCallNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(CALL_NOTIFICATION_ID)
    }

    override fun onDestroy() {
        socketJob?.cancel()
        instance = null
        super.onDestroy()
    }
}
