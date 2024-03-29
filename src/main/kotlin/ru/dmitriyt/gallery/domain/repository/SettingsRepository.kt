package ru.dmitriyt.gallery.domain.repository

import ru.dmitriyt.gallery.domain.model.Settings

/**
 * Работа с настройками приложения
 */
interface SettingsRepository {

    /**
     * Получение настроек
     */
    suspend fun getSettings(): Settings

    /**
     * Сохранение настроек
     */
    suspend fun storeSettings(settings: Settings)
}