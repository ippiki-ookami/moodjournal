package com.example.moodjournal

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CheckInFlowTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun checkInFlow_savesEntryAndShowsInTimeline() = runTest {
        // Wait for splash screen to pass and check-in screen to appear
        composeTestRule.waitForIdle()
        
        // First wait for splash to complete (1500ms + buffer)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule
                    .onNodeWithTag("prompt_text")
                    .fetchSemanticsNode()
                true
            } catch (e: Exception) {
                // Check if we're still on splash or in error state
                try {
                    // Look for any text that might indicate the current state
                    composeTestRule.onNodeWithText("Mood Journal").fetchSemanticsNode()
                    // Still on splash screen
                    false
                } catch (splashException: Exception) {
                    // Not on splash, might be in loading or error state
                    false
                }
            }
        }

        // Print the current UI tree for debugging
        composeTestRule.onRoot().printToLog("CheckInTest")

        // Verify we're on the check-in screen by looking for a prompt
        composeTestRule
            .onNodeWithTag("prompt_text")
            .assertIsDisplayed()

        // Select mood #4 (happy)
        composeTestRule
            .onNodeWithTag("mood_button_4")
            .performClick()

        // Type note
        val testNote = "Espresso entry"
        composeTestRule
            .onNodeWithTag("note_input")
            .performTextInput(testNote)

        // Click save button
        composeTestRule
            .onNodeWithTag("save_button")
            .assertIsDisplayed()
            .performClick()

        // Wait for navigation to timeline
        composeTestRule.waitForIdle()
        
        // Wait for Timeline header to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onNodeWithText("Timeline")
                .fetchSemanticsNode().let { true }
        }

        // Verify we're on timeline screen
        composeTestRule
            .onNodeWithText("Timeline")
            .assertIsDisplayed()

        // Verify our entry appears in the list
        composeTestRule
            .onNodeWithText(testNote)
            .assertIsDisplayed()
    }

    @Test
    fun checkInFlow_requiresMoodBeforeSaving() = runTest {
        // Wait for splash screen to pass and check-in screen to appear
        composeTestRule.waitForIdle()
        
        // Wait for the note input to be available (indicates we're on check-in screen)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onNodeWithTag("note_input")
                .fetchSemanticsNode().let { true }
        }

        // Type note without selecting mood
        composeTestRule
            .onNodeWithTag("note_input")
            .performTextInput("Test note without mood")

        // Verify save button is not displayed (mood is required)
        composeTestRule
            .onNodeWithTag("save_button")
            .assertDoesNotExist()

        // Select a mood
        composeTestRule
            .onNodeWithTag("mood_button_3")
            .performClick()

        // Now save button should appear
        composeTestRule
            .onNodeWithTag("save_button")
            .assertIsDisplayed()
    }
}
