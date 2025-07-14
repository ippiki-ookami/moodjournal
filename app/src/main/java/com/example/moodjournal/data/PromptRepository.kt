package com.example.moodjournal.data

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PromptsJson(
    val prompts: List<PromptJson>
)

@Serializable
data class PromptJson(
    val id: String,
    val text: String,
    val pack: String
)

@Singleton
class PromptRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private var cachedPrompts: List<Prompt>? = null
    
    companion object {
        private const val TAG = "PromptRepository"
    }

    suspend fun getAllPrompts(): List<Prompt> {
        Log.d(TAG, "getAllPrompts() called, context: ${context.packageName}")
        if (cachedPrompts == null) {
            try {
                Log.d(TAG, "Loading prompts.json from assets...")
                val jsonString = context.assets.open("prompts.json")
                    .bufferedReader()
                    .use { it.readText() }
                Log.d(TAG, "Loaded ${jsonString.length} characters from prompts.json")
                
                val promptsJson = json.decodeFromString<PromptsJson>(jsonString)
                cachedPrompts = promptsJson.prompts.map {
                    Prompt(id = it.id, text = it.text, pack = it.pack)
                }
                Log.d(TAG, "Successfully parsed ${cachedPrompts?.size} prompts")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load prompts.json", e)
                throw e
            }
        } else {
            Log.d(TAG, "Using cached prompts (${cachedPrompts?.size} items)")
        }
        return cachedPrompts!!
    }

    suspend fun getTodayPrompt(): Prompt {
        Log.d(TAG, "getTodayPrompt() called")
        val prompts = getAllPrompts()
        val dayOfYear = LocalDate.now().dayOfYear
        val index = (dayOfYear - 1) % prompts.size
        val selectedPrompt = prompts[index]
        Log.d(TAG, "Selected prompt for day $dayOfYear: ${selectedPrompt.text}")
        return selectedPrompt
    }

    suspend fun getPromptById(id: String): Prompt? {
        return getAllPrompts().find { it.id == id }
    }
}
