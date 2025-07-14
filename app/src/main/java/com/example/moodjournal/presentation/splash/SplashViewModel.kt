package com.example.moodjournal.presentation.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SplashViewModel @Inject constructor(
    @Named("splashDelayMs") val splashDelayMs: Long
) : ViewModel()