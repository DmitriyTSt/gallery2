package ru.dmitriyt.gallery.presentation.screen.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem

@Composable
fun DirectoryItem(item: UiGalleryItem.Directory, onClick: (UiGalleryItem.Directory) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .clickable { onClick(item) }
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                item.images.ifEmpty { listOf(UiGalleryItem.Image.placeholder()) }.take(3).forEach {
                    ImageItem(it)
                }
            }
            Text(
                text = item.directory.name,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}