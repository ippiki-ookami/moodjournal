package com.example.moodjournal.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodjournal.data.Entry
import com.example.moodjournal.data.EntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TimelineViewModel @Inject constructor(
    entryDao: EntryDao
) : ViewModel() {

    val entries: StateFlow<List<Entry>> = entryDao
        .observeRange(
            start = LocalDate.now().minusDays(90),
            end = LocalDate.now()
        )
        .map { entries ->
            entries.sortedByDescending { it.date }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
