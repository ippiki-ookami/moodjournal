package com.example.moodjournal.presentation.checkin

import com.example.moodjournal.data.Prompt
import java.time.LocalDate

sealed interface CheckInUiState {

    /** App is fetching today's prompt or loading DB prefs. */
    data object Loading : CheckInUiState

    /** UI is interactive. */
    data class Ready(
        val prompt: Prompt,
        val mood: Int? = null, // 1-5 emoji scale; null until user taps
        val note: String = "",
        val tags: List<String> = emptyList()
    ) : CheckInUiState

    /** User pressed save; entry being persisted. */
    data object Saving : CheckInUiState

    /** DB write succeeded; contains the entry's primary-key date for possible nav. */
    data class Saved(val entryId: LocalDate) : CheckInUiState
}
