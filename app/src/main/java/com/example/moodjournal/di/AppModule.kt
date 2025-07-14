package com.example.moodjournal.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.example.moodjournal.UserPrefs
import com.example.moodjournal.VoicePrefs
import com.example.moodjournal.data.EntryDao
import com.example.moodjournal.data.MoodDatabase
import com.example.moodjournal.data.PromptRepository
import com.example.moodjournal.datastore.StreakManager
import com.example.moodjournal.datastore.UserPrefsSerializer
import com.example.moodjournal.datastore.VoicePrefsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoodDatabase(
        @ApplicationContext context: Context
    ): MoodDatabase {
        return Room.databaseBuilder(
            context,
            MoodDatabase::class.java,
            "mood_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideEntryDao(database: MoodDatabase): EntryDao {
        return database.entryDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<UserPrefs> {
        return DataStoreFactory.create(
            serializer = UserPrefsSerializer,
            produceFile = { context.dataStoreFile("user_prefs.pb") },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Provides
    @Singleton
    fun provideStreakManager(
        dataStore: DataStore<UserPrefs>
    ): StreakManager {
        return StreakManager(dataStore)
    }

    @Provides
    @Singleton
    fun providePromptRepository(
        @ApplicationContext context: Context
    ): PromptRepository {
        return PromptRepository(context)
    }

    @Provides
    @Named("splashDelayMs")
    fun provideSplashDelay(): Long = 1500L
    
    @Provides
    @Singleton
    fun provideVoicePrefsDataStore(
        @ApplicationContext context: Context
    ): DataStore<VoicePrefs> {
        return DataStoreFactory.create(
            serializer = VoicePrefsSerializer,
            produceFile = { context.dataStoreFile("voice_prefs.pb") },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}
