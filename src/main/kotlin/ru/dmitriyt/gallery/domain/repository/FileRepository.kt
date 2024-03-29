package ru.dmitriyt.gallery.domain.repository

import ru.dmitriyt.gallery.domain.model.FileModel

interface FileRepository {

    suspend fun getFile(uri: String): FileModel
}