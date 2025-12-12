package com.videoeditor.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class InstagramShareHelper(private val context: Context) {
    
    companion object {
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
    }
    
    /**
     * Check if Instagram is installed
     */
    fun isInstagramInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(INSTAGRAM_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * Share video to Instagram
     */
    fun shareToInstagram(videoPath: String, caption: String? = null): Boolean {
        if (!isInstagramInstalled()) {
            return false
        }
        
        try {
            val videoFile = File(videoPath)
            if (!videoFile.exists()) {
                return false
            }
            
            val videoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                videoFile
            )
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                setPackage(INSTAGRAM_PACKAGE)
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, videoUri)
                
                // Add caption if provided
                caption?.let {
                    putExtra(Intent.EXTRA_TEXT, it)
                }
                
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Check if Instagram can handle this intent
            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(shareIntent)
                return true
            }
            
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Share video to Instagram Stories
     */
    fun shareToInstagramStories(videoPath: String): Boolean {
        if (!isInstagramInstalled()) {
            return false
        }
        
        try {
            val videoFile = File(videoPath)
            if (!videoFile.exists()) {
                return false
            }
            
            val videoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                videoFile
            )
            
            val shareIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                setPackage(INSTAGRAM_PACKAGE)
                type = "video/*"
                putExtra("interactive_asset_uri", videoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(shareIntent)
                return true
            }
            
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Open Instagram app
     */
    fun openInstagram(): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(INSTAGRAM_PACKAGE)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
