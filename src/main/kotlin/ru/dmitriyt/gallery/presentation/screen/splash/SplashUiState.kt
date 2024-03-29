package ru.dmitriyt.gallery.presentation.screen.splash

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object SelectDirectory : SplashUiState()
}