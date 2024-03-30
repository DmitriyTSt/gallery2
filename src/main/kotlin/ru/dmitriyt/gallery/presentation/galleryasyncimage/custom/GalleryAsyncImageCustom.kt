package ru.dmitriyt.gallery.presentation.galleryasyncimage.custom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.imgscalr.Scalr
import ru.dmitriyt.gallery.data.storage.GalleryCacheStorage
import ru.dmitriyt.gallery.presentation.galleryasyncimage.GalleryAsyncImageModel
import ru.dmitriyt.logger.Logger
import java.awt.image.BufferedImage
import kotlin.time.measureTime

class GalleryAsyncImageCustom(
    private val imageReader: ImageReader = ImageReaderImageIO(),
) : GalleryAsyncImageModel {

    @OptIn(DelicateCoroutinesApi::class)
    private val resizeContext by lazy {
        newFixedThreadPoolContext(2, "resizedContext")
    }

    private val counterMutex = Mutex()
    private var imagesInProgress: Int = 0

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
        var state by remember { mutableStateOf<ImageState>(ImageState.Loading(placeholder)) }
        val scope = rememberCoroutineScope()

        DisposableEffect(model) {
            scope.launch {
                if (model == null) {
                    state = ImageState.Error(error, RuntimeException("ImageLoad model null"))
                    return@launch
                }
                counterMutex.withLock {
                    imagesInProgress++
                    Logger.d("imageLoader : inProgressCount = $imagesInProgress")
                }
                runCatching {
                    val imageBitmap: ImageBitmap
                    val time = measureTime {
                        imageBitmap = loadImage(model.toString(), size, resizeContext)
                    }
                    Logger.d("imageLoader : time $time from $model")
                    imageBitmap
                }
                    .fold(
                        onSuccess = {
//                            Logger.d("imageLoader : success $model")
                            counterMutex.withLock {
                                imagesInProgress--
                                Logger.d("imageLoader : inProgressCount = $imagesInProgress")
                            }
                            state = ImageState.Success(it)
                        },
                        onFailure = {
                            counterMutex.withLock {
                                imagesInProgress--
                                Logger.d("imageLoader : inProgressCount = $imagesInProgress")
                            }
                            if (it is CancellationException) {
                                throw it
                            }
//                            Logger.e(it)
                            state = ImageState.Error(error, it)
                        }
                    )
            }

            onDispose {
                scope.cancel()
            }
        }
        Box(modifier = modifier) {
            when (val imageState = state) {
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
        val bufferedImage = GalleryCacheStorage.getFromFileCache(imageUri)
            ?: loadImageFile(imageUri, size, resizeDispatcher)

        val fixedOrientationBufferedImage = imageReader.fixOrientation(imageUri, bufferedImage)
        val finalImageBitmap = fixedOrientationBufferedImage.toComposeImageBitmap()
        GalleryCacheStorage.addToFastCache(imageUri, finalImageBitmap)
        return finalImageBitmap
    }

    private suspend fun loadImageFile(
        imageUri: String,
        size: Size?,
        resizeDispatcher: CoroutineDispatcher,
    ): BufferedImage = withContext(Dispatchers.IO) {
        val bufferedImage: BufferedImage
        val time = measureTime {
            bufferedImage = imageReader.readImage(imageUri)
        }
        Logger.d("imageLoader : readTime $time from $imageUri")
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