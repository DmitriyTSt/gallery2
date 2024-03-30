package ru.dmitriyt.gallery.presentation.views.galleryasyncimage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

interface GalleryAsyncImageModel {
    @Composable
    fun GalleryAsyncImage(
        model: Any?,
        contentDescription: String?,
        modifier: Modifier,
        contentScale: ContentScale,
        placeholder: Painter?,
        error: Painter?,
    )
}
