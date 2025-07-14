package com.example.moodjournal.datastore

import androidx.datastore.core.DataStore
import app.cash.turbine.test
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StreakManagerTest {

    private lateinit var testDataStore: TestDataStore
    private lateinit var streakManager: StreakManager
    private lateinit var testScheduler: TestCoroutineScheduler

    @Before
    fun setup() {
        testScheduler = TestCoroutineScheduler()
        testDataStore = TestDataStore()
        streakManager = StreakManager(testDataStore)
    }

    @Test
    fun `first ever check-in starts streak at 1`() = runTest {
        // Given
        val today = LocalDate.of(2025, 7, 14)

        // When
        streakManager.updateAfterCheckIn(today)

        // Then
        streakManager.currentStats().test {
            val stats = awaitItem()
            assertEquals(1, stats.current)
            assertEquals(1, stats.longest)
            assertEquals(today, stats.lastDate)
        }
    }

    @Test
    fun `continuing streak increments current streak`() = runTest {
        // Given
        val day1 = LocalDate.of(2025, 7, 14)
        val day2 = LocalDate.of(2025, 7, 15)
        val day3 = LocalDate.of(2025, 7, 16)

        // When
        streakManager.updateAfterCheckIn(day1)
        streakManager.updateAfterCheckIn(day2)
        streakManager.updateAfterCheckIn(day3)

        // Then
        streakManager.currentStats().test {
            val stats = awaitItem()
            assertEquals(3, stats.current)
            assertEquals(3, stats.longest)
            assertEquals(day3, stats.lastDate)
        }
    }

    @Test
    fun `missing 2 days then resuming resets current streak but keeps longest`() = runTest {
        // Given - build up a 5 day streak
        val startDate = LocalDate.of(2025, 7, 10)
        for (i in 0..4) {
            streakManager.updateAfterCheckIn(startDate.plusDays(i.toLong()))
        }

        // Skip 2 days
        val resumeDate = startDate.plusDays(7) // July 17th (missed 15th and 16th)

        // When
        streakManager.updateAfterCheckIn(resumeDate)

        // Then
        streakManager.currentStats().test {
            val stats = awaitItem()
            assertEquals(1, stats.current) // Streak reset to 1
            assertEquals(5, stats.longest) // Longest streak preserved
            assertEquals(resumeDate, stats.lastDate)
        }
    }

    @Test
    fun `checking in on same day maintains streak`() = runTest {
        // Given
        val today = LocalDate.of(2025, 7, 14)
        streakManager.updateAfterCheckIn(today)

        // When - check in again on same day
        streakManager.updateAfterCheckIn(today)

        // Then
        streakManager.currentStats().test {
            val stats = awaitItem()
            assertEquals(1, stats.current) // Streak stays at 1
            assertEquals(1, stats.longest)
            assertEquals(today, stats.lastDate)
        }
    }

    @Test
    fun `isStreakActive returns true for consecutive days`() = runTest {
        // Given
        val yesterday = LocalDate.of(2025, 7, 13)
        val today = LocalDate.of(2025, 7, 14)

        streakManager.updateAfterCheckIn(yesterday)

        // When & Then
        assertTrue(streakManager.isStreakActive(today))
    }

    @Test
    fun `isStreakActive returns false after missing a day`() = runTest {
        // Given
        val twoDaysAgo = LocalDate.of(2025, 7, 12)
        val today = LocalDate.of(2025, 7, 14)

        streakManager.updateAfterCheckIn(twoDaysAgo)

        // When & Then
        assertFalse(streakManager.isStreakActive(today))
    }

    @Test
    fun `daysSinceLastCheckIn returns null for first time user`() = runTest {
        // Given
        val today = LocalDate.of(2025, 7, 14)

        // When & Then
        assertNull(streakManager.daysSinceLastCheckIn(today))
    }

    @Test
    fun `daysSinceLastCheckIn returns correct number of days`() = runTest {
        // Given
        val checkInDate = LocalDate.of(2025, 7, 10)
        val currentDate = LocalDate.of(2025, 7, 14)

        streakManager.updateAfterCheckIn(checkInDate)

        // When
        val daysSince = streakManager.daysSinceLastCheckIn(currentDate)

        // Then
        assertEquals(4L, daysSince)
    }

    /**
     * Test implementation of DataStore for unit testing
     */
    private class TestDataStore : DataStore<UserPrefs> {
        private val prefsFlow = MutableStateFlow(UserPrefs.getDefaultInstance())

        override val data = prefsFlow

        override suspend fun updateData(transform: suspend (t: UserPrefs) -> UserPrefs): UserPrefs {
            val newValue = transform(prefsFlow.value)
            prefsFlow.value = newValue
            return newValue
        }
    }
}

