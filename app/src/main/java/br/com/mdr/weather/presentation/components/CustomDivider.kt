package br.com.mdr.weather.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING

@Composable
fun CustomDivider(modifier: Modifier, color: Color = Color.Transparent, isVertical: Boolean = false) {
    if (isVertical) {
        VerticalDivider(modifier, color = color)
    } else {
        HorizontalDivider(modifier, color = color)
    }
}