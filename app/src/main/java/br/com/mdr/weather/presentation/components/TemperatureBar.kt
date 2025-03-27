package br.com.mdr.weather.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING

@Composable
fun TemperatureBar(
    isFirstItem: Boolean = false,
    currentTemp: Int, //Temperatura atual
    minTemp: Int, // Temperatura mínima do dia
    maxTemp: Int, // Temperatura máxima do dia
    overallTemp: Pair<Int, Int>, // Temperatura mínima e máxima do intervalo geral
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(
        Color(0xFF87CEEB), // Azul claro (temperaturas mais frias)
        Color(0xFFFFFF00), // Amarelo (temperaturas médias)
        Color(0xFFFF8C00)  // Laranja (temperaturas quentes)
    )

    Canvas (
        modifier = modifier
            .fillMaxWidth()
            .height(MEDIUM_PADDING)
    ) {
        val barHeight = size.height
        val barWidth = size.width

        // Fundo cinza (toda a barra)
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.2f),
            size = size,
            cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)
        )

        // Calcular posição relativa das temperaturas dentro do intervalo
        val maxAverageTemp = if (maxTemp > overallTemp.second ) overallTemp.second else maxTemp
        val startFraction = (minTemp - overallTemp.first).toFloat() / (overallTemp.second - overallTemp.first).toFloat()
        val endFraction = (barWidth / overallTemp.second) * maxAverageTemp//(maxTemp - overallTemp.first).toFloat() / (maxAverageTemp - overallTemp.first).toFloat()

        val startX = barWidth * startFraction
        val endX = endFraction//barWidth * endFraction

        // Gradiente representando o intervalo de temperaturas
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = gradientColors,
                startX = startX,
                endX = endX
            ),
            topLeft = Offset(startX, 0f),
            size = Size(endX - startX, barHeight),
            cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)
        )

        if (isFirstItem) {
            val circleFraction = (currentTemp - overallTemp.first).toFloat() / (maxAverageTemp - overallTemp.first).toFloat()
            val circlePos = barWidth * circleFraction
            drawCircle(
                color = Color.Gray,
                radius = barHeight / 2,
                center = Offset(circlePos, barHeight / 2)
            )
            drawCircle(
                color = Color.White,
                radius = barHeight / 2.5f,
                center = Offset(circlePos, barHeight / 2)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TemperatureBarPreview() {
    TemperatureBar(
        false,
        21,
        17,
        23,
        15 to 28
    )
}