package com.videoeditor

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import com.yausername.youtubedl_android.YoutubeDL

class VideoEditorApp : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize YouTube Downloader
        try {
            android.util.Log.d("VideoEditorApp", "Initializing YouTubeDL...")
            YoutubeDL.getInstance().init(this, YoutubeDL.UpdateChannel.STABLE)
            android.util.Log.d("VideoEditorApp", "YouTubeDL initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("VideoEditor App", "YouTubeDL initialization failed", e)
            // App can still function without YouTube download feature
        }
        
        // Create notification channel for schedules
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Instagram Schedule Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled Instagram posts"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    companion object {
        const val CHANNEL_ID = "instagram_schedule_channel"
    }
}
