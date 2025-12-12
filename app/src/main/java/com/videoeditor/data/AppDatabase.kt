package com.videoeditor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.videoeditor.data.dao.ScheduleDao
import com.videoeditor.data.entities.ScheduleEntity

@Database(
    entities = [ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun scheduleDao(): ScheduleDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "video_editor_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
