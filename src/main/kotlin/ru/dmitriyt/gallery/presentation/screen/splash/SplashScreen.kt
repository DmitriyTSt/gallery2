package ru.dmitriyt.gallery.presentation.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import kotlinx.coroutines.flow.collectLatest
import ru.dmitriyt.gallery.presentation.screen.gallery.GalleryScreen
import javax.swing.JFileChooser
import javax.swing.UIManager

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SplashScreenModel>()
        val screenState by screenModel.state.collectAsState()
        var showDirectoryPicker by remember { mutableStateOf(false) }

        DirectoryPicker(showDirectoryPicker) { directoryUri ->
            showDirectoryPicker = false
            if (directoryUri != null) {
                screenModel.selectDirectory(directoryUri)
            }
        }

        LaunchedEffect(Unit) {
            screenModel.event.collectLatest { event ->
                when (event) {
                    is SplashUiEvent.OpenGallery -> navigator.replace(GalleryScreen(event.directory))
                }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.init()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Color(0xFFEAD5E6), Color(0xFFE7C2E1)))),
            contentAlignment = Alignment.Center,
        ) {
            when (screenState) {
                SplashUiState.Loading -> CircularProgressIndicator()
                is SplashUiState.Caching -> {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(text = "Создание миниатюр... (${(screenState as SplashUiState.Caching).progressPercent}%)")
                    }
                }
                SplashUiState.SelectDirectory -> {
                    Button(
                        onClick = {
                            showDirectoryPicker = true
                        },
                        modifier = Modifier.align(Alignment.Center),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.DarkGray,
                            contentColor = Color.White,
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
                    ) {
                        Text(text = "Выбрать директорию")
                    }
                }
            }
        }
    }

    private fun selectWithJFileChooser(): String? {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        val f = JFileChooser()
        f.setDialogTitle("Выбор директории с фото")
        f.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val result = try {
            f.showSaveDialog(null)
        } catch (e: Exception) {
            return null
        }
        return if (result == JFileChooser.APPROVE_OPTION) {
            f.selectedFile?.absolutePath
        } else {
            null
        }
    }
}