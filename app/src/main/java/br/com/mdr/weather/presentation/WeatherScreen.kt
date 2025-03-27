package br.com.mdr.weather.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mdr.weather.R
import br.com.mdr.weather.core.domain.model.Weather
import br.com.mdr.weather.presentation.components.CustomDivider
import br.com.mdr.weather.presentation.components.DailyForecast
import br.com.mdr.weather.presentation.components.HourlyForecastItem
import br.com.mdr.weather.presentation.components.WeatherBackground
import br.com.mdr.weather.presentation.components.WeatherInfoCard
import br.com.mdr.weather.presentation.components.WindInfo
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING
import br.com.mdr.weather.presentation.ui.theme.SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.TOP_APP_BAR_HEIGHT
import br.com.mdr.weather.presentation.ui.theme.fontShadow
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val weather by viewModel.weatherState.collectAsStateWithLifecycle()
    val isLoading by viewModel.enableLoading.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val cardWidth = configuration.screenWidthDp.dp / 2

    if (!isLoading) {
        weather?.let {
            WeatherBackground(weatherCondition = it.weatherCondition)

            Box(modifier = Modifier.fillMaxSize().padding(horizontal = MEDIUM_PADDING)) {
                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                ) {
                    item {
                        CustomDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TOP_APP_BAR_HEIGHT),
                            isVertical = true
                        )
                    }
                    item { WeatherHeader(it) }
                    item {
                        CustomDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MEDIUM_PADDING),
                            isVertical = true
                        )
                    }
                    item {
                        HourlyForecastItem(
                            hourlyForecast = it.hourlyForecast,
                            weatherCondition = it.weatherCondition
                        )
                    }
                    item {
                        DailyForecast(
                            backgroundColor = it.weatherCondition.skyColor.first,
                            dailyData = it.dailyForecast
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
                        ) {
                            WeatherInfoCard(
                                Modifier.weight(1f),
                                it.weatherCondition.skyColor.first,
                                R.drawable.ic_thermometer,
                                "Sensação",
                                "${weather?.feelsLike?.roundToInt()}º",
                                "Similar à temperatura real."
                            )
                            WeatherInfoCard(
                                Modifier.weight(1f),
                                it.weatherCondition.skyColor.first,
                                R.drawable.ic_visibility,
                                "Visibilidade",
                                "${it.getVisibility()} KM",
                                "Visibilidade excelente."
                            )
                        }
                    }
                    item {
                        WindInfo(
                            it.weatherCondition.skyColor.first,
                            it.wind
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
                        ) {
                            WeatherInfoCard(
                                Modifier.weight(1f),
                                backgroundColor = it.weatherCondition.skyColor.first,
                                icon = R.drawable.ic_water,
                                title = "Umidade",
                                info = "${it.humidity}%",
                                description = "O ponto de orvalho é de 17º agora."
                            )
                            CustomDivider(
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }
                    item {
                        CustomDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MEDIUM_PADDING),
                            isVertical = true
                        )
                    }
                }
            }
        } ?: Text(text = "Erro ao buscar dados do tempo!", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun WeatherHeader(weather: Weather) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weather.city,
            style = MaterialTheme.typography.headlineMedium.copy(
                shadow = fontShadow
            )
        )
        Text(
            text = "${weather.currentTemp}°",
            style = MaterialTheme.typography.displayLarge.copy(
                shadow = fontShadow
            )
        )
        Text(
            text = weather.condition.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            },
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = fontShadow
            )
        )
        Text(
            text = "Máx.: ${weather.tempMax}° Mín.: ${weather.tempMin}°",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = fontShadow
            )
        )
    }
}