package br.com.mdr.weather.presentation

import br.com.mdr.weather.core.domain.model.Weather
import br.com.mdr.weather.core.domain.usecase.WeatherUseCase
import br.com.mdr.weather.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val useCase: WeatherUseCase
): BaseViewModel() {
    private val _weatherState = MutableStateFlow<Weather?>(null)
    var weatherState: StateFlow<Weather?> = _weatherState
    init {
        fetchWeatherData(-25.4773632, -49.3272485)
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        launch {
            _weatherState.value = useCase.fetchWeather(lat, lon)
        }
    }
}