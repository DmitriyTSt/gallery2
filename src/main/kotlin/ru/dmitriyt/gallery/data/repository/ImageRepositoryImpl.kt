package ru.dmitriyt.gallery.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.imgscalr.Scalr
import ru.dmitriyt.gallery.data.storage.GalleryCacheStorage
import ru.dmitriyt.gallery.domain.repository.ImageRepository
import ru.dmitriyt.gallery.presentation.galleryasyncimage.custom.ImageReader
import ru.dmitriyt.gallery.presentation.galleryasyncimage.custom.ImageReaderImageIO

class ImageRepositoryImpl(
    private val imageReader: ImageReader = ImageReaderImageIO(),
) : ImageRepository {

    override suspend fun cacheImage(imageUri: String, resizePx: Int, force: Boolean) {
        val oldImage = GalleryCacheStorage.getFromFileCache(imageUri)
        if (!force && oldImage != null) return

        val bufferedImage = withContext(Dispatchers.IO) { imageReader.readImage(imageUri) }
        val finalImage = withContext(Dispatchers.Default) {
            val resized = Scalr.resize(
                bufferedImage,
                Scalr.Method.SPEED,
                Scalr.Mode.AUTOMATIC,
                resizePx,
                resizePx,
            )
            resized
        }

        GalleryCacheStorage.addToFileCache(imageUri, finalImage)
    }
}