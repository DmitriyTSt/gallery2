package ru.dmitriyt.gallery.data.model

import ru.dmitriyt.gallery.domain.model.FileModel
import java.io.File

private val imageExtensions = setOf("jpg", "png", "bmp", "webp", "ico", "gif", "jpeg")

fun File.toDomain(images: List<FileModel.Image> = emptyList()): FileModel {
    return if (isDirectory) {
        FileModel.Directory(
            uri = absolutePath,
            name = name,
            images = images,
        )
    } else {
        if (isImage()) {
            FileModel.Image(
                uri = absolutePath,
                name = name,
            )
        } else {
            FileModel.Other(
                uri = absolutePath,
                name = name,
            )
        }
    }
}

fun File.isImage(): Boolean {
    return imageExtensions.contains(this.extension.lowercase())
}