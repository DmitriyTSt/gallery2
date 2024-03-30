package ru.dmitriyt.gallery.presentation.galleryasyncimage

import androidx.compose.runtime.compositionLocalOf

val LocalGalleryAsyncImageModel = compositionLocalOf<GalleryAsyncImageModel> { error("GalleryAsyncImageModel not provided") }