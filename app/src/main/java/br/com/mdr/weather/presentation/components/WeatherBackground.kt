package br.com.mdr.weather.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import br.com.mdr.weather.core.domain.model.TimeOfDay
import br.com.mdr.weather.core.domain.model.WeatherCondition
import br.com.mdr.weather.presentation.components.shaders.CloudAnimation
import br.com.mdr.weather.presentation.components.shaders.MoonAnimation
import br.com.mdr.weather.presentation.components.shaders.SunAnimation
import br.com.mdr.weather.presentation.components.weatherAnimation.RainAnimation
import kotlinx.coroutines.delay
import kotlin.math.sin

data class Star(
    var x: Float,
    var y: Float,
    var alpha: Float,
    val speedX: Float = -0.02f
) {
    private val initialAlpha = alpha

    fun update(value: Float, width: Float) {
        val alphaX = (value - initialAlpha).toDouble()
        val newAlpha = 0.5f + (0.5f * sin(alphaX).toFloat())
        alpha = newAlpha
        x += speedX

        if (x < 0) x = width // Reseta para o final se sair da tela pelo lado esquerdo
    }
}

@Composable
fun WeatherBackground(weatherCondition: WeatherCondition) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fundo do céu
        SkyBackground(weatherCondition)

        // Sol ou elementos principais
        if (weatherCondition.isSunny && weatherCondition.timeOfDay != TimeOfDay.NIGHT) {
            SunAnimation()
        }
        // Nuvens
        if (weatherCondition.cloudiness > 0) {
            CloudAnimation(weatherCondition)
            //MoonAnimation()
        }

        // Chuva
        if (weatherCondition.isRaining || weatherCondition.isStormy) {
            RainAnimation(rainIntensity = weatherCondition.getRainIntensity())
        }
    }
}

@Composable
fun SkyBackground(weatherCondition: WeatherCondition) {
    val modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(weatherCondition.skyColor.first, weatherCondition.skyColor.second)
            )
        )

    if (weatherCondition.timeOfDay == TimeOfDay.NIGHT && !weatherCondition.isRaining  && weatherCondition.cloudiness < 80) {
        ClearSkyView(modifier)
    } else {
        Box(modifier)
    }
}

@Composable
private fun ClearSkyView(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val infinitelyAnimatedFloat = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    BoxWithConstraints(
        modifier = modifier
    ) {
        val dimens = with(LocalDensity.current) {
            Pair(maxWidth.toPx(), maxHeight.toPx())
        }

        val stars = remember {
            buildList {
                repeat(500) {
                    val x = (Math.random() * dimens.first).toFloat()
                    val y = (Math.random() * dimens.second).toFloat()
                    val alpha = (Math.random() * 2.0 * Math.PI).toFloat()
                    add(Star(x, y, alpha))
                }
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            for (star in stars) {
                star.update(infinitelyAnimatedFloat.value, dimens.first)
                drawCircle(
                    color = Color.White,
                    center = Offset(star.x, star.y),
                    radius = 2f,
                    alpha = star.alpha
                )
            }
        }
    }
}
