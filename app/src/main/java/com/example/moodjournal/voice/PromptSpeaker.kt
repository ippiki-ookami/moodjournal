package com.example.moodjournal.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptSpeaker @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener, DefaultLifecycleObserver {

    companion object {
        private const val TAG = "PromptSpeaker"
    }

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        // Register lifecycle observer to handle cleanup
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            // Set language to US English with fallback to default
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "US English not supported, using default voice")
                // Try default locale
                tts?.setLanguage(Locale.getDefault())
            }
            
            // Set utterance progress listener
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d(TAG, "TTS started: $utteranceId")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d(TAG, "TTS completed: $utteranceId")
                    stop()
                }

                override fun onError(utteranceId: String?) {
                    Log.e(TAG, "TTS error: $utteranceId")
                    stop()
                }

                @Deprecated("Deprecated in API 29")
                override fun onError(utteranceId: String?, errorCode: Int) {
                    Log.e(TAG, "TTS error: $utteranceId, code: $errorCode")
                    stop()
                }
            })
        } else {
            Log.e(TAG, "TTS initialization failed with status: $status")
        }
    }

    suspend fun speak(text: String) = withContext(Dispatchers.Main) {
        val startTime = System.currentTimeMillis()
        
        if (!ready) {
            Log.d(TAG, "TTS not ready, initializing...")
            initTTS()
        }
        
        if (!ready) {
            Log.e(TAG, "TTS initialization failed, cannot speak")
            return@withContext
        }
        
        _isSpeaking.value = true
        val utteranceId = "prompt_${System.currentTimeMillis()}"
        
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        
        val latency = System.currentTimeMillis() - startTime
        Log.d(TAG, "TTS speak latency: ${latency}ms")
        
        if (result != TextToSpeech.SUCCESS) {
            Log.e(TAG, "TTS speak failed with result: $result")
            stop()
        }
    }

    private fun initTTS() {
        if (tts == null) {
            tts = TextToSpeech(context, this)
        }
    }

    fun stop() {
        Log.d(TAG, "Stopping TTS")
        _isSpeaking.value = false
        tts?.stop()
    }

    fun shutdown() {
        Log.d(TAG, "Shutting down TTS")
        stop()
        tts?.shutdown()
        tts = null
        ready = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        shutdown()
    }
}