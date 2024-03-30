package ru.dmitriyt.gallery.presentation.screen.gallery.model

import ru.dmitriyt.gallery.domain.model.ChronologicalItem
import ru.dmitriyt.gallery.domain.model.FileModel
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale
import kotlin.random.Random

class RotationStorage {
    private val rotations = hashMapOf<FileModel.Image, Float>()

    fun getOrCreate(image: FileModel.Image): Float {
        return rotations[image] ?: run {
            val rotation = ((Random.nextFloat() - 0.5f) * 14f).let { if (it > 0) it + 2.5f else it - 2.5f }
            rotations[image] = rotation
            rotation
        }
    }
}

private val rotationStorage = RotationStorage()

fun FileModel.toUi(): UiGalleryItem? {
    return when (this) {
        is FileModel.Directory -> toUi()
        is FileModel.Image -> toUi()
        is FileModel.Other -> null
    }
}

fun FileModel.Image.toUi(): UiGalleryItem.Image {
    return UiGalleryItem.Image(
        image = this,
        rotation = rotationStorage.getOrCreate(this),
    )
}

fun FileModel.Directory.toUi(): UiGalleryItem.Directory {
    return UiGalleryItem.Directory(
        directory = this,
        images = this.images.map { it.toUi() },
    )
}

private val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("ru", "RU"))

fun ChronologicalItem.toUi(): UiGalleryItem {
    return when (this) {
        is ChronologicalItem.Image -> this.image.toUi()
        is ChronologicalItem.Month -> UiGalleryItem.MonthDivider(
            month = monthFormat
                .format(this.monthDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        )
    }
}