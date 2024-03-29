package ru.dmitriyt.gallery.data.storage

import ru.dmitriyt.gallery.data.model.toDomain
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.domain.repository.PhotoRepository
import java.io.File

class PhotoRepositoryImpl : PhotoRepository {
    override suspend fun getImagesTree(uri: String): List<FileModel> {
        return (File(uri).toDomain() as FileModel.Directory).files
    }

    override suspend fun getAllPhotos(uri: String): List<FileModel.Image> {
        TODO("Not yet implemented")
    }
}