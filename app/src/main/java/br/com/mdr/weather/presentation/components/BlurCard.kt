package br.com.mdr.weather.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING
import br.com.mdr.weather.presentation.ui.theme.TransparentWhite

@Composable
fun BlurCard(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(MEDIUM_PADDING)) // Bordas arredondadas
            .background(backgroundColor.copy(alpha = 0.7f)) // Fundo translúcido
            .drawBehind {
                // Desfoque aplicado ao fundo
                drawRect(
                    color = TransparentWhite,
                    blendMode = BlendMode.SrcOver
                )
            }
    ) {
        content() // Conteúdo interno permanece visível
    }
}