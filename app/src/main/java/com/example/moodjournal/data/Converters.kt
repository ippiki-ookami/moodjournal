package com.example.moodjournal.data

import androidx.room.TypeConverter
import java.time.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun fromTagList(tags: List<String>): String {
        return Json.encodeToString(tags)
    }

    @TypeConverter
    fun toTagList(tagsJson: String): List<String> {
        return Json.decodeFromString(tagsJson)
    }
}
