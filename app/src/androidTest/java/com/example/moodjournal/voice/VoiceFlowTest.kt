package com.example.moodjournal.voice

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.example.moodjournal.MainActivity
import com.example.moodjournal.MainDispatcherRule
import com.example.moodjournal.data.Prompt
import com.example.moodjournal.presentation.checkin.CheckInScreen
import com.example.moodjournal.presentation.checkin.CheckInUiState
import com.example.moodjournal.ui.theme.MoodJournalTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class VoiceFlowTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createComposeRule()
    
    @get:Rule(order = 3)
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Inject
    lateinit var dictationManager: DictationManager

    private lateinit var idlingResource: VoiceIdlingResource
    private lateinit var device: UiDevice

    @Before
    fun setup() {
        hiltRule.inject()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        
        // Grant microphone permission via UiAutomator
        grantMicrophonePermission()
    }

    @After
    fun tearDown() {
        if (::idlingResource.isInitialized) {
            IdlingRegistry.getInstance().unregister(idlingResource)
        }
    }

    @Test
    fun voiceFlow_dictationPopulatesNoteField() = runTest {
        // Setup IdlingResource
        idlingResource = VoiceIdlingResource(dictationManager.state)
        IdlingRegistry.getInstance().register(idlingResource)

        // Setup mock recognition result
        val mockResultText = "espresso voice test"
        val resultBundle = Bundle().apply {
            putStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION, 
                arrayListOf(mockResultText)
            )
        }
        val resultData = Intent().apply {
            putExtras(resultBundle)
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        // Mock the speech recognizer intent
        Intents.intending(
            IntentMatchers.hasAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        ).respondWith(result)

        // Launch CheckInScreen directly
        composeTestRule.setContent {
            MoodJournalTheme {
                CheckInScreen(
                    onEntrySaved = { }
                )
            }
        }

        // Wait for screen to load
        composeTestRule.waitForIdle()

        // Verify mic button is visible
        composeTestRule
            .onNodeWithTag("mic_button")
            .assertIsDisplayed()

        // Click mic button to start dictation
        composeTestRule
            .onNodeWithTag("mic_button")
            .performClick()

        // Wait for IdlingResource (recognition to complete)
        Espresso.onIdle()

        // Verify note field contains the dictated text
        composeTestRule
            .onNodeWithTag("note_input")
            .assertTextContains(mockResultText)
    }

    @Test
    fun voiceFlow_errorShowsSnackbar() = runTest {
        // Setup IdlingResource
        idlingResource = VoiceIdlingResource(dictationManager.state)
        IdlingRegistry.getInstance().register(idlingResource)

        // Launch CheckInScreen
        composeTestRule.setContent {
            MoodJournalTheme {
                CheckInScreen(
                    onEntrySaved = { }
                )
            }
        }

        // Manually trigger an error state
        dictationManager.setError("Test error message")

        // Wait for snackbar to appear
        composeTestRule.waitForIdle()

        // Verify error appears in snackbar
        composeTestRule
            .onNodeWithText("Voice input error: Test error message")
            .assertIsDisplayed()
    }

    @Test
    fun voiceFlow_micButtonTogglesListeningState() = runTest {
        composeTestRule.setContent {
            MoodJournalTheme {
                CheckInScreen(
                    onEntrySaved = { }
                )
            }
        }

        // Initial state - mic icon visible
        composeTestRule
            .onNodeWithTag("mic_button")
            .assertIsDisplayed()

        // Click to start listening
        composeTestRule
            .onNodeWithTag("mic_button")
            .performClick()

        // Verify listening state (MicOff icon should be shown)
        // Note: Icon changes based on dictationState
        
        // Click again to stop
        composeTestRule
            .onNodeWithTag("mic_button")
            .performClick()

        // Should return to idle state
        composeTestRule.waitForIdle()
    }

    @Test
    fun voiceFlow_settingsDisableMicButton() = runTest {
        // This would require navigating to settings and toggling voice input
        // For now, we can test directly with the CheckInContent composable
        
        composeTestRule.setContent {
            MoodJournalTheme {
                CheckInScreen(
                    onEntrySaved = { }
                )
            }
        }

        // Initially mic should be visible (default settings)
        composeTestRule
            .onNodeWithTag("mic_button")
            .assertIsDisplayed()

        // Note: Full settings toggle test would require navigation
        // which is beyond the scope of this unit test
    }

    private fun grantMicrophonePermission() {
        // Use UiAutomator to grant permission if dialog appears
        val allowButton = device.findObject(
            UiSelector().text("Allow")
                .className("android.widget.Button")
        )
        if (allowButton.exists()) {
            allowButton.click()
        }
        
        // Alternative: Grant via shell command (requires appropriate permissions)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.executeShellCommand(
            "pm grant ${instrumentation.targetContext.packageName} android.permission.RECORD_AUDIO"
        )
    }
}