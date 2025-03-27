package br.com.mdr.weather.core.domain.model

import androidx.compose.ui.graphics.Color
import br.com.mdr.weather.core.domain.usecase.DailyForecast
import br.com.mdr.weather.core.domain.usecase.HourlyForecast
import br.com.mdr.weather.data.model.Forecast
import br.com.mdr.weather.data.model.Wind
import br.com.mdr.weather.presentation.ui.theme.BottomCloudySky
import br.com.mdr.weather.presentation.ui.theme.BottomDarkSkyBlue
import br.com.mdr.weather.presentation.ui.theme.BottomDaySkyBlue
import br.com.mdr.weather.presentation.ui.theme.BottomMidSkyBlue
import br.com.mdr.weather.presentation.ui.theme.TopCloudySky
import br.com.mdr.weather.presentation.ui.theme.TopDarkSkyBlue
import br.com.mdr.weather.presentation.ui.theme.TopDaySkyBlue
import br.com.mdr.weather.presentation.ui.theme.TopMidSkyBlue
import kotlin.math.roundToInt

class Weather(
    val city: String,
    val currentTemp: Int,
    val condition: String,
    val tempMin: Float,
    val tempMax: Float,
    val feelsLike: Float,
    val humidity: Int,
    private val visibility: Int,
    val forecast: List<Forecast>,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>,
    val weatherCondition: WeatherCondition,
    val wind: Wind
) {
    fun getVisibility(): Int =
        visibility / 100
}

class WeatherCondition(
    val timeOfDay: TimeOfDay,
    val cloudiness: Int,
    val isSunny: Boolean,
    val isRaining: Boolean,
    val isStormy: Boolean,
    val mmHour: Float? = null
) {
    val dayTimeFactor: Float
        get() = when (timeOfDay) {
            TimeOfDay.MORNING -> 0.7f
            TimeOfDay.AFTERNOON -> 0.5f
            else -> 0f
        }

    //Cor da nuvem, que irá variar de acordo com o tipo de dia e
    // condições climáticas
    val cloudDark: Float
        get() = getCloudDarkValue()

    //Valor que representa o quanto de nuvem irá cobrir a tela.
    // Cálculo é feito de acordo com a propriedade cloudiness
    val cloudCover: Float
        get() = getCloudCoverValue()

    //Controle da densidade das nuvens.
    val cloudDensity: Float
        get() = getCloudDensityValue()

    val skyColor: Pair<Color, Color>
        get() = getSkyColors()

    fun getDropIntensity(): Int {
        return mmHour?.let {
            (it * 30).roundToInt()
        } ?: 0
    }

    fun getRainIntensity(): Int {
        return mmHour?.let {
            (it * 100).roundToInt()
        } ?: 0
    }

    private fun getSkyColors(): Pair<Color, Color> {
        val colorTop = when (timeOfDay) {
            TimeOfDay.MORNING -> {
                if (isRaining || !isSunny) {
                    Color(0xFF3a3f43) // Cinza mais escuro
                } else {
                    TopDaySkyBlue // Azul noite
                }
            }
            TimeOfDay.AFTERNOON -> {
                if (isRaining || !isSunny) {
                    Color(0xFF3a3f43) // Cinza mais escuro
                } else {
                    TopMidSkyBlue // Azul noite
                }
            }
            TimeOfDay.NIGHT -> {
                if (isRaining || !isSunny) {
                    TopCloudySky // Cinza mais escuro
                } else {
                    TopDarkSkyBlue // Azul noite
                }
            }
        }

        val colorBottom = when (timeOfDay) {
            TimeOfDay.MORNING -> {
                if (isRaining || !isSunny) {
                    Color(0xFF202c36) // Cinza escuro
                } else {
                    BottomDaySkyBlue
                }
            }
            TimeOfDay.AFTERNOON -> {
                if (isRaining || !isSunny) {
                    Color(0xFF202c36) // Cinza escuro
                } else {
                    BottomMidSkyBlue
                }
            }
            TimeOfDay.NIGHT -> {
                if (isRaining || !isSunny) {
                    BottomCloudySky // Cinza escuro
                } else {
                    BottomDarkSkyBlue
                }
            }
        }
        return Pair(colorTop, colorBottom)
    }

    private fun getCloudDensityValue(): Float =
        when {
            cloudiness in 80..100 -> 1.0f
            cloudiness in 70.. 79 -> 1.5f
            cloudiness in 50 .. 69 -> 1.0f
            cloudiness in 40 .. 49 -> 0.6f
            cloudiness in 25 .. 39 -> 0.3f
            cloudiness in 16 ..24 -> 0.1f
            cloudiness in 6 ..15 -> 0.1f
            cloudiness in 1 .. 5 -> -2.0f
            else -> -10f
        }

    private fun getCloudDarkValue(): Float =
        when (timeOfDay) {
            TimeOfDay.MORNING, TimeOfDay.AFTERNOON ->  {
                if (isSunny) {
                    0.4f
                } else if (isRaining) {
                    0.3f
                } else {
                    0.1f
                }
            }
            else -> {
                if (isSunny) {
                    0.3f
                } else if (isRaining) {
                    0.2f
                } else {
                    0.1f
                }
            }
        }

    private fun getCloudCoverValue(): Float {
        return when {
            cloudiness > 95 -> 5.0f
            cloudiness in 80..95 -> 2.0f
            cloudiness in 70.. 79 -> 1.5f
            cloudiness in 50 .. 69 -> 1.0f
            cloudiness in 40 .. 49 -> 0.6f
            cloudiness in 25 .. 39 -> 0.3f
            cloudiness in 16 ..24 -> 0.1f
            cloudiness in 6 ..15 -> 0.1f
            cloudiness in 1 .. 5 -> -2.0f
            else -> -10f
        }
    }
}

enum class TimeOfDay {
    MORNING,
    AFTERNOON,
    NIGHT
}

enum class WeatherDescription(val condition: String) {
    CLEAR("Clear"),
    CLOUDS("Clouds"),
    DRIZZLE("Drizzle"),
    RAIN("Rain"),
    THUNDERSTORM("Thunderstorm")
}
