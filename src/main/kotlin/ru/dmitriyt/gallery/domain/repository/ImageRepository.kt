package ru.dmitriyt.gallery.domain.repository

interface ImageRepository {

    suspend fun cacheImage(imageUri: String, resizePx: Int, force: Boolean)
}