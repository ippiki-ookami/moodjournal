package com.example.moodjournal.presentation.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodjournal.data.Entry
import com.example.moodjournal.data.EntryDao
import com.example.moodjournal.data.PromptRepository
import com.example.moodjournal.datastore.StreakManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val promptRepository: PromptRepository,
    private val entryDao: EntryDao,
    private val streakManager: StreakManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckInUiState>(CheckInUiState.Loading)
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadTodayPrompt()
    }

    private fun loadTodayPrompt() {
        viewModelScope.launch {
            try {
                val prompt = promptRepository.getTodayPrompt()
                _uiState.value = CheckInUiState.Ready(prompt = prompt)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load prompt: ${e.message}"
            }
        }
    }

    fun onEvent(event: CheckInEvent) {
        when (event) {
            is CheckInEvent.OnMoodSelected -> handleMoodSelected(event.mood)
            is CheckInEvent.OnNoteChanged -> handleNoteChanged(event.text)
            is CheckInEvent.OnTagsChanged -> handleTagsChanged(event.tags)
            is CheckInEvent.OnSaveTapped -> handleSave()
            is CheckInEvent.OnNavigateConsumed -> handleNavigateConsumed()
        }
    }

    private fun handleMoodSelected(mood: Int) {
        val currentState = _uiState.value
        if (currentState is CheckInUiState.Ready) {
            _uiState.value = currentState.copy(mood = mood)
        }
    }

    private fun handleNoteChanged(text: String) {
        val currentState = _uiState.value
        if (currentState is CheckInUiState.Ready) {
            _uiState.value = currentState.copy(note = text)
        }
    }

    private fun handleTagsChanged(tags: List<String>) {
        val currentState = _uiState.value
        if (currentState is CheckInUiState.Ready) {
            _uiState.value = currentState.copy(tags = tags)
        }
    }

    private fun handleSave() {
        val currentState = _uiState.value
        if (currentState !is CheckInUiState.Ready) return

        if (currentState.mood == null) {
            _errorMessage.value = "Please select a mood before saving"
            return
        }

        _uiState.value = CheckInUiState.Saving

        viewModelScope.launch {
            try {
                val entry = Entry(
                    date = LocalDate.now(),
                    moodScore = currentState.mood,
                    note = currentState.note,
                    promptId = currentState.prompt.id,
                    tags = currentState.tags
                )

                entryDao.upsert(entry)
                streakManager.updateAfterCheckIn()

                _uiState.value = CheckInUiState.Saved(entryId = entry.date)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save entry: ${e.message}"
                _uiState.value = currentState
            }
        }
    }

    private fun handleNavigateConsumed() {
        viewModelScope.launch {
            loadTodayPrompt()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
