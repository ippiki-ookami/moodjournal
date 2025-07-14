package com.example.moodjournal.voice

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Basic audio player for voice recordings.
 * This is a minimal implementation for VOICE-03 requirements.
 * Full playback features will be implemented in future sprints.
 */
@Singleton
class AudioPlayer @Inject constructor() {
    
    companion object {
        private const val TAG = "AudioPlayer"
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    fun playAudio(filePath: String) {
        try {
            stop() // Stop any existing playback
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                setOnPreparedListener { 
                    start()
                    _isPlaying.value = true
                    Log.d(TAG, "Audio playback started: $filePath")
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    Log.d(TAG, "Audio playback completed")
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Playback error: what=$what, extra=$extra")
                    _isPlaying.value = false
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio", e)
            _isPlaying.value = false
        }
    }
    
    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        _isPlaying.value = false
    }
    
    fun pause() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                _isPlaying.value = false
            }
        }
    }
    
    fun resume() {
        mediaPlayer?.apply {
            if (!isPlaying) {
                start()
                _isPlaying.value = true
            }
        }
    }
}