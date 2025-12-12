package com.videoeditor.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoUtils(private val context: Context) {
    
    data class VideoMetadata(
        val duration: Long, // in milliseconds
        val width: Int,
        val height: Int,
        val rotation: Int,
        val bitrate: Int
    )
    
    /**
     * Get video metadata
     */
    suspend fun getVideoMetadata(videoPath: String): VideoMetadata? = withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoPath)
            
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: 0
            
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: 0
            
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                ?.toIntOrNull() ?: 0
            
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                ?.toIntOrNull() ?: 0
            
            retriever.release()
            
            VideoMetadata(duration, width, height, rotation, bitrate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get video metadata from URI
     */
    suspend fun getVideoMetadataFromUri(uri: Uri): VideoMetadata? = withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: 0
            
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: 0
            
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                ?.toIntOrNull() ?: 0
            
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                ?.toIntOrNull() ?: 0
            
            retriever.release()
            
            VideoMetadata(duration, width, height, rotation, bitrate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Format time in milliseconds to MM:SS format
     */
    fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    /**
     * Format time in milliseconds to HH:MM:SS format
     */
    fun formatTimeLong(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    /**
     * Parse time string (MM:SS) to milliseconds
     */
    fun parseTimeToMs(timeString: String): Long {
        return try {
            val parts = timeString.split(":")
            val minutes = parts.getOrNull(0)?.toLongOrNull() ?: 0
            val seconds = parts.getOrNull(1)?.toLongOrNull() ?: 0
            (minutes * 60 + seconds) * 1000
        } catch (e: Exception) {
            0L
        }
    }
}
