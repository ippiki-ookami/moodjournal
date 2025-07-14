package com.example.moodjournal.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class VoicePermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var pendingPermissionCallback: ((Boolean) -> Unit)? = null

    fun registerPermissionLauncher(activity: ComponentActivity) {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            pendingPermissionCallback?.invoke(isGranted)
            pendingPermissionCallback = null
        }
    }

    suspend fun ensureMicPermission(activity: ComponentActivity): Boolean {
        val permission = Manifest.permission.RECORD_AUDIO
        
        return when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                showRationaleAndRequest(activity, permission)
            }
            else -> {
                requestPermission(permission)
            }
        }
    }

    private suspend fun showRationaleAndRequest(activity: ComponentActivity, permission: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            AlertDialog.Builder(activity)
                .setTitle("Microphone Permission Required")
                .setMessage("Voice input requires microphone access to record your notes. This helps you quickly capture your thoughts without typing.")
                .setPositiveButton("Grant Permission") { _, _ ->
                    // Request permission after user confirms
                    pendingPermissionCallback = { isGranted ->
                        continuation.resume(isGranted)
                    }
                    permissionLauncher?.launch(permission)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    continuation.resume(false)
                }
                .setOnCancelListener {
                    continuation.resume(false)
                }
                .show()
        }
    }

    private suspend fun requestPermission(permission: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            pendingPermissionCallback = { isGranted ->
                continuation.resume(isGranted)
            }
            permissionLauncher?.launch(permission)
        }
    }

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRationale(activity: ComponentActivity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity, 
            Manifest.permission.RECORD_AUDIO
        )
    }
}