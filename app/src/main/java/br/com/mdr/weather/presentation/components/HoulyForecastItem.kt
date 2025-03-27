package br.com.mdr.weather.presentation.components

import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import br.com.mdr.weather.commons.getResId
import br.com.mdr.weather.core.domain.model.WeatherCondition
import br.com.mdr.weather.core.domain.usecase.HourlyForecast
import br.com.mdr.weather.presentation.components.weatherAnimation.FragmentingDroplets
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING
import br.com.mdr.weather.presentation.ui.theme.SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.TransparentWhite

val shader = RuntimeShader("""
    uniform shader composable;
    uniform float2 resolution;
    uniform float time;
    uniform float3 impactPoint; // Ponto de impacto da gota
    
    float circle(vec2 uv, vec2 center, float radius) {
        return smoothstep(radius, radius - 0.01, length(uv - center));
    }
    
    vec4 main(vec2 fragCoord) {
        vec2 uv = fragCoord / resolution;
    
        // Gota principal
        vec2 dropletPos = vec2(0.5, 0.8 - mod(time, 1.0)); // Queda da gota
        float droplet = circle(uv, dropletPos, 0.02);
    
        // Fragmentação ao colidir
        if (dropletPos.y < impactPoint.y) {
            vec2 frag1 = dropletPos + vec2(0.05, 0.1);
            vec2 frag2 = dropletPos + vec2(-0.05, 0.1);
            vec2 frag3 = dropletPos + vec2(0.0, 0.15);
    
            droplet += circle(uv, frag1, 0.01) * (1.0 - time * 0.1);
            droplet += circle(uv, frag2, 0.01) * (1.0 - time * 0.1);
            droplet += circle(uv, frag3, 0.01) * (1.0 - time * 0.1);
        }
    
        return vec4(vec3(droplet), 1.0);
    }

    """.trimIndent())
@Composable
fun HourlyForecastItem(hourlyForecast: List<HourlyForecast>, weatherCondition: WeatherCondition) {
    val context = LocalContext.current
    var itemSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                val position = layoutCoordinates.positionInRoot()
                itemSize = layoutCoordinates.size
            }
    ) {
        // Animação das gotas
        if (itemSize.width > 0) { // Evita iniciar antes do cálculo do tamanho
            FragmentingDroplets(
                width = itemSize.width.toFloat(),
                height = 0f, // Topo do componente,
                dropIntensity = weatherCondition.getDropIntensity()
            )
        }
        BlurCard(
            backgroundColor = weatherCondition.skyColor.first,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = MEDIUM_PADDING, vertical = SMALL_PADDING),
                    text = "Previsão de temperatura por hora.",
                    style = MaterialTheme.typography.titleMedium
                )
                CustomDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(
                            start = MEDIUM_PADDING,
                            top = 0.dp,
                            end = MEDIUM_PADDING,
                            bottom = MEDIUM_PADDING
                        ),
                    color = TransparentWhite
                )
                LazyRow(
                    modifier = Modifier.padding(vertical = MEDIUM_PADDING)
                ) {
                    itemsIndexed(hourlyForecast) { index, hourlyData ->
                        CustomDivider(Modifier.size(MEDIUM_PADDING))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = hourlyData.hour,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(SMALL_PADDING),
                                painter = painterResource(id = context.getResId(hourlyData.icon)),
                                contentDescription = "Weather Icon",
                                tint = Color.Unspecified
                            )
                            Text(
                                text = "${hourlyData.temp}°",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (index == hourlyForecast.size - 1) {
                            CustomDivider(Modifier.size(MEDIUM_PADDING))
                        }
                    }
                }
            }
        }
    }
}