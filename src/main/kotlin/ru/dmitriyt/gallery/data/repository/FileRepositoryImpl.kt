package ru.dmitriyt.gallery.data.repository

import ru.dmitriyt.gallery.data.model.toDomain
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.domain.repository.FileRepository
import java.io.File

class FileRepositoryImpl : FileRepository {

    override suspend fun getFile(uri: String): FileModel {
        return File(uri).toDomain()
    }
}