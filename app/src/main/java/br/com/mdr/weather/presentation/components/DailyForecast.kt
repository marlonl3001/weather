package br.com.mdr.weather.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.mdr.weather.R
import br.com.mdr.weather.commons.getResId
import br.com.mdr.weather.core.domain.usecase.DailyForecast
import br.com.mdr.weather.core.util.Constants
import br.com.mdr.weather.presentation.ui.theme.BottomDarkSkyBlue
import br.com.mdr.weather.presentation.ui.theme.EXTRA_SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING
import br.com.mdr.weather.presentation.ui.theme.SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.TransparentWhite
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun DailyForecast(backgroundColor: Color, dailyData: List<DailyForecast>) {
    BlurCard(
        backgroundColor = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(MEDIUM_PADDING),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(EXTRA_SMALL_PADDING)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "",
                    tint = TransparentWhite
                )
                Text(
                    //modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
                    text = "PREVISÃO PARA 5 DIAS",
                    style = MaterialTheme.typography.titleMedium,
                    color = TransparentWhite,
                )
            }

            CustomDivider(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(
                        start = MEDIUM_PADDING,
                        top = 0.dp,
                        end = MEDIUM_PADDING,
                        bottom = MEDIUM_PADDING
                    ),
                TransparentWhite
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)
            ) {
                dailyData.forEach { day ->
                    DailyForecastItem(day, day == dailyData.first())
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(forecast: DailyForecast, isFirstItem: Boolean) {
    val context = LocalContext.current

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MEDIUM_PADDING, vertical = SMALL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = forecast.day,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Icon(
            modifier = Modifier
                .size(40.dp)
                .padding(EXTRA_SMALL_PADDING),
            painter = painterResource(id = context.getResId(forecast.icon)),
            contentDescription = "Weather Icon",
            tint = Color.Unspecified
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(horizontal = SMALL_PADDING),
                text = "${forecast.tempMin}°",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            forecast.overallTemp?.let {
                TemperatureBar(
                    isFirstItem,
                    forecast.currentTemp,
                    forecast.tempMin,
                    forecast.tempMax,
                    forecast.overallTemp,
                    Modifier
                        .size(width = 100.dp, height = 5.dp)
                )
            }
            Text(
                modifier = Modifier.padding(start = SMALL_PADDING),
                text = "${forecast.tempMax}°",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
    CustomDivider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(
                horizontal = MEDIUM_PADDING
            ),
        color = TransparentWhite
    )
}

@Preview
@Composable
fun DailyForecastPreview() {
    val forecasts = mutableListOf<DailyForecast>()

    for (i in 0..6) {
        val day = when (i) {
            0 -> "Dom."
            1 -> "Seg."
            2 -> "Ter."
            3 -> "Qua."
            4 -> "Qui."
            else -> "Sex."
        }
        val forecast = DailyForecast(
            day,
            "-4d",
            18,
            15,
            26,
            10 to 32
        )
        forecasts.add(forecast)
    }
    DailyForecast(BottomDarkSkyBlue, forecasts)
}