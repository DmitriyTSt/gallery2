package ru.dmitriyt.gallery.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.dmitriyt.gallery.data.storage.SettingsStorage
import java.io.File

val storageModule = module {
    single(named("settings")) {
        PreferenceDataStoreFactory.create {
            File(
                SettingsStorage.appDirectory,
                SettingsStorage.SETTINGS_FILE_NAME,
            )
        }
    }
    single { SettingsStorage(get(qualifier = named("settings"))) }
}