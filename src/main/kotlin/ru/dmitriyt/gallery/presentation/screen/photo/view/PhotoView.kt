package ru.dmitriyt.gallery.presentation.screen.photo.view

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import ru.dmitriyt.gallery.domain.model.FileModel
import ru.dmitriyt.gallery.presentation.galleryasyncimage.GalleryAsyncImage
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhotoView(modifier: Modifier = Modifier, image: FileModel.Image) {
    var mousePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var photoSize by remember { mutableStateOf(IntSize(0, 0)) }

    LaunchedEffect(image) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }

    GalleryAsyncImage(
        model = image.uri,
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY,
            )
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    val scaleDivider = photoSize.height / 2
                    val oldScale = scale
                    scale = max(oldScale + delta * scale / scaleDivider, 1f)
                    val centerX = photoSize.width / 2
                    val centerY = photoSize.height / 2
                    val mousePositionXDelta = centerX - mousePosition.x
                    val mousePositionYDelta = centerY - mousePosition.y
                    val scaledMousePositionXDelta = scale * mousePositionXDelta
                    val scaledMousePositionYDelta = scale * mousePositionYDelta
                    offsetX = (scaledMousePositionXDelta - mousePositionXDelta)
                    offsetY = (scaledMousePositionYDelta - mousePositionYDelta)
                    delta
                }
            )
            .onSizeChanged { size ->
                photoSize = size
            }
            .onPointerEvent(PointerEventType.Move) {
                mousePosition = it.changes.first().position
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    if (scale != 1f) {
                        offsetX += dragAmount.x * scale
                        offsetY += dragAmount.y * scale
                    }
                }
            },
        contentDescription = null,
        contentScale = ContentScale.Fit,
    )
}
