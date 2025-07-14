package com.example.moodjournal.voice

import androidx.test.espresso.IdlingResource
import kotlinx.coroutines.flow.StateFlow

/**
 * IdlingResource that waits for DictationManager state changes.
 * Used to synchronize Espresso tests with async speech recognition.
 */
class VoiceIdlingResource(
    private val dictationStateFlow: StateFlow<DictationState>
) : IdlingResource {
    
    private var callback: IdlingResource.ResourceCallback? = null
    
    override fun getName(): String = "VoiceIdlingResource"
    
    override fun isIdleNow(): Boolean {
        val state = dictationStateFlow.value
        val isIdle = state !is DictationState.Listening
        
        if (isIdle) {
            callback?.onTransitionToIdle()
        }
        
        return isIdle
    }
    
    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}