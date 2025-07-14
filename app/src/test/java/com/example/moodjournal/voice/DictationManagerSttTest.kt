package com.example.moodjournal.voice

import android.content.Context
import android.os.Bundle
import android.speech.SpeechRecognizer
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class DictationManagerSttTest {

    private lateinit var context: Context
    private lateinit var dictationManager: DictationManager
    private lateinit var mockRecognizer: SpeechRecognizer

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        mockRecognizer = mockk(relaxed = true)
        
        mockkStatic(SpeechRecognizer::class)
        every { SpeechRecognizer.isRecognitionAvailable(context) } returns true
        every { SpeechRecognizer.createSpeechRecognizer(context) } returns mockRecognizer
        
        dictationManager = DictationManager(context)
    }

    @Test
    fun `startListening creates recognizer and starts listening when available`() = runTest {
        // When
        dictationManager.startListening()

        // Then
        verify { mockRecognizer.setRecognitionListener(dictationManager) }
        verify { mockRecognizer.startListening(any()) }
        assertTrue(dictationManager.state.value is DictationState.Listening)
    }

    @Test
    fun `startListening sets error when recognition not available`() = runTest {
        // Given
        every { SpeechRecognizer.isRecognitionAvailable(context) } returns false

        // When
        dictationManager.startListening()

        // Then
        val state = dictationManager.state.value
        assertTrue(state is DictationState.Error)
        assertEquals("Speech recognition not available on this device", (state as DictationState.Error).reason)
    }

    @Test
    fun `onResults emits Result state with recognized text`() = runTest {
        // Given
        val expectedText = "I feel great today"
        val bundle = Bundle().apply {
            putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(expectedText, "alternative"))
        }

        // When
        dictationManager.onResults(bundle)

        // Then
        val state = dictationManager.state.value
        assertTrue(state is DictationState.Result)
        assertEquals(expectedText, (state as DictationState.Result).text)
    }

    @Test
    fun `onError maps error codes to user friendly messages`() = runTest {
        // Test network error
        dictationManager.onError(SpeechRecognizer.ERROR_NETWORK)
        var state = dictationManager.state.value
        assertTrue(state is DictationState.Error)
        assertEquals("Network error. Please check your connection.", (state as DictationState.Error).reason)

        // Test no match error
        dictationManager.clearError()
        dictationManager.onError(SpeechRecognizer.ERROR_NO_MATCH)
        state = dictationManager.state.value
        assertTrue(state is DictationState.Error)
        assertEquals("Couldn't understand. Please try again.", (state as DictationState.Error).reason)

        // Test permission error
        dictationManager.clearError()
        dictationManager.onError(SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)
        state = dictationManager.state.value
        assertTrue(state is DictationState.Error)
        assertEquals("Microphone permission required", (state as DictationState.Error).reason)
    }

    @Test
    fun `stopListening cancels recognition and changes state to Idle`() = runTest {
        // Given
        dictationManager.startListening()
        assertTrue(dictationManager.state.value is DictationState.Listening)

        // When
        dictationManager.stopListening()

        // Then
        verify { mockRecognizer.stopListening() }
        verify { mockRecognizer.cancel() }
        assertEquals(DictationState.Idle, dictationManager.state.value)
    }

    @Test
    fun `onPartialResults logs partial text`() = runTest {
        // Given
        val partialText = "I feel"
        val bundle = Bundle().apply {
            putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(partialText))
        }

        // When
        dictationManager.onPartialResults(bundle)

        // Then - just verify no crash, state remains unchanged
        // Partial results are logged but don't change state
        assertTrue(dictationManager.state.value is DictationState.Idle)
    }

    @Test
    fun `destroy cleans up speech recognizer`() = runTest {
        // Given
        dictationManager.startListening()

        // When
        dictationManager.destroy()

        // Then
        verify { mockRecognizer.destroy() }
    }
}