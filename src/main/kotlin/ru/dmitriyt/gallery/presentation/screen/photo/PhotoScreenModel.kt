package ru.dmitriyt.gallery.presentation.screen.photo

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dmitriyt.gallery.domain.model.ChronologicalItem
import ru.dmitriyt.gallery.domain.repository.PhotoRepository
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryViewType

class PhotoScreenModel(
    private val photoRepository: PhotoRepository,
) : StateScreenModel<PhotoUiState>(PhotoUiState.Loading) {

    fun init(params: PhotoScreenParams) {
        screenModelScope.launch {
            runCatching {
                when (params.viewType) {
                    UiGalleryViewType.Chronology -> {
                        photoRepository.getAllPhotos(params.directory.uri)
                            .filterIsInstance<ChronologicalItem.Image>()
                            .map { it.image }
                    }
                    UiGalleryViewType.Tree -> {
                        photoRepository.getImages(params.directory.uri)
                    }
                }
            }.fold(
                onSuccess = { images ->
                    mutableState.update {
                        PhotoUiState.Success(
                            currentImage = images[params.index],
                            index = params.index,
                            images = images,
                        )
                    }
                },
                onFailure = { throwable ->
                    mutableState.update {
                        PhotoUiState.Error(
                            error = throwable.message.orEmpty(),
                        )
                    }
                }
            )
        }
    }

    fun onNextClick() {
        val state = mutableState.value as? PhotoUiState.Success ?: return
        if (state.index < state.images.size) {
            val newIndex = state.index + 1
            mutableState.update {
                state.copy(
                    currentImage = state.images[newIndex],
                    index = newIndex,
                )
            }
        }
    }

    fun onPrevClick() {
        val state = mutableState.value as? PhotoUiState.Success ?: return
        if (state.index > 0) {
            val newIndex = state.index - 1
            mutableState.update {
                state.copy(
                    currentImage = state.images[newIndex],
                    index = newIndex,
                )
            }
        }
    }
}