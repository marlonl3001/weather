package br.com.mdr.weather.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val name: String,
    val weather: List<WeatherDescription>,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val dt: Long,
    val visibility: Int,
    val clouds: Clouds,
    val rain: Rain?
)

data class Rain(
    @SerializedName("1h")
    val mmHour: Float
)

data class Clouds(
    val all: Int
)

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Float,
    @SerializedName("feels_like")
    val feelsLike: Float,
    val humidity: Int,
    @SerializedName("temp_max")
    val tempMax: Float,
    @SerializedName("temp_min")
    val tempMin: Float,
    @SerializedName("temp_kf")
    val tempKf: Float
)
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
) {
    fun getWindDirection(): String {
        val directions = listOf(
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
        )
        val index = ((deg / 22.5) + 0.5).toInt() % 16
        return "${deg}º ${directions[index]}"
    }
}

data class Sys(
    val sunrise: Long,
    val sunset: Long
)