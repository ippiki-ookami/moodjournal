package com.example.moodjournal.datastore

import java.time.LocalDate

data class StreakStats(
    val current: Int,
    val longest: Int,
    val lastDate: LocalDate?
)

