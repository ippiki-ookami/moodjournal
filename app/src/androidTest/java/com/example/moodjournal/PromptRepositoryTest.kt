package com.example.moodjournal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.moodjournal.data.PromptRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PromptRepositoryTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
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
        assertTrue("prompts.json should not be empty", content.isNotEmpty())
        assertTrue("prompts.json should contain 'prompts' key", content.contains("prompts"))
    }

    @Test
    fun testPromptRepositoryLoadsPrompts() = runTest {
        val prompts = promptRepository.getAllPrompts()
        assertTrue("Should load prompts from assets", prompts.isNotEmpty())
    }

    @Test
    fun testGetTodayPrompt() = runTest {
        val todayPrompt = promptRepository.getTodayPrompt()
        assertNotNull("Should return a prompt for today", todayPrompt)
        assertTrue("Prompt text should not be empty", todayPrompt.text.isNotEmpty())
    }
}