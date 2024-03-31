package ru.dmitriyt.gallery.presentation.galleryasyncimage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import ru.dmitriyt.gallery.presentation.galleryasyncimage.custom.GalleryAsyncImageCustom

private val galleryAsyncImageModel: GalleryAsyncImageModel = GalleryAsyncImageCustom()

@Composable
fun GalleryAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    placeholder: Painter? = null,
    error: Painter? = null,
    size: Size? = null,
    loggerEnabled: Boolean = false
) {
    GalleryAsyncImageCustom(loggerEnabled = loggerEnabled).GalleryAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = placeholder,
        error = error,
        size = size,
    )
}