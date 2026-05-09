-optimizationpasses 5
-allowaccessmodification
-repackageclasses

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.** { *; }

-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
