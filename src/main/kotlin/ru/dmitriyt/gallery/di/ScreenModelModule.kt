package ru.dmitriyt.gallery.di

import org.koin.dsl.module
import ru.dmitriyt.gallery.presentation.screen.gallery.GalleryScreenModel
import ru.dmitriyt.gallery.presentation.screen.photo.PhotoScreenModel
import ru.dmitriyt.gallery.presentation.screen.splash.SplashScreenModel

val screenModelModule = module {
    factory { SplashScreenModel(get(), get(), get()) }
    factory { GalleryScreenModel(get(), get()) }
    factory { PhotoScreenModel(get()) }
}