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
            YoutubeDL.getInstance().init(this)
        } catch (e: Exception) {
            e.printStackTrace()
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
