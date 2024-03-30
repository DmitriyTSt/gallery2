package ru.dmitriyt.gallery.presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.KoinApplication
import org.koin.core.context.loadKoinModules
import ru.dmitriyt.gallery.di.coilModule
import ru.dmitriyt.gallery.di.repositoryModule
import ru.dmitriyt.gallery.di.screenModelModule
import ru.dmitriyt.gallery.di.storageModule
import ru.dmitriyt.gallery.presentation.screen.splash.SplashScreen
import ru.dmitriyt.gallery.presentation.views.galleryasyncimage.GalleryAsyncImageImageLoader
import ru.dmitriyt.gallery.presentation.views.galleryasyncimage.LocalGalleryAsyncImageModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(SplashScreen())
    }
}

fun main() = application {
    KoinApplication(application = {
        loadKoinModules(listOf(coilModule, screenModelModule, storageModule, repositoryModule))
    }) {
        Window(title = "Gallery", onCloseRequest = ::exitApplication) {
            CompositionLocalProvider(LocalGalleryAsyncImageModel provides GalleryAsyncImageImageLoader()) {
                App()
            }
        }
    }
}
