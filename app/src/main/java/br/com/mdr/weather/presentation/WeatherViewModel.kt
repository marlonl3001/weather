package br.com.mdr.weather.presentation

import br.com.mdr.weather.core.domain.location.LocationTracker
import br.com.mdr.weather.core.domain.model.Weather
import br.com.mdr.weather.core.domain.usecase.WeatherUseCase
import br.com.mdr.weather.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val useCase: WeatherUseCase,
    private val locationTracker: LocationTracker
): BaseViewModel() {
    private val _weatherState = MutableStateFlow<Weather?>(null)
    var weatherState: StateFlow<Weather?> = _weatherState

    fun fetchWeatherData() {
        launch(dispatcher = Dispatchers.Main) {
            locationTracker.getCurrentLocation()?.let { location ->
                _weatherState.value = useCase.fetchWeather(location.latitude, location.longitude)
            }
        }
    }
}