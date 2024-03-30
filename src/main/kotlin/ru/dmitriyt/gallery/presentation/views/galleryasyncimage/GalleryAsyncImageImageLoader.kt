package ru.dmitriyt.gallery.presentation.views.galleryasyncimage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.ui.AutoSizeImage
import okio.Path.Companion.toPath

class GalleryAsyncImageImageLoader : GalleryAsyncImageModel {
    @Composable
    override fun GalleryAsyncImage(
        model: Any?,
        contentDescription: String?,
        modifier: Modifier,
        contentScale: ContentScale,
        placeholder: Painter?,
        error: Painter?
    ) {
        AutoSizeImage(
            request = ImageRequest { data(model.toString().toPath()) },
            contentDescription = contentDescription,
            modifier = modifier,
            placeholderPainter = placeholder?.let { { it } },
            errorPainter = error?.let { { it } },
            contentScale = contentScale,
        )
    }
}