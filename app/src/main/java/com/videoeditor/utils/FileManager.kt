package com.videoeditor.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileManager(private val context: Context) {
    
    companion object {
        private const val APP_FOLDER = "VideoEditor"
        private const val TEMP_FOLDER = "temp"
        private const val OUTPUT_FOLDER = "output"
    }
    
    /**
     * Get the app's main directory in Movies
     */
    fun getAppDirectory(): File {
        val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val appDir = File(moviesDir, APP_FOLDER)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }
    
    /**
     * Get temp directory for processing
     */
    fun getTempDirectory(): File {
        val tempDir = File(context.cacheDir, TEMP_FOLDER)
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return tempDir
    }
    
    /**
     * Get output directory for processed videos
     */
    fun getOutputDirectory(): File {
        val outputDir = File(getAppDirectory(), OUTPUT_FOLDER)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        return outputDir
    }
    
    /**
     * Generate unique filename with timestamp
     */
    fun generateFileName(prefix: String = "video", extension: String = "mp4"): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "${prefix}_${timestamp}.${extension}"
    }
    
    /**
     * Get file size in MB
     */
    fun getFileSizeMB(file: File): Double {
        return file.length().toDouble() / (1024 * 1024)
    }
    
    /**
     * Delete file
     */
    fun deleteFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Clean temp directory
     */
    fun cleanTempDirectory() {
        try {
            getTempDirectory().listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Copy file from URI to temp directory
     */
    suspend fun copyFileFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val fileName = getFileNameFromUri(uri) ?: generateFileName()
            val outputFile = File(getTempDirectory(), fileName)
            
            FileOutputStream(outputFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            
            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get filename from URI
     */
    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }
    
    /**
     * Add video to MediaStore (make it visible in Gallery)
     */
    suspend fun addVideoToGallery(videoPath: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val videoFile = File(videoPath)
            if (!videoFile.exists()) return@withContext null
            
            val values = android.content.ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/$APP_FOLDER")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
            
            val uri = context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: return@withContext null
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                videoFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get MIME type from file
     */
    fun getMimeType(file: File): String? {
        val extension = file.extension
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}
