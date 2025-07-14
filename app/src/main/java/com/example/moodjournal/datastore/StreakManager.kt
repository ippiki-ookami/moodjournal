package com.example.moodjournal.datastore

import androidx.datastore.core.DataStore
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StreakManager(
    private val dataStore: DataStore<UserPrefs>
) {
    /**
     * Updates streak information after a check-in.
     * Calculates whether the streak continues, restarts, or is maintained.
     */
    suspend fun updateAfterCheckIn(today: LocalDate) {
        dataStore.updateData { currentPrefs ->
            val lastCheckInDate = if (currentPrefs.lastCheckInEpochDay > 0) {
                LocalDate.ofEpochDay(currentPrefs.lastCheckInEpochDay)
            } else {
                null
            }

            val newStreak = when {
                lastCheckInDate == null -> {
                    // First ever check-in
                    1
                }
                lastCheckInDate == today -> {
                    // Already checked in today, maintain current streak
                    currentPrefs.currentStreak
                }
                lastCheckInDate == today.minusDays(1) -> {
                    // Consecutive day, increment streak
                    currentPrefs.currentStreak + 1
                }
                else -> {
                    // Streak broken, restart at 1
                    1
                }
            }

            val newLongestStreak = maxOf(currentPrefs.longestStreak, newStreak)

            currentPrefs.toBuilder()
                .setLastCheckInEpochDay(today.toEpochDay())
                .setCurrentStreak(newStreak)
                .setLongestStreak(newLongestStreak)
                .build()
        }
    }

    /**
     * Returns a Flow of current streak statistics
     */
    fun currentStats(): Flow<StreakStats> {
        return dataStore.data.map { prefs ->
            StreakStatsMapper.map(prefs)
        }
    }

    /**
     * Checks if the current streak is still valid for the given date.
     * Useful for determining if a streak has been broken before check-in.
     */
    suspend fun isStreakActive(currentDate: LocalDate): Boolean {
        val prefs = dataStore.data.first()

        if (prefs.lastCheckInEpochDay == 0L) {
            return false
        }

        val lastCheckInDate = LocalDate.ofEpochDay(prefs.lastCheckInEpochDay)
        val daysSinceLastCheckIn = ChronoUnit.DAYS.between(lastCheckInDate, currentDate)

        return daysSinceLastCheckIn <= 1
    }

    /**
     * Gets the number of days since the last check-in
     */
    suspend fun daysSinceLastCheckIn(currentDate: LocalDate): Long? {
        val prefs = dataStore.data.first()

        if (prefs.lastCheckInEpochDay == 0L) {
            return null
        }

        val lastCheckInDate = LocalDate.ofEpochDay(prefs.lastCheckInEpochDay)
        return ChronoUnit.DAYS.between(lastCheckInDate, currentDate)
    }
}

