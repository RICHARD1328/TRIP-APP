package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Destination
import com.tripdirect.data.model.EstimatedTripCost
import com.tripdirect.data.repository.DestinationRepository
import com.tripdirect.data.repository.WeatherRepository
import com.tripdirect.data.repository.WeatherResult
import com.tripdirect.util.NetworkErrorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DestinationDetailUiState {
    object Loading : DestinationDetailUiState()
    data class Success(
        val destination: Destination,
        val heroImageUrl: String,
        val photoAttribution: String?,
        val isSaved: Boolean,
        val editableCost: EstimatedTripCost
    ) : DestinationDetailUiState()
    data class Error(val message: String, val isOffline: Boolean = false) : DestinationDetailUiState()
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val weatherText: String, val condition: String, val isLive: Boolean) : WeatherUiState()
    data class Error(val message: String, val isOffline: Boolean) : WeatherUiState()
}

class DestinationDetailViewModel(
    private val repository: DestinationRepository = DestinationRepository(),
    private val weatherRepository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DestinationDetailUiState>(DestinationDetailUiState.Loading)
    val uiState: StateFlow<DestinationDetailUiState> = _uiState.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _isRetryingWeather = MutableStateFlow(false)
    val isRetryingWeather: StateFlow<Boolean> = _isRetryingWeather.asStateFlow()

    private var currentDestinationId: String? = null

    fun loadDestination(id: String) {
        currentDestinationId = id
        viewModelScope.launch {
            _uiState.value = DestinationDetailUiState.Loading
            try {
                val dest = repository.getDestinationById(id)
                if (dest == null) {
                    _uiState.value = DestinationDetailUiState.Error("Destination not found")
                    return@launch
                }

                val socialList = repository.getSocialContentForDestination(dest.id, dest.name)
                val destWithSocial = dest.copy(socialContent = socialList)

                val isSaved = repository.savedIds.value.contains(dest.id)
                val initialCost = dest.costBreakdown

                _uiState.value = DestinationDetailUiState.Success(
                    destination = destWithSocial,
                    heroImageUrl = dest.image,
                    photoAttribution = null,
                    isSaved = isSaved,
                    editableCost = initialCost
                )

                // Fetch live Open-Meteo weather
                loadWeatherForDestination(destWithSocial)

            } catch (e: Exception) {
                val errorState = NetworkErrorState.fromThrowable(e)
                _uiState.value = DestinationDetailUiState.Error(
                    message = errorState.message,
                    isOffline = errorState.isOffline
                )
            }
        }
    }

    private fun loadWeatherForDestination(dest: Destination) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            val result = weatherRepository.fetchCurrentWeather(dest.latitude, dest.longitude)
            when (result) {
                is WeatherResult.Success -> {
                    _weatherState.value = WeatherUiState.Success(
                        weatherText = "${result.weather.temperatureFormatted} • ${result.weather.condition}",
                        condition = result.weather.condition,
                        isLive = true
                    )
                }
                is WeatherResult.Error -> {
                    val msg = if (result.errorState.isOffline) {
                        "Weather information is unavailable offline."
                    } else {
                        "Weather information unavailable. Check connection."
                    }
                    _weatherState.value = WeatherUiState.Error(
                        message = msg,
                        isOffline = result.errorState.isOffline
                    )
                }
            }
        }
    }

    fun retryWeather() {
        if (_isRetryingWeather.value) return // Prevent duplicate retries
        val currentState = _uiState.value
        if (currentState is DestinationDetailUiState.Success) {
            viewModelScope.launch {
                _isRetryingWeather.value = true
                try {
                    val dest = currentState.destination
                    val result = weatherRepository.fetchCurrentWeather(dest.latitude, dest.longitude)
                    when (result) {
                        is WeatherResult.Success -> {
                            _weatherState.value = WeatherUiState.Success(
                                weatherText = "${result.weather.temperatureFormatted} • ${result.weather.condition}",
                                condition = result.weather.condition,
                                isLive = true
                            )
                        }
                        is WeatherResult.Error -> {
                            val msg = if (result.errorState.isOffline) {
                                "Weather information is unavailable offline."
                            } else {
                                "Weather information unavailable. Check connection."
                            }
                            _weatherState.value = WeatherUiState.Error(
                                message = msg,
                                isOffline = result.errorState.isOffline
                            )
                        }
                    }
                } finally {
                    _isRetryingWeather.value = false
                }
            }
        }
    }

    fun toggleSave(destinationId: String) {
        repository.toggleSave(destinationId)
        val currentState = _uiState.value
        if (currentState is DestinationDetailUiState.Success) {
            val isNowSaved = repository.savedIds.value.contains(destinationId)
            _uiState.value = currentState.copy(isSaved = isNowSaved)
        }
    }

    fun updateCost(
        transport: Int? = null,
        hotel: Int? = null,
        food: Int? = null,
        localTransport: Int? = null,
        activities: Int? = null
    ) {
        val currentState = _uiState.value
        if (currentState is DestinationDetailUiState.Success) {
            val old = currentState.editableCost
            val newCost = EstimatedTripCost(
                transport = transport ?: old.transport,
                hotel = hotel ?: old.hotel,
                food = food ?: old.food,
                localTransport = localTransport ?: old.localTransport,
                activities = activities ?: old.activities
            )
            _uiState.value = currentState.copy(editableCost = newCost)
        }
    }
}
