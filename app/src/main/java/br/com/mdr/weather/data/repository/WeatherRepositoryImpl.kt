package br.com.mdr.weather.data.repository

import br.com.mdr.weather.core.domain.model.Weather
import br.com.mdr.weather.core.domain.repository.WeatherRepository
import br.com.mdr.weather.data.model.ForecastResponse
import br.com.mdr.weather.data.model.WeatherResponse
import br.com.mdr.weather.data.remote.WeatherApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
): WeatherRepository {
    override suspend fun getWeather(lat: String, lon: String): Flow<WeatherResponse> {
        return flow {
            val response = api.getWeatherData(lat, lon)
            emit(response)
        }
    }

    override suspend fun getForecast(lat: String, lon: String): Flow<ForecastResponse> {
        return flow {
            val response = api.getHourlyForecast(lat, lon)
            emit(response)
        }
    }
}