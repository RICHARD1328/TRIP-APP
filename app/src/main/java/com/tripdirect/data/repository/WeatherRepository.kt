package com.tripdirect.data.repository

import com.tripdirect.data.network.OpenMeteoApiService
import com.tripdirect.util.NetworkErrorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

data class LiveWeatherInfo(
    val temperatureCelsius: Double,
    val temperatureFormatted: String,
    val condition: String,
    val windSpeedKmh: Double,
    val isLive: Boolean = true
)

sealed class WeatherResult {
    data class Success(val weather: LiveWeatherInfo) : WeatherResult()
    data class Error(val errorState: NetworkErrorState) : WeatherResult()
}

class WeatherRepository(
    private val apiService: OpenMeteoApiService = OpenMeteoApiService.create()
) {

    suspend fun fetchCurrentWeather(latitude: Double, longitude: Double): WeatherResult = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentWeather(latitude, longitude, currentWeather = true)
            if (response.isSuccessful) {
                val body = response.body()
                val current = body?.currentWeather
                if (current != null) {
                    val condition = mapWeatherCodeToCondition(current.weatherCode)
                    val temp = current.temperature
                    WeatherResult.Success(
                        LiveWeatherInfo(
                            temperatureCelsius = temp,
                            temperatureFormatted = "${temp.toInt()}°C",
                            condition = condition,
                            windSpeedKmh = current.windSpeed,
                            isLive = true
                        )
                    )
                } else {
                    WeatherResult.Error(
                        NetworkErrorState.fromThrowable(
                            IOException("Weather data unavailable in response")
                        )
                    )
                }
            } else {
                WeatherResult.Error(
                    NetworkErrorState.serverError(
                        "Weather server returned error ${response.code()}",
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            WeatherResult.Error(NetworkErrorState.fromThrowable(e))
        }
    }

    private fun mapWeatherCodeToCondition(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            71, 73, 75 -> "Snowy"
            80, 81, 82 -> "Rain Showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Pleasant"
        }
    }
}
