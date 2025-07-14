package com.example.moodjournal.voice

import androidx.datastore.core.DataStore
import com.example.moodjournal.UserPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoicePrefsManager @Inject constructor(
    private val dataStore: DataStore<UserPrefs>
) {
    val isVoiceInputEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        // Default to true if not explicitly set
        prefs.voiceInputEnabled
    }

    suspend fun setVoiceInputEnabled(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setVoiceInputEnabled(enabled)
                .build()
        }
    }
}