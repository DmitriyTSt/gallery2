package ru.dmitriyt.gallery.presentation.screen.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem
import ru.dmitriyt.gallery.presentation.screen.gallery.model.toUi
import ru.dmitriyt.gallery.presentation.screen.splash.SplashScreen

data class GalleryScreen(
    val rootDir: FileModel.Directory,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val files = rootDir.files.mapNotNull {
            when (it) {
                is FileModel.Directory -> it.toUi()
                is FileModel.Image -> it.toUi()
                is FileModel.Other -> null
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val backgroundImageItem = files.filterIsInstance<UiGalleryItem.Image>().randomOrNull()
            AsyncImage(
                model = backgroundImageItem?.image?.uri,
                contentDescription = backgroundImageItem?.image?.name,
                modifier = Modifier.fillMaxSize().blur(32.dp),
                contentScale = ContentScale.Crop,
            )
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                FloatingActionButton(
                    onClick = {
                        navigator.pop()
                    },
                    modifier = Modifier.padding(16.dp),
                    backgroundColor = Color.DarkGray,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
                FloatingActionButton(
                    onClick = {
                        navigator.replace(SplashScreen())
                    },
                    modifier = Modifier.padding(16.dp),
                    backgroundColor = Color.DarkGray,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(232.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 68.dp)
                    ) {
                        items(files.size) {
                            when (val item = files[it]) {
                                is UiGalleryItem.Directory -> DirectoryItem(item = item, onClick = { directory ->
                                    navigator.push(GalleryScreen(directory.directory))
                                })
                                is UiGalleryItem.Image -> ImageItem(item = item)
                            }
                        }
                    }
                    Card(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(50),
                        backgroundColor = Color.DarkGray,
                        contentColor = Color.White,
                        elevation = 16.dp,
                    ) {
                        Text(
                            text = rootDir.name,
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                        )
                    }
                }
            }
        }
    }
}