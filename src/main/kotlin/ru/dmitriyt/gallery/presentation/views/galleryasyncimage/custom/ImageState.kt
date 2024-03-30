package ru.dmitriyt.gallery.presentation.views.galleryasyncimage.custom

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter

sealed class ImageState {
    data class Loading(val placeholder: Painter?) : ImageState()
    data class Error(val error: Painter?, val throwable: Throwable) : ImageState()
    data class Success(val image: ImageBitmap) : ImageState()
}
