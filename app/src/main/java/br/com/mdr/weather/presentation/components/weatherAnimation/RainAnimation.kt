package br.com.mdr.weather.presentation.components.weatherAnimation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun RainAnimation(modifier: Modifier = Modifier, rainIntensity: Int) {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    val drops = remember {
        mutableStateListOf<RainDrop>().apply {
            repeat(rainIntensity) { // Número fixo para teste
                add(
                    RainDrop(
                        x = (0f..screenWidth).random(),
                        y = (0f..screenHeight).random(), // Início no topo
                        width = (3f..10f).random(),
                        height = (40f..110f).random(),
                        alpha = (0.05f..0.2f).random(),
                        speed = (8000f..9000f).random()
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                drops.forEachIndexed { index, drop ->
                    val newY = drop.y + drop.speed * (16 / 1000f)
                    drops[index] = drop.copy(
                        y = if (newY > screenHeight) -drop.height else newY,
                        x = if (newY > screenHeight) (0f..screenWidth).random() else drop.x
                    )
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drops.forEach { drop ->
            drawRainDrop(
                color = Color.White.copy(alpha = drop.alpha),
                centerX = drop.x,
                centerY = drop.y,
                width = drop.width,
                height = drop.height
            )
        }
    }
}

data class RainDrop(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val alpha: Float,
    val speed: Float
)

fun DrawScope.drawRainDrop(
    color: Color,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float
) {
    val halfWidth = width / 2
    val topRadius = width / 2
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

fun ClosedFloatingPointRange<Float>.random(): Float =
    (start + Math.random() * (endInclusive - start)).toFloat()
