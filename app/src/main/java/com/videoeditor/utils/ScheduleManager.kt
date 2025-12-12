package com.videoeditor.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.videoeditor.data.AppDatabase
import com.videoeditor.data.entities.ScheduleEntity
import com.videoeditor.workers.PostSchedulerWorker
import java.util.UUID
import java.util.concurrent.TimeUnit

class ScheduleManager(private val context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Schedule a post for Instagram
     */
    suspend fun schedulePost(
        videoPath: String,
        scheduledTimeMs: Long,
        caption: String? = null,
        hashtags: String? = null
    ): String {
        val scheduleId = UUID.randomUUID().toString()
        
        // Save to database
        val schedule = ScheduleEntity(
            id = scheduleId,
            videoPath = videoPath,
            scheduledTime = scheduledTimeMs,
            caption = caption,
            hashtags = hashtags
        )
        database.scheduleDao().insertSchedule(schedule)
        
        // Calculate delay
        val currentTime = System.currentTimeMillis()
        val delay = scheduledTimeMs - currentTime
        
        if (delay > 0) {
            // Schedule WorkManager task
            val inputData = Data.Builder()
                .putString(PostSchedulerWorker.KEY_SCHEDULE_ID, scheduleId)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<PostSchedulerWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(scheduleId)
                .build()
            
            workManager.enqueue(workRequest)
        }
        
        return scheduleId
    }
    
    /**
     * Cancel a scheduled post
     */
    suspend fun cancelSchedule(scheduleId: String) {
        // Cancel WorkManager task
        workManager.cancelAllWorkByTag(scheduleId)
        
        // Delete from database
        database.scheduleDao().deleteScheduleById(scheduleId)
    }
    
    /**
     * Update a scheduled post
     */
    suspend fun updateSchedule(schedule: ScheduleEntity) {
        // Cancel existing work
        workManager.cancelAllWorkByTag(schedule.id)
        
        // Update database
        database.scheduleDao().updateSchedule(schedule)
        
        // Reschedule if not posted
        if (!schedule.isPosted) {
            val currentTime = System.currentTimeMillis()
            val delay = schedule.scheduledTime - currentTime
            
            if (delay > 0) {
                val inputData = Data.Builder()
                    .putString(PostSchedulerWorker.KEY_SCHEDULE_ID, schedule.id)
                    .build()
                
                val workRequest = OneTimeWorkRequestBuilder<PostSchedulerWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(schedule.id)
                    .build()
                
                workManager.enqueue(workRequest)
            }
        }
    }
}
