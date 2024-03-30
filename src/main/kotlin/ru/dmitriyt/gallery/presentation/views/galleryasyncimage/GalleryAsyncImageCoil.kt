package ru.dmitriyt.gallery.presentation.views.galleryasyncimage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import org.koin.compose.getKoin

class GalleryAsyncImageCoil : GalleryAsyncImageModel {
    @Composable
    override fun GalleryAsyncImage(
        model: Any?,
        contentDescription: String?,
        modifier: Modifier,
        contentScale: ContentScale,
        placeholder: Painter?,
        error: Painter?
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            imageLoader = getKoin().get(),
            modifier = modifier,
            placeholder = placeholder,
            error = error,
            contentScale = contentScale,
        )
    }
}