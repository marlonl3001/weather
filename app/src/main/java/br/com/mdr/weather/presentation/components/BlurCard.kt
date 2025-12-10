package br.com.mdr.weather.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect

@Composable
fun BlurCard(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(MEDIUM_PADDING))
            .hazeEffect(state = hazeState)
            .background(Color.Transparent)
    ) {
        content() // Conteúdo interno permanece visível
    }
}