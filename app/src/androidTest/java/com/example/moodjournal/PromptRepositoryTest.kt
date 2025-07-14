package com.example.moodjournal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.moodjournal.data.PromptRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PromptRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var promptRepository: PromptRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testPromptsJsonExists() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val inputStream = context.assets.open("prompts.json")
        val content = inputStream.bufferedReader().use { it.readText() }
        assertTrue(content.isNotEmpty(), "prompts.json should not be empty")
        assertTrue(content.contains("prompts"), "prompts.json should contain 'prompts' key")
    }

    @Test
    fun testPromptRepositoryLoadsPrompts() = runTest {
        val prompts = promptRepository.getAllPrompts()
        assertTrue(prompts.isNotEmpty(), "Should load prompts from assets")
    }

    @Test
    fun testGetTodayPrompt() = runTest {
        val todayPrompt = promptRepository.getTodayPrompt()
        assertNotNull(todayPrompt, "Should return a prompt for today")
        assertTrue(todayPrompt.text.isNotEmpty(), "Prompt text should not be empty")
    }
}