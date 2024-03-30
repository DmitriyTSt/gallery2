package ru.dmitriyt.gallery.presentation.views.galleryasyncimage.custom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import org.imgscalr.Scalr
import ru.dmitriyt.gallery.data.storage.GalleryCacheStorage
import ru.dmitriyt.gallery.presentation.views.galleryasyncimage.GalleryAsyncImageModel
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class GalleryAsyncImageCustom : GalleryAsyncImageModel {

    @OptIn(DelicateCoroutinesApi::class)
    private val resizeContext by lazy { newFixedThreadPoolContext(2, "resizedContext") }

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
        val state = produceState<ImageState>(initialValue = ImageState.Loading(placeholder), key1 = model) {
            if (model != null) {
                runCatching { loadImage(model.toString(), size, resizeContext) }
                    .fold(
                        onSuccess = {
                            value = ImageState.Success(it)
                        },
                        onFailure = {
                            value = ImageState.Error(error, it)
                        }
                    )
            }
        }
        Box(modifier = modifier) {
            when (val imageState = state.value) {
                is ImageState.Error -> imageState.error?.let {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                is ImageState.Loading -> imageState.placeholder?.let {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                is ImageState.Success -> {
                    Image(
                        bitmap = imageState.image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale,
                    )
                }
            }
        }
    }

    private suspend fun loadImage(imageUri: String, size: Size?, resizeDispatcher: CoroutineDispatcher): ImageBitmap {
        val fastCacheImage = GalleryCacheStorage.getFromFastCache(imageUri)
        if (fastCacheImage != null) {
            return fastCacheImage
        }
        val bufferedImage = GalleryCacheStorage.getFromFileCache(imageUri) ?: loadImageFile(imageUri, size, resizeDispatcher)

        val imageInformation = ImageInformation.readImageInformation(imageUri)
        val thumbnail = ImageUtil.fixImageByExif(
            bufferedImage,
            imageInformation.copy(width = bufferedImage.width, height = bufferedImage.height),
        )
        val newImage = thumbnail.toComposeImageBitmap()
        GalleryCacheStorage.addToFastCache(imageUri, newImage)
        return newImage
    }

    private suspend fun loadImageFile(
        imageUri: String,
        size: Size?,
        resizeDispatcher: CoroutineDispatcher,
    ): BufferedImage = withContext(Dispatchers.IO) {
        val bufferedImage = ImageIO.read(File(imageUri))
        val finalImage = if (size != null) {
            withContext(resizeDispatcher) {
                val resized = Scalr.resize(
                    bufferedImage,
                    Scalr.Method.SPEED,
                    Scalr.Mode.AUTOMATIC,
                    size.width.toInt(),
                    size.height.toInt(),
                )
                resized
            }
        } else {
            bufferedImage
        }

        GalleryCacheStorage.addToFileCache(imageUri, finalImage)
        finalImage
    }
}