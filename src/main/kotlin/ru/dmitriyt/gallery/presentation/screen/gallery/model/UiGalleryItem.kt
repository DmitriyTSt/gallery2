package ru.dmitriyt.gallery.presentation.screen.gallery.model

import ru.dmitriyt.gallery.domain.model.FileModel

sealed class UiGalleryItem {

    data class Image(
        val image: FileModel.Image,
        val rotation: Float,
    ) : UiGalleryItem() {

        companion object {
            fun placeholder(): Image {
                return FileModel.Image("", "placeholder").toUi()
            }
        }
    }

    data class Directory(
        val directory: FileModel.Directory,
        val images: List<Image>,
    ) : UiGalleryItem()
}
