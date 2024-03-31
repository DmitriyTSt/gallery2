package ru.dmitriyt.gallery.presentation.screen.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.dmitriyt.gallery.presentation.galleryasyncimage.GalleryAsyncImage
import ru.dmitriyt.gallery.presentation.screen.photo.view.PhotoView

data class PhotoScreen(
    val params: PhotoScreenParams,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<PhotoScreenModel>()
        val screenState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.init(params)
        }

        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Color(0xFFEAD5E6), Color(0xFFE7C2E1))))
        ) {
            params.backgroundImageUri?.let { backgroundImagesUri ->
                GalleryAsyncImage(
                    model = backgroundImagesUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().blur(32.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            ImageStateView(modifier = Modifier.align(Alignment.Center), photoUiState = screenState)
            FloatingActionButton(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
                onClick = {
                    screenModel.onPrevClick()
                },
                backgroundColor = Color.DarkGray,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
            FloatingActionButton(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
                onClick = {
                    screenModel.onNextClick()
                },
                backgroundColor = Color.DarkGray,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                )
            }

            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    navigator.pop()
                },
                backgroundColor = Color.DarkGray,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun ImageStateView(modifier: Modifier, photoUiState: PhotoUiState) {
    when (photoUiState) {
        is PhotoUiState.Error -> Column(modifier = modifier) {
            Image(
                painter = rememberVectorPainter(Icons.Default.Warning),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                contentDescription = null,
            )
            Text(
                text = photoUiState.error,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }
        is PhotoUiState.Loading -> CircularProgressIndicator(modifier = modifier)
        is PhotoUiState.Success -> {
            PhotoView(
                modifier = modifier.fillMaxSize(),
                image = photoUiState.currentImage,
            )
        }
    }
}