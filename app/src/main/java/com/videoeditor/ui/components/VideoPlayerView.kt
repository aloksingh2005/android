package com.videoeditor.ui.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.videoeditor.databinding.ViewVideoPlayerBinding

class VideoPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    
    private val binding: ViewVideoPlayerBinding
    private var player: ExoPlayer? = null
    private var onProgressListener: ((Long) -> Unit)? = null
    
    init {
        binding = ViewVideoPlayerBinding.inflate(LayoutInflater.from(context), this, true)
        initializePlayer()
    }
    
    private fun initializePlayer() {
        player = ExoPlayer.Builder(context).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            
            // Add listener for progress updates
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        startProgressTracking()
                    }
                }
            })
        }
    }
    
    fun setVideoUri(uri: Uri) {
        player?.apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
        }
    }
    
    fun setVideoPath(path: String) {
        setVideoUri(Uri.parse(path))
    }
    
    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }
    
    fun play() {
        player?.play()
    }
    
    fun pause() {
        player?.pause()
    }
    
    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }
    
    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }
    
    fun getDuration(): Long {
        return player?.duration ?: 0
    }
    
    fun setOnProgressListener(listener: (Long) -> Unit) {
        onProgressListener = listener
    }
    
    private fun startProgressTracking() {
        handler.post(object : Runnable {
            override fun run() {
                onProgressListener?.invoke(getCurrentPosition())
                if (isPlaying()) {
                    handler.postDelayed(this, 100)
                }
            }
        })
    }
    
    fun release() {
        player?.release()
        player = null
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }
}
