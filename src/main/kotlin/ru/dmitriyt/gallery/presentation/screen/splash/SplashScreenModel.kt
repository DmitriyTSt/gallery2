package ru.dmitriyt.gallery.presentation.screen.splash

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.domain.repository.FileRepository
import ru.dmitriyt.gallery.domain.repository.SettingsRepository

class SplashScreenModel(
    private val settingsRepository: SettingsRepository,
    private val fileRepository: FileRepository,
) : StateScreenModel<SplashUiState>(SplashUiState.Loading) {

    private val mutableEvent = MutableSharedFlow<SplashUiEvent>(replay = 1)
    val event: SharedFlow<SplashUiEvent> = mutableEvent.asSharedFlow()

    fun init() {
        mutableState.value = SplashUiState.Loading
        screenModelScope.launch {
            val settingsResult = runCatching { settingsRepository.getSettings() }
            val directory = settingsResult.getOrNull()?.photoArchiveUri
                ?.let { fileRepository.getFile(it) }
                as? FileModel.Directory
            if (directory != null) {
                mutableEvent.emit(SplashUiEvent.OpenGallery(directory))
            } else {
                mutableState.value = SplashUiState.SelectDirectory
            }
        }
    }

    fun selectDirectory(directoryUri: String) {
        mutableState.value = SplashUiState.Loading
        screenModelScope.launch {
            val directoryResult = runCatching {
                val settings = settingsRepository.getSettings()
                settingsRepository.storeSettings(settings.copy(photoArchiveUri = directoryUri))
                fileRepository.getFile(directoryUri) as FileModel.Directory
            }
            directoryResult.fold(
                onSuccess = {
                    mutableEvent.emit(SplashUiEvent.OpenGallery(it))
                },
                onFailure = {
                    mutableState.value = SplashUiState.SelectDirectory
                }
            )
        }
    }
}
