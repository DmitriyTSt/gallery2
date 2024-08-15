package ru.dmitriyt.gallery.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import ru.dmitriyt.gallery.domain.usecase.CacheImagesUseCase

val useCaseModule = module {
    factory { CacheImagesUseCase(get(), get(), CoroutineScope(Dispatchers.Default)) }
}