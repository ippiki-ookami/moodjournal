package com.example.moodjournal.data

import androidx.room.TypeConverter
import java.time.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromTagsList(tags: List<String>): String {
        return Json.encodeToString(tags)
    }

    @TypeConverter
    fun toTagsList(tagsJson: String): List<String> {
        return if (tagsJson.isEmpty()) {
            emptyList()
        } else {
            Json.decodeFromString(tagsJson)
        }
    }
}
