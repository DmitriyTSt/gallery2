package ru.dmitriyt.gallery.presentation.screen.gallery.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.graphics.vector.ImageVector

sealed class UiGalleryViewType(
    val inverseIcon: ImageVector,
) {
    data object Chronology : UiGalleryViewType(Icons.Default.Folder)
    data object Tree : UiGalleryViewType(Icons.Default.CalendarMonth)
}