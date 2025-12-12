# Add project specific ProGuard rules here.

# Keep FFmpeg classes
-keep class com.arthenica.ffmpegkit.** { *; }

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep WorkManager classes
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker

# Keep Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
