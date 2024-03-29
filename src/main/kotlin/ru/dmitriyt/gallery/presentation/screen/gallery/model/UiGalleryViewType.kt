package ru.dmitriyt.gallery.presentation.screen.gallery.model

sealed class UiGalleryViewType {
    data object Chronology : UiGalleryViewType()
    data object Tree : UiGalleryViewType()
}