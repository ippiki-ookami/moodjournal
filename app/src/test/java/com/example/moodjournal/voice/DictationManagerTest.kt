package com.example.moodjournal.voice

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class DictationManagerTest {

    private lateinit var dictationManager: DictationManager

    @Before
    fun setup() {
        dictationManager = DictationManager()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(DictationState.Idle, dictationManager.state.value)
    }

    @Test
    fun `startListening changes state to Listening`() = runTest {
        // When
        dictationManager.startListening()

        // Then
        assertTrue(dictationManager.state.value is DictationState.Listening)
    }

    @Test
    fun `stopListening changes state back to Idle`() = runTest {
        // Given
        dictationManager.startListening()
        assertTrue(dictationManager.state.value is DictationState.Listening)

        // When
        dictationManager.stopListening()

        // Then
        assertEquals(DictationState.Idle, dictationManager.state.value)
    }

    @Test
    fun `stopListening when not listening does nothing`() = runTest {
        // Given - initially Idle
        assertEquals(DictationState.Idle, dictationManager.state.value)

        // When
        dictationManager.stopListening()

        // Then
        assertEquals(DictationState.Idle, dictationManager.state.value)
    }

    @Test
    fun `setError changes state to Error`() = runTest {
        // Given
        val errorMessage = "Test error"

        // When
        dictationManager.setError(errorMessage)

        // Then
        val state = dictationManager.state.value
        assertTrue(state is DictationState.Error)
        assertEquals(errorMessage, (state as DictationState.Error).reason)
    }

    @Test
    fun `clearError changes Error state back to Idle`() = runTest {
        // Given
        dictationManager.setError("Test error")
        assertTrue(dictationManager.state.value is DictationState.Error)

        // When
        dictationManager.clearError()

        // Then
        assertEquals(DictationState.Idle, dictationManager.state.value)
    }

    @Test
    fun `setResult changes state to Result`() = runTest {
        // Given
        val resultText = "Test recognition result"

        // When
        dictationManager.setResult(resultText)

        // Then
        val state = dictationManager.state.value
        assertTrue(state is DictationState.Result)
        assertEquals(resultText, (state as DictationState.Result).text)
    }

    @Test
    fun `startListening when already listening is ignored`() = runTest {
        // Given
        dictationManager.startListening()
        val initialState = dictationManager.state.value

        // When
        dictationManager.startListening() // Second call

        // Then
        assertEquals(initialState, dictationManager.state.value)
        assertTrue(dictationManager.state.value is DictationState.Listening)
    }
}