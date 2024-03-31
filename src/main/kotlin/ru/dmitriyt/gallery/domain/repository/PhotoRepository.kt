package ru.dmitriyt.gallery.domain.repository

import ru.dmitriyt.gallery.domain.model.ChronologicalItem
import ru.dmitriyt.gallery.domain.model.FileModel

/**
 * Доступ к файловой системе для получения изображений
 */
interface PhotoRepository {

    /**
     * Получить изображения из заданной папки по пути [uri]
     * @param uri - путь папки
     */
    suspend fun getImages(uri: String): List<FileModel.Image>

    /**
     * Получить дерево из папок и изображений по пути [uri]
     * @param uri - путь корневой папки дерева
     */
    suspend fun getImagesTree(uri: String): List<FileModel>

    /**
     * Получить все изображения рекурсивно по всем папкам по пути [uri]
     * @param uri - путь корневой папки
     */
    suspend fun getAllPhotos(uri: String): List<ChronologicalItem>
}