package com.example.moodjournal.voice

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictationManager @Inject constructor() {
    
    companion object {
        private const val TAG = "DictationManager"
    }

    private val _state = MutableStateFlow<DictationState>(DictationState.Idle)
    val state: StateFlow<DictationState> = _state.asStateFlow()

    fun startListening() {
        Log.d(TAG, "startListening() called")
        if (_state.value is DictationState.Listening) {
            Log.w(TAG, "Already listening, ignoring start request")
            return
        }
        
        _state.value = DictationState.Listening
        Log.d(TAG, "State changed to Listening")
        
        // TODO: Implement actual speech recognition in issue #20
        // For now, this is just a scaffold that sets the state
    }

    fun stopListening() {
        Log.d(TAG, "stopListening() called")
        if (_state.value !is DictationState.Listening) {
            Log.w(TAG, "Not currently listening, ignoring stop request")
            return
        }
        
        _state.value = DictationState.Idle
        Log.d(TAG, "State changed to Idle")
        
        // TODO: Implement actual speech recognition cleanup in issue #20
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

    // Internal method to set result (will be used in #20)
    internal fun setResult(text: String) {
        Log.d(TAG, "Recognition result received: $text")
        _state.value = DictationState.Result(text)
    }
}

sealed class DictationState {
    object Idle : DictationState()
    object Listening : DictationState()
    data class Error(val reason: String) : DictationState()
    data class Result(val text: String) : DictationState()
}