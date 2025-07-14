package com.example.moodjournal.data

/**
 * Represents a daily prompt for mood journaling
 */
data class Prompt(
    val id: String,
    val text: String,
    val pack: String
)
