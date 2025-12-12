package com.videoeditor.workers

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.videoeditor.data.AppDatabase
import com.videoeditor.utils.InstagramShareHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val videoPath = intent.getStringExtra("video_path")
        val caption = intent.getStringExtra("caption")
        val scheduleId = intent.getStringExtra("schedule_id")
        
        if (videoPath != null) {
            shareToInstagram(videoPath, caption, scheduleId)
        } else {
            finish()
        }
    }
    
    private fun shareToInstagram(videoPath: String, caption: String?, scheduleId: String?) {
        val instagramHelper = InstagramShareHelper(this)
        
        if (!instagramHelper.isInstagramInstalled()) {
            Toast.makeText(this, "Instagram is not installed", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        val success = instagramHelper.shareToInstagram(videoPath, caption)
        
        if (success && scheduleId != null) {
            // Mark as posted in database
            CoroutineScope(Dispatchers.IO).launch {
                val database = AppDatabase.getDatabase(this@ShareActivity)
                database.scheduleDao().markAsPosted(scheduleId)
            }
        }
        
        finish()
    }
}
