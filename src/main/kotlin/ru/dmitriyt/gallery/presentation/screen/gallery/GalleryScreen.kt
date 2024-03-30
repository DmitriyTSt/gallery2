package ru.dmitriyt.gallery.presentation.screen.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.presentation.screen.splash.SplashScreen
import ru.dmitriyt.gallery.presentation.utils.rememberKeysLazyGridState
import ru.dmitriyt.gallery.presentation.galleryasyncimage.LocalGalleryAsyncImageModel
import ru.dmitriyt.logger.Logger

data class GalleryScreen(
    val rootDir: FileModel.Directory,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<GalleryScreenModel>()
        val screenState by screenModel.state.collectAsState()

        var titleExpanded by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            screenModel.init(rootDir)
        }

        LaunchedEffect(Unit) {
            screenModel.event.collectLatest { event ->
                when (event) {
                    is GalleryUiEvent.OpenSplash -> navigator.replace(SplashScreen())
                    is GalleryUiEvent.ShowError -> {
                        Logger.e(event.error)
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Color(0xFFEAD5E6), Color(0xFFE7C2E1)))),
        ) {
            screenState.contentState.getOrNull()?.backgroundImageUri?.let { backgroundImagesUri ->
                LocalGalleryAsyncImageModel.current.GalleryAsyncImage(
                    model = backgroundImagesUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().blur(32.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = null,
                    error = null,
                    size = null,
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    if (screenState.showBackIcon) {
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
                    } else {
                        Spacer(Modifier.height(72.dp))
                    }
                    FloatingActionButton(
                        onClick = {
                            screenModel.changeViewType()
                        },
                        modifier = Modifier.padding(8.dp),
                        backgroundColor = Color.DarkGray,
                    ) {
                        Icon(
                            imageVector = screenState.viewType.inverseIcon,
                            contentDescription = null,
                            tint = Color.White,
                        )
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
                            val scrollState = rememberKeysLazyGridState(screenState.viewType, screenState.currentDirectory)
                            GalleryContent(
                                contentState = contentState,
                                changeDirectory = screenModel::changeDirectory,
                                modifier = Modifier.fillMaxSize(),
                                lazyGridState = scrollState,
                            )
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
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val endPadding = if (screenState.showTitleExpandLogic) 8.dp else 24.dp
                                    Text(
                                        text = directoryName,
                                        modifier = Modifier.padding(
                                            top = 16.dp,
                                            bottom = 16.dp,
                                            start = 24.dp,
                                            end = endPadding
                                        ),
                                    )
                                    if (screenState.showTitleExpandLogic) {
                                        IconButton(
                                            onClick = {
                                                titleExpanded = !titleExpanded
                                            },
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (titleExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    }
                    if (titleExpanded && screenState.showTitleExpandLogic) {
                        Column {
                            Spacer(modifier = Modifier.height(84.dp))
                            Card(
                                shape = RoundedCornerShape(50),
                                backgroundColor = Color.DarkGray,
                                contentColor = Color.White,
                                elevation = 16.dp,
                            ) {
                                Text(
                                    text = screenState.directoryPath,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp, end = 16.dp),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}