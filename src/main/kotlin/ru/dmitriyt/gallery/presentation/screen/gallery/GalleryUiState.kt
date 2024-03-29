package ru.dmitriyt.gallery.presentation.screen.gallery

import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryViewType

data class GalleryUiState(
    val currentDirectory: UiGalleryItem.Directory? = null,
    val canGoBack: Boolean = false,
    val viewType: UiGalleryViewType = UiGalleryViewType.Tree,
    val contentState: Content = Content.Loading,
) {

    sealed class Content {
        data object Loading : Content()
        data class Error(val error: String) : Content()
        data class Success(
            val backgroundImageUri: String?,
            val items: List<UiGalleryItem> = emptyList(),
        ) : Content()

        fun getOrNull(): Success? {
            return this as? Success
        }
    }
}


