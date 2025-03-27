package br.com.mdr.weather.core.domain.repository

import br.com.mdr.weather.core.domain.model.Weather
import br.com.mdr.weather.data.model.ForecastResponse
import br.com.mdr.weather.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeather(lat: String, lon: String): Flow<WeatherResponse>
    suspend fun getForecast(lat: String, lon: String): Flow<ForecastResponse>
}