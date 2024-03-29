package ru.dmitriyt.gallery.presentation.screen.splash

import ru.dmitriyt.gallery.domain.model.FileModel

sealed interface SplashUiEvent {
    data class OpenGallery(val directory: FileModel.Directory) : SplashUiEvent
}