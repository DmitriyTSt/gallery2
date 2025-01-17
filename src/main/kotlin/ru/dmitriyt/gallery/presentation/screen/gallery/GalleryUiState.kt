package ru.dmitriyt.gallery.presentation.screen.gallery

import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryViewType

data class GalleryUiState(
    val currentDirectory: UiGalleryItem.Directory? = null,
    val directoryPath: String = "",
    val isNestedDirectory: Boolean = false,
    val viewType: UiGalleryViewType = UiGalleryViewType.Tree,
    val contentState: Content = Content.Loading,
    val backgroundImageUri: String? = null,
) {
    val showBackIcon: Boolean
        get() = viewType == UiGalleryViewType.Tree && isNestedDirectory

    val showTitleExpandLogic: Boolean
        get() = viewType == UiGalleryViewType.Tree && isNestedDirectory

    sealed class Content {
        data object Loading : Content()
        data class Error(val error: String) : Content()
        data class Success(
            val items: List<UiGalleryItem> = emptyList(),
        ) : Content()
    }
}


