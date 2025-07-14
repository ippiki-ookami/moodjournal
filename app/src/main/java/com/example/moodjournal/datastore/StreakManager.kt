package com.example.moodjournal.datastore

import androidx.datastore.core.DataStore
import com.example.moodjournal.UserPrefs
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastCheckIn: LocalDate?
)

class StreakManager @Inject constructor(
    private val dataStore: DataStore<UserPrefs>
) {
    val currentStats: Flow<StreakStats> = dataStore.data.map { prefs ->
        StreakStats(
            currentStreak = prefs.currentStreak,
            longestStreak = prefs.longestStreak,
            lastCheckIn = if (prefs.lastCheckInEpochDay > 0) {
                LocalDate.ofEpochDay(prefs.lastCheckInEpochDay)
            } else {
                null
            }
        )
    }

    suspend fun updateAfterCheckIn() {
        val today = LocalDate.now()
        val todayEpochDay = today.toEpochDay()

        dataStore.updateData { prefs ->
            val lastCheckInDate = if (prefs.lastCheckInEpochDay > 0) {
                LocalDate.ofEpochDay(prefs.lastCheckInEpochDay)
            } else {
                null
            }

            val newStreak = when {
                lastCheckInDate == null -> 1
                lastCheckInDate == today -> prefs.currentStreak
                lastCheckInDate == today.minusDays(1) -> prefs.currentStreak + 1
                else -> 1
            }

            prefs.toBuilder()
                .setLastCheckInEpochDay(todayEpochDay)
                .setCurrentStreak(newStreak)
                .setLongestStreak(maxOf(prefs.longestStreak, newStreak))
                .build()
        }
    }

    suspend fun isProUnlocked(): Boolean {
        return dataStore.data.first().proUnlocked
    }

    suspend fun unlockPro() {
        dataStore.updateData { prefs ->
            prefs.toBuilder().setProUnlocked(true).build()
        }
    }
}
