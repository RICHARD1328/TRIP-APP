package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Destination
import com.tripdirect.data.model.EstimatedTripCost
import com.tripdirect.data.repository.DestinationRepository
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
    data class Error(val message: String) : DestinationDetailUiState()
}

class DestinationDetailViewModel(
    private val repository: DestinationRepository = DestinationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DestinationDetailUiState>(DestinationDetailUiState.Loading)
    val uiState: StateFlow<DestinationDetailUiState> = _uiState.asStateFlow()

    fun loadDestination(id: String) {
        viewModelScope.launch {
            _uiState.value = DestinationDetailUiState.Loading
            try {
                val dest = repository.getDestinationById(id)
                if (dest == null) {
                    _uiState.value = DestinationDetailUiState.Error("Destination details unavailable")
                    return@launch
                }

                val isSaved = repository.savedIds.value.contains(dest.id)
                val initialCost = dest.costBreakdown

                // Initial success with fallback image
                _uiState.value = DestinationDetailUiState.Success(
                    destination = dest,
                    heroImageUrl = dest.fallbackImageUrl,
                    photoAttribution = null,
                    isSaved = isSaved,
                    editableCost = initialCost
                )

                // Try fetching live Google Places Photo
                try {
                    val (googlePhotoUrl, attribution) = repository.getGooglePlacePhotoUrl(dest.googleSearchQuery)
                    if (!googlePhotoUrl.isNullOrBlank()) {
                        val current = _uiState.value
                        if (current is DestinationDetailUiState.Success) {
                            _uiState.value = current.copy(
                                heroImageUrl = googlePhotoUrl,
                                photoAttribution = attribution
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Photo fetch failure is non-fatal; fallback image remains displayed
                }
            } catch (e: Exception) {
                _uiState.value = DestinationDetailUiState.Error("Unable to load destination. Please try again.")
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
