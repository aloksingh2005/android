package com.videoeditor.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.videoeditor.R
import com.videoeditor.VideoEditorApp
import com.videoeditor.data.AppDatabase
import com.videoeditor.utils.InstagramShareHelper
import com.videoeditor.utils.NetworkConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostSchedulerWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val KEY_SCHEDULE_ID = "schedule_id"
        private const val NOTIFICATION_ID = 1001
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val scheduleId = inputData.getString(KEY_SCHEDULE_ID) ?: return@withContext Result.failure()
            
            val database = AppDatabase.getDatabase(context)
            val schedule = database.scheduleDao().getScheduleById(scheduleId) ?: return@withContext Result.failure()
            
            // Check if already posted
            if (schedule.isPosted) {
                return@withContext Result.success()
            }
            
            // Check network connectivity
            val networkManager = NetworkConnectivityManager(context)
            if (!networkManager.isConnected()) {
                // Reschedule for later (retry in 15 minutes)
                return@withContext Result.retry()
            }
            
            // Send notification to user
            sendNotification(schedule.id, schedule.videoPath, schedule.caption)
            
            return@withContext Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
    
    private fun sendNotification(scheduleId: String, videoPath: String, caption: String?) {
        // Create intent to open app and share to Instagram
        val intent = Intent(context, ShareActivity::class.java).apply {
            putExtra("video_path", videoPath)
            putExtra("caption", caption)
            putExtra("schedule_id", scheduleId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, VideoEditorApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_schedule)
            .setContentTitle(context.getString(R.string.time_to_post))
            .setContentText(context.getString(R.string.tap_to_share_on_instagram))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
