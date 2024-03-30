package ru.dmitriyt.gallery.presentation.galleryasyncimage.custom

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage

object ImageUtil {

    suspend fun fixImageByExif(
        image: BufferedImage,
        imageInformation: ImageInformation,
    ): BufferedImage = withContext(Dispatchers.Default) {
        val op = AffineTransformOp(
            getExifTransformation(imageInformation),
            AffineTransformOp.TYPE_BICUBIC,
        )
        val destinationImage = op.createCompatibleDestImage(
            image,
            if (image.type == BufferedImage.TYPE_BYTE_GRAY) image.colorModel else null
        )
        op.filter(image, destinationImage)
    }

    private fun getExifTransformation(info: ImageInformation): AffineTransform {
        val t = AffineTransform()
        when (info.orientation) {
            1 -> Unit
            2 -> {
                t.scale(-1.0, 1.0)
                t.translate(-info.width.toDouble(), 0.0)
            }
            3 -> {
                t.translate(info.width.toDouble(), info.height.toDouble())
                t.rotate(Math.PI)
            }
            4 -> {
                t.scale(1.0, -1.0)
                t.translate(0.0, -info.height.toDouble())
            }
            5 -> {
                t.rotate(-Math.PI / 2)
                t.scale(-1.0, 1.0)
            }
            6 -> {
                t.translate(info.height.toDouble(), 0.0)
                t.rotate(Math.PI / 2)
            }
            7 -> {
                t.scale(-1.0, 1.0)
                t.translate(-info.height.toDouble(), 0.0)
                t.translate(0.0, info.width.toDouble())
                t.rotate(3 * Math.PI / 2)
            }
            8 -> {
                t.translate(0.0, info.width.toDouble())
                t.rotate(3 * Math.PI / 2)
            }
        }
        return t
    }
}