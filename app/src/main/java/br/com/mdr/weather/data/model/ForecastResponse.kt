package br.com.mdr.weather.data.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse (
    @SerializedName("list" )
    val forecasts : ArrayList<Forecast> = arrayListOf(),
)

data class Forecast (
    val dt: Long,
    val main: Main,
    val weather: ArrayList<WeatherDescription> = arrayListOf(),
    val wind: Wind,
    val visibility : Int,
    val pop: Float,
    val sys: Sys,
    @SerializedName("dt_txt")
    val dtTxt: String
)