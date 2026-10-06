package com.tripdirect.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

data class OpenMeteoCurrentWeatherDto(
    @SerializedName("temperature")
    val temperature: Double = 0.0,
    @SerializedName("windspeed")
    val windSpeed: Double = 0.0,
    @SerializedName("weathercode")
    val weatherCode: Int = 0,
    @SerializedName("time")
    val time: String = ""
)

data class OpenMeteoResponseDto(
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("current_weather")
    val currentWeather: OpenMeteoCurrentWeatherDto? = null
)

interface OpenMeteoApiService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): Response<OpenMeteoResponseDto>

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/"

        fun create(): OpenMeteoApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoApiService::class.java)
        }
    }
}
