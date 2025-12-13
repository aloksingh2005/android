package com.videoeditor.processing

import android.content.Context
import com.videoeditor.data.models.AspectRatio
import com.videoeditor.utils.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

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
     * Process video with trimming and aspect ratio conversion
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
            val exitCode = executeFFmpeg(command)
            
            if (exitCode == 0) {
                if (outputFile.exists()) {
                    onProgress(100, "Processing complete!")
                    Result.success(outputFile.absolutePath)
                } else {
                    Result.failure(Exception("Output file not created"))
                }
            } else {
                Result.failure(Exception("FFmpeg error: Exit code $exitCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Build FFmpeg command for video processing
     */
    private fun buildFFmpegCommand(options: ProcessingOptions, outputPath: String): Array<String> {
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
        
        return arrayOf(
            "ffmpeg",
            "-i", options.inputPath,
            "-ss", startTime,
            "-t", duration,
            "-vf", videoFilter,
            "-c:v", "libx264",
            "-preset", "medium",
            "-crf", "23",
            "-c:a", "aac",
            "-b:a", "128k",
            "-movflags", "+faststart",
            "-y",
            outputPath
        )
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
     * Extract video thumbnail at specific time
     */
    suspend fun extractThumbnail(
        videoPath: String,
        timeMs: Long,
        outputPath: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val time = formatTime(timeMs)
            val command = arrayOf(
                "ffmpeg",
                "-i", videoPath,
                "-ss", time,
                "-vframes", "1",
                "-y",
                outputPath
            )
            
            val exitCode = executeFFmpeg(command)
            
            if (exitCode == 0) {
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
            val command = arrayOf(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoPath
            )
            
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine() ?: "0"
            process.waitFor()
            
            (output.trim().toDoubleOrNull() ?: 0.0).toLong() * 1000
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * Execute FFmpeg command and return exit code
     */
    private fun executeFFmpeg(command: Array<String>): Int {
        return try {
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
        } catch (e: Exception) {
            -1
        }
    }
}
