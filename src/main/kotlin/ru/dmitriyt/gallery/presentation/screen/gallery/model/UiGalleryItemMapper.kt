package ru.dmitriyt.gallery.presentation.screen.gallery.model

import ru.dmitriyt.gallery.domain.model.FileModel
import kotlin.random.Random

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
        rotation = ((Random.nextFloat() - 0.5f) * 14f).let { if (it > 0) it + 2.5f else it - 2.5f },
    )
}

fun FileModel.Directory.toUi(): UiGalleryItem.Directory {
    return UiGalleryItem.Directory(
        directory = this,
        images = this.files.filterIsInstance<FileModel.Image>().map { it.toUi() },
    )
}
