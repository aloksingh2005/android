package com.videoeditor.utils

import android.content.Context
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
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
     */
    suspend fun getVideoInfo(url: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
        try {
            if (!isYouTubeUrl(url)) {
                return@withContext Result.failure(Exception("Invalid YouTube URL"))
            }
            
            val videoInfo = YoutubeDL.getInstance().getInfo(url)
            
            Result.success(
                VideoInfo(
                    title = videoInfo.title ?: "Downloaded Video",
                    duration = videoInfo.duration?.toLong() ?: 0,
                    thumbnail = videoInfo.thumbnail
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Download video from YouTube URL
     */
    suspend fun downloadVideo(
        url: String,
        onProgress: (DownloadProgress) -> Unit = {}
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isYouTubeUrl(url)) {
                return@withContext Result.failure(Exception("Invalid YouTube URL"))
            }
            
            val fileManager = FileManager(context)
            val downloadDir = fileManager.getTempDirectory()
            val outputFile = File(downloadDir, fileManager.generateFileName("youtube"))
            
            val request = YoutubeDLRequest(url)
            request.addOption("-f", "best[ext=mp4]") // Best quality MP4
            request.addOption("-o", outputFile.absolutePath)
            
            // Execute download with progress callback
            YoutubeDL.getInstance().execute(request) { progress, etaInSeconds, line ->
                onProgress(
                    DownloadProgress(
                        progress = progress,
                        downloadedBytes = 0,
                        totalBytes = 0,
                        eta = "${etaInSeconds}s"
                    )
                )
            }
            
            if (outputFile.exists()) {
                Result.success(outputFile.absolutePath)
            } else {
                Result.failure(Exception("Download failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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
