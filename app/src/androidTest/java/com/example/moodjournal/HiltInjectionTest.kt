package com.example.moodjournal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moodjournal.data.EntryDao
import com.example.moodjournal.data.PromptRepository
import com.example.moodjournal.datastore.StreakManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HiltInjectionTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var entryDao: EntryDao

    @Inject
    lateinit var streakManager: StreakManager

    @Inject
    lateinit var promptRepository: PromptRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testDependenciesInjected() {
        assertNotNull("EntryDao should be injected", entryDao)
        assertNotNull("StreakManager should be injected", streakManager)
        assertNotNull("PromptRepository should be injected", promptRepository)
    }

    @Test
    fun testPromptRepositoryWorks() = runTest {
        val prompts = promptRepository.getAllPrompts()
        assertTrue("Should have prompts loaded", prompts.isNotEmpty())
        assertTrue("Should have 365 prompts", prompts.size == 365)
    }
}
