package com.videoeditor.data.models

sealed class ProcessingState {
    object Idle : ProcessingState()
    data class InProgress(val progress: Int, val message: String) : ProcessingState()
    data class Success(val outputPath: String) : ProcessingState()
    data class Error(val message: String) : ProcessingState()
}
