package ru.dmitriyt.gallery.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.dmitriyt.gallery.domain.model.ChronologicalItem
import ru.dmitriyt.gallery.domain.repository.ImageRepository
import ru.dmitriyt.gallery.domain.repository.PhotoRepository

class CacheImagesUseCase(
    private val imageRepository: ImageRepository,
    private val photoRepository: PhotoRepository,
    private val cacheScope: CoroutineScope,
) {

    suspend operator fun invoke(params: Params): Flow<Result> = coroutineScope {
        val images = photoRepository.getAllPhotos(params.rootDirectoryUri).filterIsInstance<ChronologicalItem.Image>()
        val allCount = images.size
        val mutex = Mutex()
        var processed = 0

        val mutableStateFlow = MutableStateFlow<Result>(Result.Progress(0f))

        images.chunked(100).forEach { imageChunk ->
            cacheScope.launch {
                imageChunk.forEach { image ->
                    imageRepository.cacheImage(
                        imageUri = image.image.uri,
                        resizePx = params.resizePx,
                        force = false
                    )
                    mutex.withLock {
                        processed++
                        mutableStateFlow.value = if (processed == allCount) {
                            Result.Finish
                        } else {
                            Result.Progress(processed.toFloat() / allCount)
                        }
                    }
                }
            }
        }

        mutableStateFlow
    }

    data class Params(
        val rootDirectoryUri: String,
        val resizePx: Int,
    )

    sealed interface Result {
        data class Progress(val progress: Float) : Result
        data object Finish : Result
    }
}