package ru.dmitriyt.gallery.domain.model

/**
 * Настройки приложения
 */
data class Settings(
    /** Путь до директории архива с фото */
    val photoArchiveUri: String?,
)
