package ru.dmitriyt.gallery.presentation.utils

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ru.dmitriyt.logger.Logger

@Composable
fun rememberKeysLazyGridState(
    key1: Any?,
    key2: Any?,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyGridState {
    val states = remember { HashMap<Pair<Any?, Any?>, LazyGridState>() }
    Logger.d("scroll states get for ${key1 to key2}")
    val state = states[key1 to key2] ?: run {
        val state = LazyGridState(
            firstVisibleItemIndex = initialFirstVisibleItemIndex,
            firstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
        )
        states[key1 to key2] = state
        state
    }
    Logger.d("scroll states count ${states.size}")
    return state
}
