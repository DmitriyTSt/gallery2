package ru.dmitriyt.gallery.presentation.views.galleryasyncimage.custom

import com.sksamuel.scrimage.ImmutableImage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.coroutines.resume

interface ImageReader {
    suspend fun readImage(imageUri: String): BufferedImage

    suspend fun fixOrientation(imageUri: String, image: BufferedImage): BufferedImage
}

class ImageReaderImageIO : ImageReader {
    override suspend fun readImage(imageUri: String): BufferedImage = suspendCancellableCoroutine {
        val bufferedImage = ImageIO.read(File(imageUri))
        it.resume(bufferedImage)
    }

    override suspend fun fixOrientation(imageUri: String, image: BufferedImage): BufferedImage {
        val imageInformation = ImageInformation.readImageInformation(imageUri)
        return ImageUtil.fixImageByExif(
            image,
            imageInformation.copy(width = image.width, height = image.height),
        )
    }
}

class ImageReaderScrimage : ImageReader {
    override suspend fun readImage(imageUri: String): BufferedImage = suspendCancellableCoroutine {
        val bufferedImage = ImmutableImage.loader().fromFile(File(imageUri)).awt()
        it.resume(bufferedImage)
    }

    override suspend fun fixOrientation(imageUri: String, image: BufferedImage): BufferedImage {
        return image
    }
}