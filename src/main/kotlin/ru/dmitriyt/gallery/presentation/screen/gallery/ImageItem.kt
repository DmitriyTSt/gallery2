package ru.dmitriyt.gallery.presentation.screen.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem

@Composable
fun ImageItem(item: UiGalleryItem.Image, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .rotate(item.rotation)
            .padding(32.dp)
            .border(3.dp, Color.White)
            .shadow(8.dp)
    ) {
        AsyncImage(
            model = item.image.uri,
            contentDescription = item.image.name,
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.Gray)
                .fillMaxSize(),
            placeholder = painterResource("placeholder.svg"),
            error = painterResource("placeholder.svg"),
            contentScale = ContentScale.Crop,
        )
    }
}