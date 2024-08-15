package ru.dmitriyt.gallery.presentation.screen.splash

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data class Caching(val progressPercent: String) : SplashUiState()
    data object SelectDirectory : SplashUiState()
}