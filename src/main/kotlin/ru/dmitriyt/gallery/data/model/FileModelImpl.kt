package ru.dmitriyt.gallery.data.model

import ru.dmitriyt.gallery.domain.model.FileModel
import java.io.File

private val imageExtensions = setOf("jpg", "png", "bmp", "webp", "ico", "gif", "jpeg")

fun File.toDomain(): FileModel {
    return if (isDirectory) {
        FileModel.Directory(
            uri = absolutePath,
            name = name,
            files = emptyList(),
        ).let { directory ->
            directory.copy(
                files = listFiles().orEmpty().map { it.toDomain() }
            )
        }
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

private fun File.isImage(): Boolean {
    return imageExtensions.contains(this.extension.lowercase())
}