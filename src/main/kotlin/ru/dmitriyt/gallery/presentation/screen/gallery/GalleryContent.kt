package ru.dmitriyt.gallery.presentation.screen.gallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem
import ru.dmitriyt.gallery.presentation.screen.gallery.views.DirectoryItem
import ru.dmitriyt.gallery.presentation.screen.gallery.views.ImageItem
import ru.dmitriyt.gallery.presentation.screen.gallery.views.MonthDividerItem

@Composable
fun GalleryContent(
    contentState: GalleryUiState.Content.Success,
    changeDirectory: (FileModel.Directory) -> Unit,
    openPhoto: (FileModel.Image) -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(232.dp),
        modifier = modifier,
        contentPadding = PaddingValues(top = 84.dp),
        state = lazyGridState,
    ) {
        contentState.items.forEach { item ->
            item(
                span = {
                    GridItemSpan(
                        if (item is UiGalleryItem.MonthDivider) {
                            maxLineSpan
                        } else {
                            1
                        }
                    )
                },
                key = when (item) {
                    is UiGalleryItem.Directory -> "D_${item.directory.uri}"
                    is UiGalleryItem.Image -> "I_${item.image.uri}"
                    is UiGalleryItem.MonthDivider -> "M_${item.month}"
                },
                contentType = when (item) {
                    is UiGalleryItem.Directory -> "D_${item.directory.uri}"
                    is UiGalleryItem.Image -> "I_${item.image.uri}"
                    is UiGalleryItem.MonthDivider -> "M_${item.month}"
                },
            ) {
                when (item) {
                    is UiGalleryItem.Directory -> DirectoryItem(
                        item = item,
                        onClick = { directory ->
                            changeDirectory(directory.directory)
                        })

                    is UiGalleryItem.Image -> ImageItem(item = item, onClick = { image ->
                        openPhoto(image.image)
                    })
                    is UiGalleryItem.MonthDivider -> MonthDividerItem(item = item)
                }
            }
        }
    }
}