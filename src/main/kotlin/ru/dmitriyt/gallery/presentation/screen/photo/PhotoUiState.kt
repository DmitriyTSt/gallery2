package ru.dmitriyt.gallery.presentation.screen.photo

import ru.dmitriyt.gallery.domain.model.FileModel

sealed class PhotoUiState {
    data object Loading : PhotoUiState()

    data class Success(
        val currentImage: FileModel.Image,
        val index: Int,
        val images: List<FileModel.Image>,
    ) : PhotoUiState()

    data class Error(
        val error: String,
    ) : PhotoUiState()
}