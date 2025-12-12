package com.videoeditor.processing

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.arthenica.ffmpegkit.Statistics
import com.videoeditor.data.models.AspectRatio
import com.videoeditor.utils.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FFmpegProcessor(private val context: Context) {
    
    private val fileManager = FileManager(context)
    
    data class ProcessingOptions(
        val inputPath: String,
        val startTimeMs: Long,
        val endTimeMs: Long,
        val aspectRatio: AspectRatio,
        val cropMode: CropMode = CropMode.SCALE_AND_CROP
    )
    
    enum class CropMode {
        SCALE_AND_CROP,  // Fill the frame, may crop video
        LETTERBOX        // Add black bars to fit
    }
    
    /**
     * Process video withтай trimming and aspect ratio conversion
     */
    suspend fun processVideo(
        options: ProcessingOptions,
        onProgress: (Int, String) -> Unit = { _, _ -> }
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val outputFile = File(
                fileManager.getOutputDirectory(),
                fileManager.generateFileName("processed")
            )
            
            // Build FFmpeg command
            val command = buildFFmpegCommand(options, outputFile.absolutePath)
            
            onProgress(0, "Starting processing...")
            
            // Execute FFmpeg command
            val session = FFmpegKit.execute(command)
            
            // Monitor progress
            FFmpegKitConfig.enableStatisticsCallback { statistics ->
                val progress = calculateProgress(statistics, options)
                val message = when {
                    progress < 30 -> "Trimming video..."
                    progress < 70 -> "Converting aspect ratio..."
                    else -> "Encoding video..."
                }
                onProgress(progress, message)
            }
            
            val returnCode = session.returnCode
            
            if (ReturnCode.isSuccess(returnCode)) {
                if (outputFile.exists()) {
                    onProgress(100, "Processing complete!")
                    Result.success(outputFile.absolutePath)
                } else {
                    Result.failure(Exception("Output file not created"))
                }
            } else {
                val output = session.failStackTrace ?: "Unknown error"
                Result.failure(Exception("FFmpeg error: $output"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Build FFmpeg command for video processing
     */
    private fun buildFFmpegCommand(options: ProcessingOptions, outputPath: String): String {
        val startTime = formatTime(options.startTimeMs)
        val duration = formatTime(options.endTimeMs - options.startTimeMs)
        
        val width = options.aspectRatio.width
        val height = options.aspectRatio.height
        
        // Video filter for aspect ratio conversion
        val videoFilter = when (options.cropMode) {
            CropMode.SCALE_AND_CROP -> {
                // Scale to fill and crop
                "scale=$width:$height:force_original_aspect_ratio=increase,crop=$width:$height"
            }
            CropMode.LETTERBOX -> {
                // Scale to fit with black bars
                "scale=$width:$height:force_original_aspect_ratio=decrease,pad=$width:$height:(ow-iw)/2:(oh-ih)/2"
            }
        }
        
        return buildString {
            append("-i \"${options.inputPath}\" ")
            append("-ss $startTime ")
            append("-t $duration ")
            append("-vf \"$videoFilter\" ")
            append("-c:v libx264 ")
            append("-preset medium ")
            append("-crf 23 ")
            append("-c:a aac ")
            append("-b:a 128k ")
            append("-movflags +faststart ")
            append("-y ")
            append("\"$outputPath\"")
        }
    }
    
    /**
     * Format time from milliseconds to HH:MM:SS.mmm
     */
    private fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val millis = timeMs % 1000
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
    }
    
    /**
     * Calculate processing progress from FFmpeg statistics
     */
    private fun calculateProgress(statistics: Statistics, options: ProcessingOptions): Int {
        val time = statistics.time // in milliseconds
        val duration = options.endTimeMs - options.startTimeMs
        return if (duration > 0) {
            ((time.toFloat() / duration) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }
    
    /**
     * Extract video thumbnail at specific time
     */
    suspend fun extractThumbnail(
        videoPath: String,
        timeMs: Long,
        outputPath: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val time = formatTime(timeMs)
            val command = "-i \"$videoPath\" -ss $time -vframes 1 -y \"$outputPath\""
            
            val session = FFmpegKit.execute(command)
            
            if (ReturnCode.isSuccess(session.returnCode)) {
                Result.success(outputPath)
            } else {
                Result.failure(Exception("Thumbnail extraction failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get video duration using FFprobe
     */
    suspend fun getVideoDuration(videoPath: String): Long = withContext(Dispatchers.IO) {
        try {
            val command = "-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"$videoPath\""
            val session = FFmpegKit.execute(command)
            
            if (ReturnCode.isSuccess(session.returnCode)) {
                val output = session.output ?: "0"
                (output.trim().toDoubleOrNull() ?: 0.0).toLong() * 1000
            } else {
                0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}
