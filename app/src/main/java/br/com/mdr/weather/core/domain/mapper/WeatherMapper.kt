package br.com.mdr.weather.core.domain.mapper

import br.com.mdr.weather.commons.asIso8601Date
import br.com.mdr.weather.core.domain.model.TimeOfDay
import br.com.mdr.weather.core.domain.model.Weather
import br.com.mdr.weather.core.domain.model.WeatherCondition
import br.com.mdr.weather.core.domain.model.WeatherDescription
import br.com.mdr.weather.core.domain.model.WeekDays
import br.com.mdr.weather.core.domain.usecase.DailyForecast
import br.com.mdr.weather.core.domain.usecase.HourlyForecast
import br.com.mdr.weather.data.model.Forecast
import br.com.mdr.weather.data.model.ForecastResponse
import br.com.mdr.weather.data.model.WeatherResponse
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private const val HOUR_NOW_DESCRIPTION = "Agora"

object WeatherMapper {
    fun mapResponseToWeatherData(
        weatherResponse: WeatherResponse,
        forecastResponse: ForecastResponse
    ): Weather {
        val mutableForecasts = mutableListOf<Forecast>()
        mutableForecasts.addAll(forecastResponse.forecasts)
        //Como a API retorna apenas dados com 5 horas de diferença, simula a previsão de hora em hora do dia de hoje
        val dailyFiltered = filterForecastForTodayAndUpcoming(mutableForecasts, 0)
        val calendar = Calendar.getInstance()

        return Weather(
            city = weatherResponse.name,
            currentTemp = weatherResponse.main.temp.roundToInt(),
            condition = weatherResponse.weather.first().description,
            tempMin = weatherResponse.main.tempMin,
            tempMax = weatherResponse.main.tempMax,
            feelsLike = weatherResponse.main.feelsLike,
            humidity = weatherResponse.main.humidity,
            visibility = weatherResponse.visibility,
            forecast = forecastResponse.forecasts,
            hourlyForecast = dailyFiltered.toHourlyForecasts(calendar),
            dailyForecast = mutableForecasts.toDailyForecasts(calendar, dailyFiltered),
            weatherCondition = weatherResponse.toWeatherCondition(),
            wind = weatherResponse.wind
        )
    }

    private fun WeatherResponse.toWeatherCondition(): WeatherCondition {
        val isSunny = weather.first().main == WeatherDescription.CLEAR.condition
                || weather.first().main == WeatherDescription.CLOUDS.condition && clouds.all <= 80
        val isRaining = weather.first().main == WeatherDescription.RAIN.condition
        val isStorm = weather.first().main == WeatherDescription.THUNDERSTORM.condition

        val calendar = Calendar.getInstance()

        calendar.timeInMillis = dt * 1000
        val forecastHour = calendar.get(Calendar.HOUR_OF_DAY)

        calendar.timeInMillis = sys.sunrise * 1000
        val sunriseHour = calendar.get(Calendar.HOUR_OF_DAY)

        calendar.timeInMillis = sys.sunset * 1000
        val sunsetHour = calendar.get(Calendar.HOUR_OF_DAY)

        val timeOfDay = when {
            forecastHour >= 0 && forecastHour <= sunriseHour ||
                    forecastHour >= sunsetHour && forecastHour <= 0 -> TimeOfDay.NIGHT
            forecastHour in sunriseHour..<sunsetHour -> TimeOfDay.MORNING
            else -> TimeOfDay.AFTERNOON
        }

        return WeatherCondition(
            timeOfDay = timeOfDay,
            cloudiness = clouds.all,
            isSunny = isSunny,
            isRaining = isRaining,
            isStormy = isStorm,
            mmHour = rain?.mmHour
        )
    }

    private fun filterForecastForTodayAndUpcoming(
        forecastList: MutableList<Forecast>,
        plusDays: Long
    ): List<Forecast> {
        val currentDate = LocalDate.now().plusDays(plusDays)

        val now = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).time
        val endOfToday = now + 86_400_000

        val filtered =  forecastList.filter { item ->
            val forecastTime = item.dt * 1000 // Converte timestamp para milissegundos
            forecastTime in now..endOfToday
        }
        //Remove da lista principal a lista já filtrada
        forecastList.removeAll(filtered)
        return filtered
    }

    private fun List<Forecast>.toHourlyForecasts(calendar: Calendar): List<HourlyForecast> {
        val hourlyForecasts = mutableListOf<HourlyForecast>()

        this.forEach{ forecast ->
            calendar.time = forecast.dtTxt.asIso8601Date()
            val forecastHour = calendar.get(Calendar.HOUR_OF_DAY)
            val formattedHour = getFormattedHour(forecastHour)
            /* Como a api retorna os dados apenas à cada 3 horas, preciso simular as outras horas
             * do dia com a rotina abaixo
             */
            val hourToConvert = if (forecastHour == 0) 24 else forecastHour
            hourlyForecasts.add(
                forecast.toHourlyForeCast(getFormattedHour( hourToConvert-2))
            )
            hourlyForecasts.add(
                forecast.toHourlyForeCast(getFormattedHour(hourToConvert -1))
            )

            val hourlyForecast = forecast.toHourlyForeCast(formattedHour)
            hourlyForecasts.add(hourlyForecast)
        }
        return hourlyForecasts
    }

    private fun getFormattedHour(forecastHour: Int): String {
        val calendar = Calendar.getInstance().apply { time = Date() }
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return if (forecastHour == currentHour) {
            HOUR_NOW_DESCRIPTION
        } else {
            String
                .format(
                    Locale.getDefault(),
                    "%02d",
                        forecastHour
                    )
        }
    }

    private fun Forecast.toHourlyForeCast(hour: String) = HourlyForecast(
        hour = hour,
        icon = weather.first().icon,
        temp = main.temp.roundToInt().toString()
    )

    private fun MutableList<Forecast>.toDailyForecasts(
        calendar: Calendar,
        todayForecast: List<Forecast>
    ): List<DailyForecast> {
        val dailyForecasts = mutableListOf<DailyForecast>()

        //Cria apenas um forecast representando o dia de hoje, com base na lista já filtrada

        dailyForecasts.add(todayForecast.filterToSelectedDailyForecast(calendar))

        //Pega a lista de forecasts dos dias seguintes. Ex: Hoje é sexta,
        // então pega a de sábado, domingo, etc. até completar a semana
        val plusOneForecast = filterForecastForTodayAndUpcoming(this, 1)
        val plusTwoForecast = filterForecastForTodayAndUpcoming(this, 2)
        val plusThreeForecast = filterForecastForTodayAndUpcoming(this, 3)
        val plusFourForecast = filterForecastForTodayAndUpcoming(this, 4)

        dailyForecasts.add(plusOneForecast.filterToSelectedDailyForecast(calendar))
        dailyForecasts.add(plusTwoForecast.filterToSelectedDailyForecast(calendar))
        dailyForecasts.add(plusThreeForecast.filterToSelectedDailyForecast(calendar))
        dailyForecasts.add(plusFourForecast.filterToSelectedDailyForecast(calendar))

        return dailyForecasts
    }

    private fun List<Forecast>.filterToSelectedDailyForecast(calendar: Calendar): DailyForecast {
        val minByTemperature = this.minBy { it.main.temp }
        val maxTemperature = this.maxBy { it.main.temp }
        val averageTempMin = this.minBy { it.main.tempMin }
        val averageTempMax = this.maxBy { it.main.tempMax }
        calendar.timeInMillis = this.first().dt * 1000
        return DailyForecast(
            day = getFormattedWeekDay(calendar),
            icon = minByTemperature.weather.first().icon,
            currentTemp = first().main.temp.roundToInt(),
            tempMin = minByTemperature.main.tempMin.roundToInt(),
            tempMax = maxTemperature.main.tempMax.roundToInt(),
            overallTemp = calculateDailyTemperatureRange(
                averageTempMin.main.tempMin.roundToInt(),
                averageTempMax.main.tempMax.roundToInt()
            )
        )
    }

    private fun calculateDailyTemperatureRange(tempMin: Int, tempMax: Int): Pair<Int, Int> {
        val overallMinTemp = tempMin - 5
        val overallMaxTemp = tempMax + 5
        return overallMinTemp to overallMaxTemp
    }

    private fun getFormattedWeekDay(calendar: Calendar): String {
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)

        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar.get(Calendar.DAY_OF_WEEK)

        return if (weekDay == today) {
            WeekDays.HOJ.formattedDay
        } else {
            when (weekDay) {
                WeekDays.DOM.intDay -> WeekDays.DOM.formattedDay
                WeekDays.SEG.intDay -> WeekDays.SEG.formattedDay
                WeekDays.TER.intDay -> WeekDays.TER.formattedDay
                WeekDays.QUA.intDay -> WeekDays.QUA.formattedDay
                WeekDays.QUI.intDay -> WeekDays.QUI.formattedDay
                WeekDays.SEX.intDay -> WeekDays.SEX.formattedDay
                WeekDays.SAB.intDay -> WeekDays.SAB.formattedDay
                else -> WeekDays.HOJ.formattedDay
            }
        }
    }
}