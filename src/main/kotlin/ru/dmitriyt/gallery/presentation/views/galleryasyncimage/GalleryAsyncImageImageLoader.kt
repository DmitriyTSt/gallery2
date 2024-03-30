package ru.dmitriyt.gallery.presentation.views.galleryasyncimage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.ui.AutoSizeImage
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import ru.dmitriyt.gallery.data.storage.SettingsStorage
import java.io.File

/**
 * @see <a href="https://github.com/qdsfdhvh/compose-imageloader">compose-imageloader</a>
 */
class GalleryAsyncImageImageLoader : GalleryAsyncImageModel {

    private val imageLoader by lazy { generateImageLoader() }

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
        CompositionLocalProvider(
            LocalImageLoader provides remember { imageLoader },
        ) {
            AutoSizeImage(
                request = ImageRequest {
                    data(model.toString().toPath())
                },
                contentDescription = contentDescription,
                modifier = modifier,
                placeholderPainter = placeholder?.let { { it } },
                errorPainter = error?.let { { it } },
                contentScale = contentScale,
            )
        }
    }

    private fun generateImageLoader(): ImageLoader {
        return ImageLoader {
            components {
                setupDefaultComponents()
            }
            interceptor {
                // cache 32MB bitmap
                bitmapMemoryCacheConfig {
                    maxSize(32 * 1024 * 1024) // 32MB
                }
                // cache 50 image
                imageMemoryCacheConfig {
                    maxSize(100)
                }
                // cache 50 painter
                painterMemoryCacheConfig {
                    maxSize(100)
                }
                diskCacheConfig {
                    directory(getCacheDir().toOkioPath())
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }

    private fun getCacheDir(): File {
        return File(SettingsStorage.appDirectory, "image_cache").apply {
            if (!exists()) {
                mkdir()
            }
        }
    }
}