package com.videoeditor.data.models

data class VideoProject(
    val videoPath: String,
    val videoUri: String? = null,
    val isYouTubeVideo: Boolean = false,
    val youtubeUrl: String? = null,
    val videoTitle: String? = null,
    val videoDuration: Long = 0, // in milliseconds
    val thumbnailPath: String? = null,
    val startTime: Long = 0, // in milliseconds
    val endTime: Long = 0, // in milliseconds
    val selectedAspectRatio: AspectRatio = AspectRatio.VERTICAL_9_16,
    val outputPath: String? = null,
    val isProcessing: Boolean = false,
    val isProcessed: Boolean = false
)
