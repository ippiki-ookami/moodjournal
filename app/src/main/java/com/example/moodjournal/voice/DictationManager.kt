package com.example.moodjournal.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictationManager @Inject constructor(
    @ApplicationContext private val context: Context
) : RecognitionListener {
    
    companion object {
        private const val TAG = "DictationManager"
    }

    private val _state = MutableStateFlow<DictationState>(DictationState.Idle)
    val state: StateFlow<DictationState> = _state.asStateFlow()
    
    private var speechRecognizer: SpeechRecognizer? = null
    private val recognizerIntent: Intent by lazy {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var startTime: Long = 0

    fun startListening() {
        Log.d(TAG, "startListening() called")
        if (_state.value is DictationState.Listening) {
            Log.w(TAG, "Already listening, ignoring start request")
            return
        }
        
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognition not available")
            setError("Speech recognition not available on this device")
            return
        }
        
        startTime = System.currentTimeMillis()
        
        scope.launch {
            try {
                initializeRecognizer()
                _state.value = DictationState.Listening
                Log.d(TAG, "State changed to Listening")
                speechRecognizer?.startListening(recognizerIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start listening", e)
                setError("Failed to start voice input")
            }
        }
    }

    fun stopListening() {
        Log.d(TAG, "stopListening() called")
        if (_state.value !is DictationState.Listening) {
            Log.w(TAG, "Not currently listening, ignoring stop request")
            return
        }
        
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        _state.value = DictationState.Idle
        Log.d(TAG, "State changed to Idle")
    }

    fun clearError() {
        Log.d(TAG, "clearError() called")
        if (_state.value is DictationState.Error) {
            _state.value = DictationState.Idle
            Log.d(TAG, "Error cleared, state changed to Idle")
        }
    }

    // Internal method to set error state (will be used in #20)
    internal fun setError(reason: String) {
        Log.e(TAG, "Error occurred: $reason")
        _state.value = DictationState.Error(reason)
    }

    // Internal method to set result
    internal fun setResult(text: String) {
        val elapsed = System.currentTimeMillis() - startTime
        Log.d(TAG, "Recognition result received: $text (elapsed: ${elapsed}ms)")
        _state.value = DictationState.Result(text)
    }
    
    private fun initializeRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@DictationManager)
            }
        }
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
    
    // RecognitionListener implementation
    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Could use this for visual feedback
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // Not used
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech")
    }

    override fun onError(error: Int) {
        Log.e(TAG, "Recognition error: $error")
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
            SpeechRecognizer.ERROR_NETWORK -> "Network error. Please check your connection."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_SERVER -> "Server error. Please try again."
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
            SpeechRecognizer.ERROR_NO_MATCH -> "Couldn't understand. Please try again."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
            else -> "Recognition error"
        }
        setError(errorMessage)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val text = matches[0]
            setResult(text)
        } else {
            Log.w(TAG, "No recognition results")
            _state.value = DictationState.Idle
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        // Could show partial results for better UX
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            Log.d(TAG, "Partial result: ${matches[0]}")
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "onEvent: $eventType")
    }
}

sealed class DictationState {
    object Idle : DictationState()
    object Listening : DictationState()
    data class Error(val reason: String) : DictationState()
    data class Result(val text: String) : DictationState()
}