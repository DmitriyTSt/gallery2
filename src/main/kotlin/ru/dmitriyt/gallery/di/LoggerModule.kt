package ru.dmitriyt.gallery.di

import org.koin.dsl.module
import ru.dmitriyt.logger.Logger

val loggerModule = module {
    single<Logger> { Logger() }
}