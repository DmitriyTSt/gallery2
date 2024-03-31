package ru.dmitriyt.gallery.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.dmitriyt.gallery.data.model.isImage
import ru.dmitriyt.gallery.data.model.toDomain
import ru.dmitriyt.gallery.domain.model.ChronologicalItem
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.domain.repository.PhotoRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId

class PhotoRepositoryImpl : PhotoRepository {

    override suspend fun getImages(uri: String): List<FileModel.Image> = withContext(Dispatchers.IO) {
        File(uri).listImages(withDirs = false).map { it.toDomain() as FileModel.Image }
    }

    override suspend fun getImagesTree(uri: String): List<FileModel> = withContext(Dispatchers.IO) {
        File(uri)
            .listImages(withDirs = true)
            .map { firstLayerFile ->
                firstLayerFile.toDomain(
                    images = firstLayerFile.listImages(withDirs = false)
                        .take(0)
                        .map { it.toDomain() as FileModel.Image }
                )
            }
    }

    override suspend fun getAllPhotos(uri: String): List<ChronologicalItem> = withContext(Dispatchers.IO) {
        val fileToAttrs = getAllFilesRecursive(File(uri))
            .map { it to getPhotoCreationTime(it) }
            .sortedByDescending { (_, creatingDateTime) ->
                creatingDateTime
            }
        val items = mutableListOf<ChronologicalItem>()
        fileToAttrs.forEachIndexed { index, (file, creatingDateTime) ->
            if (index == 0 || !isSameMonths(creatingDateTime, fileToAttrs[index - 1].second)) {
                items.add(ChronologicalItem.Month(creatingDateTime))
            }
            items.add(ChronologicalItem.Image(file.toDomain() as FileModel.Image))
        }
        items
    }

    private fun isSameMonths(first: LocalDateTime, second: LocalDateTime): Boolean {
        return first.monthValue == second.monthValue && first.year == second.year
    }

    private fun getAllFilesRecursive(directory: File): List<File> {
        val dirFiles = directory.listImages(withDirs = true)
        val allFiles = mutableListOf<File>()
        dirFiles.forEach { dirFile ->
            if (dirFile.isDirectory) {
                allFiles.addAll(getAllFilesRecursive(dirFile))
            } else {
                allFiles.add(dirFile)
            }
        }
        return allFiles
    }

    private fun File.listImages(withDirs: Boolean): List<File> {
        return listFiles()?.toList().orEmpty().filter { (if (withDirs) it.isDirectory else false) || it.isImage() }
    }

    private fun getPhotoCreationTime(imageFile: File): LocalDateTime {
        return Files.readAttributes(imageFile.toPath(), BasicFileAttributes::class.java)
            .lastModifiedTime()
            .toInstant()
            .let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }
    }
}