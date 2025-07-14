package com.example.moodjournal.presentation.checkin

sealed interface CheckInEvent {
    data class OnMoodSelected(val mood: Int) : CheckInEvent
    data class OnNoteChanged(val text: String) : CheckInEvent
    data class OnTagsChanged(val tags: List<String>) : CheckInEvent
    data object OnSaveTapped : CheckInEvent
    data object OnNavigateConsumed : CheckInEvent // optional helper to reset Saved→Ready
}
