package ru.dmitriyt.gallery.di

import org.koin.dsl.module
import ru.dmitriyt.gallery.data.repository.SettingsRepositoryImpl
import ru.dmitriyt.gallery.data.storage.FileRepositoryImpl
import ru.dmitriyt.gallery.domain.repository.FileRepository
import ru.dmitriyt.gallery.domain.repository.SettingsRepository

val repositoryModule = module {
    factory<SettingsRepository> { SettingsRepositoryImpl(get()) }
    factory<FileRepository> { FileRepositoryImpl() }
}