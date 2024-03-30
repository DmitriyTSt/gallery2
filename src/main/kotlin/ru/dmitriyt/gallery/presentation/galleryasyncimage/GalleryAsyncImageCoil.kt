package ru.dmitriyt.gallery.presentation.galleryasyncimage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import org.koin.compose.getKoin

/**
 * @see <a href="https://github.com/coil-kt/coil">coil</a>
 */
class GalleryAsyncImageCoil : GalleryAsyncImageModel {
    @Composable
    override fun GalleryAsyncImage(
        model: Any?,
        contentDescription: String?,
        modifier: Modifier,
        contentScale: ContentScale,
        placeholder: Painter?,
        error: Painter?,
        size: Size?,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(PlatformContext.INSTANCE)
                .data(model)
                .apply {
                    if (size != null) {
                        size(coil3.size.Size(size.width.toInt(), size.height.toInt()))
                    }
                }
                .build(),
            contentDescription = contentDescription,
            imageLoader = getKoin().get(),
            modifier = modifier,
            placeholder = placeholder,
            error = error,
            contentScale = contentScale,
        )
    }
}