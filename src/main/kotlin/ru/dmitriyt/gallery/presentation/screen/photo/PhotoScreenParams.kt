package ru.dmitriyt.gallery.presentation.screen.photo

import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryViewType

data class PhotoScreenParams(
    val backgroundImageUri: String?,
    val directory: FileModel.Directory,
    val index: Int,
    val viewType: UiGalleryViewType,
)