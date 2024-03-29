package ru.dmitriyt.gallery.presentation.screen.gallery.model

import ru.dmitriyt.gallery.domain.model.FileModel
import kotlin.random.Random

sealed interface UiGalleryItem {

    data class Image(
        val image: FileModel.Image,
        val rotation: Float,
    ) : UiGalleryItem {

        companion object {
            fun placeholder(): Image {
                return FileModel.Image("", "placeholder").toUi()
            }
        }
    }

    data class Directory(
        val directory: FileModel.Directory,
        val images: List<Image>,
    ) : UiGalleryItem
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

