package br.com.mdr.weather.presentation.components.weatherAnimation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun FragmentingDroplets(width: Float, height: Float, dropIntensity: Int) {
    val droplets = remember { MutableList(dropIntensity) { DropletState(width, height) } }

    droplets.forEach { droplet ->
        DropletAnimation(droplet)
    }
}

@Composable
fun DropletAnimation(droplet: DropletState) {
    val infiniteTransition = rememberInfiniteTransition()
    val progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = droplet.duration,
                easing = LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "progress"
    )

    LaunchedEffect(progress.value) {
        // Aguarda o atraso inicial antes de começar
        if (progress.value == 0f) {
            delay(droplet.initialDelay)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().background(Color.Yellow)) {
        val x = droplet.startX + droplet.arcDirection * progress.value * droplet.arcWidth
        val y = droplet.startY - (4 * droplet.arcHeight * progress.value * (1 - progress.value))
        val alpha = 0.6f - progress.value

        if (progress.value < 1f) {
            drawCircle(
                color = droplet.color.copy(alpha = alpha),
                center = Offset(x, y),
                radius = droplet.radius
            )
        } else {
            // Reposiciona a gota ao final do ciclo
            droplet.resetPosition()
        }
    }
}

class DropletState(private val maxWidth: Float, private val maxHeight: Float) {
    var startX: Float by mutableStateOf(Random.nextFloat() * maxWidth)
    var startY: Float = maxHeight
    var radius: Float = Random.nextFloat() * 5f + 5f
    var arcWidth: Float = Random.nextFloat() * 50f + 100f
    var arcHeight: Float = Random.nextFloat() * 40f + 10f
    var arcDirection: Int = if (Random.nextBoolean()) 1 else -1
    var color: Color = Color.White.copy(alpha = 0.4f)
    var duration: Int = Random.nextInt(500, 1500)
    var initialDelay: Long = Random.nextLong(0, 1000)

    fun resetPosition() {
        startX = Random.nextFloat() * maxWidth
        radius = Random.nextFloat() * 5f + 5f
        arcWidth = Random.nextFloat() * 50f + 100f
        arcHeight = Random.nextFloat() * 40f + 10f
        arcDirection = if (Random.nextBoolean()) 1 else -1
        duration = Random.nextInt(500, 1500)
        initialDelay = Random.nextLong(0, 1000)
    }
}