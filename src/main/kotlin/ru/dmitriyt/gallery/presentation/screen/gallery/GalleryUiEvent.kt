package ru.dmitriyt.gallery.presentation.screen.gallery

sealed interface GalleryUiEvent {
    data object OpenSplash : GalleryUiEvent
    data class ShowError(val error: String) : GalleryUiEvent
}