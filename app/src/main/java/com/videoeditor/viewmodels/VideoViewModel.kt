package com.videoeditor.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.videoeditor.data.models.AspectRatio
import com.videoeditor.data.models.ProcessingState
import com.videoeditor.data.models.VideoProject
import com.videoeditor.processing.FFmpegProcessor
import com.videoeditor.utils.FileManager
import com.videoeditor.utils.VideoUtils
import com.videoeditor.utils.YouTubeDownloader
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    
    private val fileManager = FileManager(application)
    private val videoUtils = VideoUtils(application)
    private val youtubeDownloader = YouTubeDownloader(application)
    private val ffmpegProcessor = FFmpegProcessor(application)
    
    private val _videoProject = MutableLiveData<VideoProject?>()
    val videoProject: LiveData<VideoProject?> = _videoProject
    
    private val _processingState = MutableLiveData<ProcessingState>(ProcessingState.Idle)
    val processingState: LiveData<ProcessingState> = _processingState
    
    private val _youtubeDownloadProgress = MutableLiveData<Int>(0)
    val youtubeDownloadProgress: LiveData<Int> = _youtubeDownloadProgress
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Load video from local file URI
     */
    fun loadVideoFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                _processingState.value = ProcessingState.InProgress(0, "Loading video...")
                
                // Copy file to temp directory
                val videoPath = fileManager.copyFileFromUri(uri)
                if (videoPath == null) {
                    _errorMessage.value = "Failed to load video"
                    _processingState.value = ProcessingState.Idle
                    return@launch
                }
                
                // Get video metadata
                val metadata = videoUtils.getVideoMetadata(videoPath)
                if (metadata == null) {
                    _errorMessage.value = "Invalid video file"
                    _processingState.value = ProcessingState.Idle
                    return@launch
                }
                
                // Create video project
                _videoProject.value = VideoProject(
                    videoPath = videoPath,
                    videoUri = uri.toString(),
                    videoDuration = metadata.duration,
                    endTime = metadata.duration
                )
                
                _processingState.value = ProcessingState.Idle
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _processingState.value = ProcessingState.Idle
            }
        }
    }
    
    /**
     * Load video from YouTube URL
     */
    fun loadVideoFromYouTubeUrl(url: String) {
        viewModelScope.launch {
            try {
                if (!youtubeDownloader.isYouTubeUrl(url)) {
                    _errorMessage.value = "Invalid YouTube URL"
                    return@launch
                }
                
                _processingState.value = ProcessingState.InProgress(0, "Downloading from YouTube...")
                
                val result = youtubeDownloader.downloadVideo(url) { progress ->
                    _youtubeDownloadProgress.postValue(progress.progress.toInt())
                }
                
                result.fold(
                    onSuccess = { videoPath ->
                        // Get metadata
                        val metadata = videoUtils.getVideoMetadata(videoPath)
                        if (metadata != null) {
                            _videoProject.value = VideoProject(
                                videoPath = videoPath,
                                isYouTubeVideo = true,
                                youtubeUrl = url,
                                videoDuration = metadata.duration,
                                endTime = metadata.duration
                            )
                        }
                        _processingState.value = ProcessingState.Idle
                    },
                    onFailure = { error ->
                        _errorMessage.value = "Download failed: ${error.message}"
                        _processingState.value = ProcessingState.Idle
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _processingState.value = ProcessingState.Idle
            }
        }
    }
    
    /**
     * Update time selection
     */
    fun updateTimeSelection(startTimeMs: Long, endTimeMs: Long) {
        val current = _videoProject.value ?: return
        _videoProject.value = current.copy(
            startTime = startTimeMs,
            endTime = endTimeMs
        )
    }
    
    /**
     * Update aspect ratio
     */
    fun updateAspectRatio(aspectRatio: AspectRatio) {
        val current = _videoProject.value ?: return
        _videoProject.value = current.copy(
            selectedAspectRatio = aspectRatio
        )
    }
    
    /**
     * Process video with current settings
     */
    fun processVideo() {
        viewModelScope.launch {
            try {
                val project = _videoProject.value ?: return@launch
                
                val options = FFmpegProcessor.ProcessingOptions(
                    inputPath = project.videoPath,
                    startTimeMs = project.startTime,
                    endTimeMs = project.endTime,
                    aspectRatio = project.selectedAspectRatio
                )
                
                val result = ffmpegProcessor.processVideo(options) { progress, message ->
                    _processingState.postValue(ProcessingState.InProgress(progress, message))
                }
                
                result.fold(
                    onSuccess = { outputPath ->
                        _processingState.value = ProcessingState.Success(outputPath)
                        _videoProject.value = project.copy(
                            outputPath = outputPath,
                            isProcessed = true
                        )
                        
                        // Add to gallery
                        fileManager.addVideoToGallery(outputPath)
                    },
                    onFailure = { error ->
                        _processingState.value = ProcessingState.Error(error.message ?: "Processing failed")
                    }
                )
            } catch (e: Exception) {
                _processingState.value = ProcessingState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Reset project
     */
    fun resetProject() {
        _videoProject.value = null
        _processingState.value = ProcessingState.Idle
        _errorMessage.value = null
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
