package ru.dmitriyt.gallery.presentation.screen.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem
import ru.dmitriyt.gallery.presentation.screen.splash.SplashScreen

data class GalleryScreen(
    val rootDir: FileModel.Directory,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<GalleryScreenModel>()
        val screenState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.init(rootDir)
        }

        LaunchedEffect(Unit) {
            screenModel.event.collectLatest { event ->
                when (event) {
                    is GalleryUiEvent.OpenSplash -> navigator.replace(SplashScreen())
                    is GalleryUiEvent.ShowError -> Unit
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            AsyncImage(
                model = screenState.contentState.getOrNull()?.backgroundImageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().blur(32.dp),
                contentScale = ContentScale.Crop,
            )
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    if (screenState.canGoBack) {
                        FloatingActionButton(
                            onClick = {
                                screenModel.onBackClick()
                            },
                            modifier = Modifier.padding(8.dp),
                            backgroundColor = Color.DarkGray,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            screenModel.closeRootDirectory()
                        },
                        modifier = Modifier.padding(8.dp),
                        backgroundColor = Color.DarkGray,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (val contentState = screenState.contentState) {
                        is GalleryUiState.Content.Error -> {
                            Text(text = contentState.error, modifier = Modifier.align(Alignment.Center))
                        }
                        GalleryUiState.Content.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        is GalleryUiState.Content.Success -> {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(232.dp),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 68.dp)
                            ) {
                                items(contentState.items.size) {
                                    when (val item = contentState.items[it]) {
                                        is UiGalleryItem.Directory -> DirectoryItem(item = item, onClick = { directory ->
                                            screenModel.changeDirectory(directory.directory)
                                        })
                                        is UiGalleryItem.Image -> ImageItem(item = item)
                                    }
                                }
                            }
                        }
                    }
                    screenState.currentDirectory?.directory?.name?.let { directoryName ->
                        Card(
                            modifier = Modifier.padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
                            shape = RoundedCornerShape(50),
                            backgroundColor = Color.DarkGray,
                            contentColor = Color.White,
                            elevation = 16.dp,
                        ) {
                            Text(
                                text = directoryName,
                                modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}