package com.videoeditor.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.videoeditor.data.entities.ScheduleEntity

@Dao
interface ScheduleDao {
    
    @Query("SELECT * FROM schedules ORDER BY scheduledTime ASC")
    fun getAllSchedules(): LiveData<List<ScheduleEntity>>
    
    @Query("SELECT * FROM schedules WHERE isPosted = 0 AND isFailed = 0 ORDER BY scheduledTime ASC")
    fun getPendingSchedules(): LiveData<List<ScheduleEntity>>
    
    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: String): ScheduleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)
    
    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)
    
    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)
    
    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: String)
    
    @Query("UPDATE schedules SET isPosted = 1 WHERE id = :id")
    suspend fun markAsPosted(id: String)
    
    @Query("UPDATE schedules SET isFailed = 1 WHERE id = :id")
    suspend fun markAsFailed(id: String)
}
