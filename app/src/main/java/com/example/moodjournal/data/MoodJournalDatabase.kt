package com.example.moodjournal.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Entry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoodJournalDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao

    companion object {
        @Volatile
        private var INSTANCE: MoodJournalDatabase? = null

        fun getDatabase(context: Context): MoodJournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodJournalDatabase::class.java,
                    "mood_journal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
