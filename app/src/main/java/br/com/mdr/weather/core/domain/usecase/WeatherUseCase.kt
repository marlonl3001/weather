package br.com.mdr.weather.core.domain.usecase

import br.com.mdr.weather.core.domain.mapper.WeatherMapper.mapResponseToWeatherData
import br.com.mdr.weather.core.domain.model.Weather
import br.com.mdr.weather.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend fun fetchWeather(lat: Double, lon: Double): Weather? {
        val weatherResponse = repository.getWeather(lat.toString(), lon.toString())
        val forecast = repository.getForecast(lat.toString(), lon.toString())

        var weather: Weather? = null
        weatherResponse.zip(forecast) { w, f ->
            return@zip mapResponseToWeatherData(w, f)
        }.collect {
            weather = it
        }
        return weather
    }
}

class HourlyForecast(
    val hour: String,
    val icon: String,
    val temp: String
)

class DailyForecast(
    val day: String,
    val icon: String,
    val currentTemp: Int,
    val tempMin: Int,
    val tempMax: Int,
    val overallTemp: Pair<Int, Int>? = null
)