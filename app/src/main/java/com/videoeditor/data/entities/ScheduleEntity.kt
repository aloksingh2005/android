package com.videoeditor.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey
    val id: String,
    val videoPath: String,
    val thumbnailPath: String? = null,
    val scheduledTime: Long, // Unix timestamp in milliseconds
    val caption: String? = null,
    val hashtags: String? = null,
    val isPosted: Boolean = false,
    val isFailed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
