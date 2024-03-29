package ru.dmitriyt.gallery.domain.model

sealed interface FileModel {
    val uri: String
    val name: String

    data class Other(
        override val uri: String,
        override val name: String,
    ) : FileModel

    data class Image(
        override val uri: String,
        override val name: String,
    ) : FileModel

    data class Directory(
        override val uri: String,
        override val name: String,
        val files: List<FileModel>,
    ) : FileModel
}
