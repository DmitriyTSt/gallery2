package ru.dmitriyt.gallery.presentation.screen.gallery.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.dmitriyt.gallery.presentation.screen.gallery.model.UiGalleryItem

@Composable
fun MonthDividerItem(item: UiGalleryItem.MonthDivider, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
        shape = RoundedCornerShape(50),
        backgroundColor = Color.DarkGray,
        contentColor = Color.White,
        elevation = 8.dp,
    ) {
        Text(
            text = item.month,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        )
    }
}