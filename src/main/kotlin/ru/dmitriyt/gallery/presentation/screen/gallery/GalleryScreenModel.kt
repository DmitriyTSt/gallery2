package ru.dmitriyt.gallery.presentation.screen.gallery

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.domain.repository.PhotoRepository
import ru.dmitriyt.gallery.domain.repository.SettingsRepository
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryViewType
import ru.dmitriyt.gallery.presentation.screen.gallery.model.toUi

class GalleryScreenModel(
    private val settingsRepository: SettingsRepository,
    private val photoRepository: PhotoRepository,
) : StateScreenModel<GalleryUiState>(GalleryUiState()) {

    private val mutableEvent = MutableSharedFlow<GalleryUiEvent>(replay = 1)
    val event: SharedFlow<GalleryUiEvent> = mutableEvent.asSharedFlow()

    private val directoryStack: ArrayDeque<FileModel.Directory> = ArrayDeque()

    fun init(rootDirectory: FileModel.Directory) {
        directoryStack.addLast(rootDirectory)
        mutableState.update { state -> state.copy(contentState = GalleryUiState.Content.Loading) }
        loadPhotos(rootDirectory)
    }

    fun changeDirectory(newDirectory: FileModel.Directory) {
        directoryStack.addLast(newDirectory)
        loadPhotos(newDirectory)
    }

    fun onBackClick() {
        val directory = if (mutableState.value.isNestedDirectory) {
            directoryStack.removeLast()
            directoryStack.last()
        } else {
            null
        }
        directory?.let { loadPhotos(it) }
    }

    fun changeViewType() {
        mutableState.update { state ->
            state.copy(
                viewType = when (state.viewType) {
                    UiGalleryViewType.Chronology -> UiGalleryViewType.Tree
                    UiGalleryViewType.Tree -> UiGalleryViewType.Chronology
                }
            )
        }
        loadPhotos(directoryStack.first())
    }

    fun closeRootDirectory() {
        screenModelScope.launch {
            runCatching {
                val settings = settingsRepository.getSettings()
                settingsRepository.storeSettings(settings.copy(photoArchiveUri = null))
            }.fold(
                onSuccess = {
                    mutableEvent.emit(GalleryUiEvent.OpenSplash)
                },
                onFailure = {
                    mutableEvent.emit(GalleryUiEvent.ShowError(it.message.orEmpty()))
                }
            )
        }
    }

    private fun loadPhotos(directory: FileModel.Directory) {
        mutableState.update { state ->
            state.copy(
                currentDirectory = directory.toUi(),
                isNestedDirectory = directoryStack.size > 1,
                directoryPath = directoryStack.joinToString(" / ") { it.name },
            )
        }
        if (mutableState.value.contentState is GalleryUiState.Content.Error) {
            mutableState.update { it.copy(contentState = GalleryUiState.Content.Loading) }
        }
        screenModelScope.launch {
            val itemsResult = runCatching {
                when (mutableState.value.viewType) {
                    UiGalleryViewType.Chronology -> photoRepository.getAllPhotos(directory.uri).map { it.toUi() }
                    UiGalleryViewType.Tree -> photoRepository.getImagesTree(directory.uri).mapNotNull { it.toUi() }
                }
            }
            itemsResult.fold(
                onSuccess = { items ->
                    mutableState.update { state ->
                        state.copy(
                            contentState = GalleryUiState.Content.Success(
                                backgroundImageUri = items.filterIsInstance<UiGalleryItem.Image>().randomOrNull()?.image?.uri,
                                items = items,
                            )
                        )
                    }
                },
                onFailure = { exception ->
                    mutableState.update { state ->
                        state.copy(
                            contentState = GalleryUiState.Content.Error(
                                error = exception.message.orEmpty(),
                            )
                        )
                    }
                }
            )
        }
    }
}