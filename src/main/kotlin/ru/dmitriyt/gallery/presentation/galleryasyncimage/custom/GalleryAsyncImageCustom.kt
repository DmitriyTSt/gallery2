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
    private val loggerEnabled: Boolean = false,
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
        val fastCacheImage = model?.let { getFastCacheImage(it.toString()) }
        var state by remember(fastCacheImage) {
            mutableStateOf(
                fastCacheImage?.let { ImageState.Success(it) }
                    ?: ImageState.Loading(placeholder))
        }
        val scope = rememberCoroutineScope()

        DisposableEffect(model) {
            val loadImageJob = scope.launch {
                if (model == null) {
                    state = ImageState.Error(error, RuntimeException("ImageLoad model null"))
                    return@launch
                }
                if (loggerEnabled) {
                    counterMutex.withLock {
                        imagesInProgress++
                        Logger.d("imageLoader : inProgressCount = $imagesInProgress")
                    }
                }

                runCatching {
                    val imageBitmap: ImageBitmap
                    val time = measureTime {
                        imageBitmap = loadImage(model.toString(), size, resizeContext)
                    }
                    if (loggerEnabled) {
                        Logger.d("imageLoader : time $time from $model")
                    }
                    imageBitmap
                }
                    .fold(
                        onSuccess = {
                            if (loggerEnabled) {
                                counterMutex.withLock {
                                    imagesInProgress--
                                    Logger.d("imageLoader : inProgressCount = $imagesInProgress")
                                }
                            }
                            state = ImageState.Success(it)
                        },
                        onFailure = {
                            if (loggerEnabled) {
                                counterMutex.withLock {
                                    imagesInProgress--
                                    Logger.d("imageLoader : inProgressCount = $imagesInProgress")
                                }
                            }
                            if (it is CancellationException) {
                                throw it
                            }
                            state = ImageState.Error(error, it)
                        }
                    )
            }

            onDispose {
                loadImageJob.cancel()
            }
        }
        Box(modifier = modifier) {
            when (val imageState = state) {
                is ImageState.Error -> {
                    if (loggerEnabled) {
                        Logger.e(imageState.throwable)
                    }
                    imageState.error?.let {
                        Image(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                is ImageState.Loading -> {
                    imageState.placeholder?.let {
                        Image(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
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

    private fun getFastCacheImage(imageUri: String): ImageBitmap? {
        return GalleryCacheStorage.getFromFastCache(imageUri)
    }

    private suspend fun loadImage(imageUri: String, size: Size?, resizeDispatcher: CoroutineDispatcher): ImageBitmap {
        val fastCacheImage = GalleryCacheStorage.getFromFastCache(imageUri)
        // сейчас сделано так, что когда size есть, то сразу берется из кеша,
        //  а если нет (нам нужно загрузить полную), то идет грузить минуя кеш
        // TODO refactoring load image size selection from cache logic
        if (fastCacheImage != null && size != null) {
            Logger.d("imageLoader : from fast cache: $imageUri")
            return fastCacheImage
        }
        // TODO refactoring load image size selection from cache logic
        val bufferedImage = (if (size != null) {
            Logger.d("imageLoader : from file cache : $imageUri")
            GalleryCacheStorage.getFromFileCache(imageUri)
        } else null)
            ?: run {
                Logger.d("imageLoader : from original file : $imageUri")
                loadImageFile(imageUri, size, resizeDispatcher)
            }

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
        if (loggerEnabled) {
            Logger.d("imageLoader : readTime $time from $imageUri")
        }
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