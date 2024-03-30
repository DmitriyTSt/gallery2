package ru.dmitriyt.gallery.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.File

private val PHOTO_ARCHIVE_URI_KEY = stringPreferencesKey("photo_archive_uri")

class SettingsStorage(
    private val dataStore: DataStore<Preferences>,
) {

    suspend fun getPhotoArchiveUri(): String? {
        return dataStore.data.map { preferences -> preferences[PHOTO_ARCHIVE_URI_KEY] }.firstOrNull()
    }

    suspend fun setPhotoArchiveUri(value: String?) {
        dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(PHOTO_ARCHIVE_URI_KEY)
            } else {
                preferences[PHOTO_ARCHIVE_URI_KEY] = value
            }
        }
    }

    companion object {
        val appDirectory by lazy {
            File(System.getProperty("user.home"), "ru.dmitriyt.gallery2").apply {
                if (!exists()) {
                    mkdir()
                }
            }
        }

        val cacheDir by lazy {
            File(appDirectory, "cache").apply {
                if (!exists()) {
                    mkdir()
                }
            }
        }

        const val SETTINGS_FILE_NAME = "settings.preferences_pb"
    }
}