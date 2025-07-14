package com.example.moodjournal.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class VoicePermissionManagerTest {

    private lateinit var context: Context
    private lateinit var activity: ComponentActivity
    private lateinit var voicePermissionManager: VoicePermissionManager

    @Before
    fun setup() {
        context = mockk()
        activity = mockk()
        voicePermissionManager = VoicePermissionManager(context)
        
        // Setup default mocks
        every { activity.registerForActivityResult(any<Any>(), any()) } returns mockk()
    }

    @Test
    fun `hasPermission returns true when permission granted`() {
        // Given
        mockkStatic(ContextCompat::class)
        every { 
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
        } returns PackageManager.PERMISSION_GRANTED

        // When
        val result = voicePermissionManager.hasPermission()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasPermission returns false when permission denied`() {
        // Given
        mockkStatic(ContextCompat::class)
        every { 
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
        } returns PackageManager.PERMISSION_DENIED

        // When
        val result = voicePermissionManager.hasPermission()

        // Then
        assertFalse(result)
    }

    @Test
    fun `shouldShowRationale returns false when permission permanently denied`() {
        // Given
        mockkStatic(ActivityCompat::class)
        every { 
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO) 
        } returns false

        // When
        val result = voicePermissionManager.shouldShowRationale(activity)

        // Then
        assertFalse(result)
        verify { 
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO) 
        }
    }

    @Test
    fun `shouldShowRationale returns true when rationale should be shown`() {
        // Given
        mockkStatic(ActivityCompat::class)
        every { 
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO) 
        } returns true

        // When
        val result = voicePermissionManager.shouldShowRationale(activity)

        // Then
        assertTrue(result)
        verify { 
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO) 
        }
    }

    @Test
    fun `ensureMicPermission returns true immediately when already granted`() = runTest {
        // Given
        mockkStatic(ContextCompat::class)
        every { 
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
        } returns PackageManager.PERMISSION_GRANTED

        voicePermissionManager.registerPermissionLauncher(activity)

        // When
        val result = voicePermissionManager.ensureMicPermission(activity)

        // Then
        assertTrue(result)
    }

    @Test
    fun `registerPermissionLauncher creates launcher successfully`() {
        // Given
        val mockLauncher = mockk<Any>()
        every { activity.registerForActivityResult(any<Any>(), any()) } returns mockLauncher

        // When
        voicePermissionManager.registerPermissionLauncher(activity)

        // Then
        verify { activity.registerForActivityResult(any<Any>(), any()) }
    }
}