package ru.dmitriyt.gallery.data.repository

import ru.dmitriyt.gallery.data.storage.SettingsStorage
import ru.dmitriyt.gallery.domain.model.Settings
import ru.dmitriyt.gallery.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val settingsStorage: SettingsStorage,
) : SettingsRepository {

    override suspend fun getSettings(): Settings {
        return Settings(photoArchiveUri = settingsStorage.getPhotoArchiveUri())
    }

    override suspend fun storeSettings(settings: Settings) {
        settingsStorage.setPhotoArchiveUri(settings.photoArchiveUri)
    }
}