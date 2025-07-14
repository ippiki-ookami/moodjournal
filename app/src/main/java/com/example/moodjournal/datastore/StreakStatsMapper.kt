package com.example.moodjournal.datastore

import java.time.LocalDate

object StreakStatsMapper {
    fun map(userPrefs: UserPrefs): StreakStats {
        val lastDate = if (userPrefs.lastCheckInEpochDay > 0) {
            LocalDate.ofEpochDay(userPrefs.lastCheckInEpochDay)
        } else {
            null
        }

        return StreakStats(
            current = userPrefs.currentStreak,
            longest = userPrefs.longestStreak,
            lastDate = lastDate
        )
    }
}

