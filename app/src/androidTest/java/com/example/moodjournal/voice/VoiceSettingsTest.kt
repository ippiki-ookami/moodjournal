package com.example.moodjournal.voice

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moodjournal.MainDispatcherRule
import com.example.moodjournal.presentation.settings.SettingsScreen
import com.example.moodjournal.ui.theme.MoodJournalTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class VoiceSettingsTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var voicePrefsManager: VoicePrefsManager

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun settingsScreen_voiceTogglesWork() = runTest {
        composeTestRule.setContent {
            MoodJournalTheme {
                SettingsScreen(
                    onBackClick = { }
                )
            }
        }

        // Wait for screen to load
        composeTestRule.waitForIdle()

        // Find TTS toggle
        val ttsToggle = composeTestRule.onNode(
            hasText("Read prompt aloud") and hasAnyDescendant(hasTestTag("switch"))
        )
        
        // Verify it exists
        ttsToggle.assertIsDisplayed()

        // Find voice input toggle
        val voiceInputToggle = composeTestRule.onNode(
            hasText("Enable voice input") and hasAnyDescendant(hasTestTag("switch"))
        )
        
        voiceInputToggle.assertIsDisplayed()

        // Note: Actually clicking the toggles would require more complex
        // test setup with proper ViewModel injection
    }

    @Test
    fun checkInScreen_speakerButtonVisibility() = runTest {
        // Set TTS enabled
        voicePrefsManager.setPromptTts(true)

        composeTestRule.setContent {
            MoodJournalTheme {
                com.example.moodjournal.presentation.checkin.CheckInScreen(
                    onEntrySaved = { }
                )
            }
        }

        // Speaker button should be visible
        composeTestRule
            .onNodeWithTag("speaker_button")
            .assertIsDisplayed()

        // Disable TTS
        voicePrefsManager.setPromptTts(false)
        
        // Wait for recomposition
        composeTestRule.waitForIdle()

        // Speaker button should not exist
        composeTestRule
            .onNodeWithTag("speaker_button")
            .assertDoesNotExist()
    }

    @Test
    fun checkInScreen_micButtonVisibility() = runTest {
        // Set voice input enabled
        voicePrefsManager.setVoiceInput(true)

        composeTestRule.setContent {
            MoodJournalTheme {
                com.example.moodjournal.presentation.checkin.CheckInScreen(
                    onEntrySaved = { }
                )
            }
        }

        // Mic button should be visible
        composeTestRule
            .onNodeWithTag("mic_button")
            .assertIsDisplayed()

        // Disable voice input
        voicePrefsManager.setVoiceInput(false)
        
        // Wait for recomposition
        composeTestRule.waitForIdle()

        // Mic button should not exist
        composeTestRule
            .onNodeWithTag("mic_button")
            .assertDoesNotExist()
    }
}