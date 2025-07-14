package com.example.moodjournal.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class PromptSpeakerTest {

    private lateinit var context: Context
    private lateinit var promptSpeaker: PromptSpeaker
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        
        // Mock ProcessLifecycleOwner
        mockkObject(androidx.lifecycle.ProcessLifecycleOwner)
        every { androidx.lifecycle.ProcessLifecycleOwner.get() } returns mockk(relaxed = true)
        
        promptSpeaker = PromptSpeaker(context)
    }
    
    @After
    fun tearDown() {
        unmockkObject(androidx.lifecycle.ProcessLifecycleOwner)
    }

    @Test
    fun `initial isSpeaking state is false`() {
        assertFalse(promptSpeaker.isSpeaking.value)
    }

    @Test
    fun `speak sets isSpeaking to true when TTS is ready`() = runTest {
        // Given - simulate TTS ready
        promptSpeaker.onInit(TextToSpeech.SUCCESS)
        
        // When
        promptSpeaker.speak("Test prompt")
        
        // Then
        assertTrue(promptSpeaker.isSpeaking.value)
    }

    @Test
    fun `speak does nothing when TTS init failed`() = runTest {
        // Given - simulate TTS failure
        promptSpeaker.onInit(TextToSpeech.ERROR)
        
        // When
        promptSpeaker.speak("Test prompt")
        
        // Then
        assertFalse(promptSpeaker.isSpeaking.value)
    }

    @Test
    fun `stop sets isSpeaking to false`() = runTest {
        // Given
        promptSpeaker.onInit(TextToSpeech.SUCCESS)
        promptSpeaker.speak("Test")
        assertTrue(promptSpeaker.isSpeaking.value)
        
        // When
        promptSpeaker.stop()
        
        // Then
        assertFalse(promptSpeaker.isSpeaking.value)
    }

    @Test
    fun `shutdown resets speaker state`() {
        // Given
        promptSpeaker.onInit(TextToSpeech.SUCCESS)
        
        // When
        promptSpeaker.shutdown()
        
        // Then
        assertFalse(promptSpeaker.isSpeaking.value)
    }
}