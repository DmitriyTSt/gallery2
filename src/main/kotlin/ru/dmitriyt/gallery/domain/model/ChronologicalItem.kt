package ru.dmitriyt.gallery.domain.model

import java.time.LocalDateTime

sealed class ChronologicalItem {
    data class Month(val monthDateTime: LocalDateTime) : ChronologicalItem()
    data class Image(val image: FileModel.Image) : ChronologicalItem()
}