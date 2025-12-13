package com.videoeditor.utils

import android.content.Context
// TODO: YouTube-DL library removed (2025)
// import com.yausername.youtubedl_android.YoutubeDL
// import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class YouTubeDownloader(private val context: Context) {
    
    data class VideoInfo(
        val title: String,
        val duration: Long, // in seconds
        val thumbnail: String?
    )
    
    data class DownloadProgress(
        val progress: Float, // 0.0 to 100.0
        val downloadedBytes: Long,
        val totalBytes: Long,
        val eta: String
    )
    
    /**
     * Extract video information from YouTube URL
     * TODO: YouTube-DL library removed - feature temporarily disabled
     */
    suspend fun getVideoInfo(url: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
        Result.failure(
            UnsupportedOperationException("YouTube download feature temporarily unavailable. Library removed.")
        )
    }
    
    /**
     * Download video from YouTube URL
     * TODO: YouTube-DL library removed - feature temporarily disabled
     */
    suspend fun downloadVideo(
        url: String,
        onProgress: (DownloadProgress) -> Unit = {}
    ): Result<String> = withContext(Dispatchers.IO) {
        Result.failure(
            UnsupportedOperationException("YouTube download feature temporarily unavailable. Library removed.")
        )
    }
    
    /**
     * Check if URL is a valid YouTube URL
     */
    fun isYouTubeUrl(url: String): Boolean {
        val patterns = listOf(
            "youtube.com/watch",
            "youtu.be/",
            "youtube.com/shorts",
            "youtube.com/embed"
        )
        return patterns.any { url.contains(it, ignoreCase = true) }
    }
    
    /**
     * Extract video ID from YouTube URL
     */
    fun extractVideoId(url: String): String? {
        val patterns = listOf(
            "(?<=watch\\?v=)[^&]+",
            "(?<=youtu.be/)[^?]+",
            "(?<=embed/)[^?]+",
            "(?<=shorts/)[^?]+"
        )
        
        for (pattern in patterns) {
            val regex = Regex(pattern)
            val match = regex.find(url)
            if (match != null) {
                return match.value
            }
        }
        return null
    }
}
