package br.com.mdr.weather.core.di

import br.com.mdr.weather.core.domain.repository.WeatherRepository
import br.com.mdr.weather.core.domain.usecase.WeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun providesWeatherUseCase(repository: WeatherRepository) =
        WeatherUseCase(repository)
}