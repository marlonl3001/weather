package br.com.mdr.weather.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RainDropDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.DarkGray)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRainDrop(
                color = Color.White.copy(alpha = 0.1f),
                centerX = size.width / 2,
                centerY = 50f,
                width = 10f,  // Largura da gota
                height = 110f // Altura da gota
            )
        }
    }
}

fun DrawScope.drawRainDrop(
    color: Color,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float
) {
    val halfWidth = width / 2
    val topRadius = width / 2 // Raio da parte inferior (círculo invertido)
    val path = Path().apply {
        // Move para a ponta superior da gota
        moveTo(centerX, centerY - height / 2)

        // Curva subindo para o lado superior direito
        quadraticTo(
            centerX + halfWidth, // Controle (arredondado)
            centerY + halfWidth, // Meio da gota
            centerX + halfWidth, // Base arredondada direita
            centerY + height / 2 // Base inferior
        )

        // Desenha o arco inferior (círculo invertido)
        arcTo(
            rect = Rect(
                centerX - topRadius,
                centerY + height / 2 - topRadius,
                centerX + topRadius,
                centerY + height / 2 + topRadius
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )

        // Curva subindo para o lado superior esquerdo
        quadraticTo(
            centerX - halfWidth, // Controle (arredondado)
            centerY, // Meio da gota
            centerX, // Ponta superior esquerda
            centerY - height / 2 // Ponta superior
        )
    }

    drawPath(
        path = path,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
fun DropPreview() {
    RainDropDemo()
}