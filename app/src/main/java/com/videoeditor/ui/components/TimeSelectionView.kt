package com.videoeditor.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.slider.RangeSlider
import com.videoeditor.databinding.ViewTimeSelectionBinding
import com.videoeditor.utils.VideoUtils

class TimeSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    private val binding: ViewTimeSelectionBinding
    private val videoUtils = VideoUtils(context)
    
    private var videoDuration: Long = 0
    private var onTimeRangeChanged: ((Long, Long) -> Unit)? = null
    
    init {
        binding = ViewTimeSelectionBinding.inflate(LayoutInflater.from(context), this, true)
        orientation = VERTICAL
        setupSlider()
    }
    
    private fun setupSlider() {
        binding.rangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val startTime = values[0].toLong()
            val endTime = values[1].toLong()
            
            updateTimeLabels(startTime, endTime)
            onTimeRangeChanged?.invoke(startTime, endTime)
        }
    }
    
    fun setVideoDuration(durationMs: Long) {
        videoDuration = durationMs
        binding.rangeSlider.valueTo = durationMs.toFloat()
        binding.rangeSlider.values = listOf(0f, durationMs.toFloat())
        updateTimeLabels(0, durationMs)
    }
    
    fun setTimeRange(startMs: Long, endMs: Long) {
        binding.rangeSlider.values = listOf(startMs.toFloat(), endMs.toFloat())
        updateTimeLabels(startMs, endMs)
    }
    
    fun getStartTime(): Long {
        return binding.rangeSlider.values[0].toLong()
    }
    
    fun getEndTime(): Long {
        return binding.rangeSlider.values[1].toLong()
    }
    
    fun setOnTimeRangeChangedListener(listener: (Long, Long) -> Unit) {
        onTimeRangeChanged = listener
    }
    
    private fun updateTimeLabels(startMs: Long, endMs: Long) {
        binding.tvStartTime.text = videoUtils.formatTime(startMs)
        binding.tvEndTime.text = videoUtils.formatTime(endMs)
        
        val duration = endMs - startMs
        binding.tvDuration.text = "Duration: ${videoUtils.formatTime(duration)}"
    }
}
