package ru.dmitriyt.gallery.di

import coil3.ImageLoader
import coil3.PlatformContext
import org.koin.dsl.module
import ru.dmitriyt.gallery.presentation.utils.WindowsFileUriFetcher

val coilModule = module {
    factory {
        ImageLoader.Builder(PlatformContext.INSTANCE)
            .components {
                add(WindowsFileUriFetcher.Factory())
            }
            .build()
    }
}