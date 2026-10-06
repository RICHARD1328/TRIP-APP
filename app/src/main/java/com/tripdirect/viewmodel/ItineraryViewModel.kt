package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Itinerary
import com.tripdirect.data.model.ItineraryDay
import com.tripdirect.data.model.ItineraryItem
import com.tripdirect.data.model.ItineraryItemType
import com.tripdirect.data.model.ItineraryStatus
import com.tripdirect.data.repository.ItineraryRepository
import com.tripdirect.data.repository.MockItineraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ItineraryViewModel(
    private val repository: ItineraryRepository = MockItineraryRepository()
) : ViewModel() {

    private val _itinerary = MutableStateFlow<Itinerary?>(null)
    val itinerary: StateFlow<Itinerary?> = _itinerary.asStateFlow()

    private val _selectedDay = MutableStateFlow<ItineraryDay?>(null)
    val selectedDay: StateFlow<ItineraryDay?> = _selectedDay.asStateFlow()

    private val _selectedItem = MutableStateFlow<ItineraryItem?>(null)
    val selectedItem: StateFlow<ItineraryItem?> = _selectedItem.asStateFlow()

    private val _selectedFilterType = MutableStateFlow<ItineraryItemType?>(null)
    val selectedFilterType: StateFlow<ItineraryItemType?> = _selectedFilterType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        loadItinerary("mock_ooty")
    }

    fun loadItinerary(tripId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val data = repository.getItinerary(tripId)
                _itinerary.value = data
                _selectedDay.value = data.days.firstOrNull()
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load itinerary. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectDay(dayNumber: Int) {
        _itinerary.value?.let { trip ->
            _selectedDay.value = trip.days.find { it.dayNumber == dayNumber }
        }
    }

    fun selectItem(item: ItineraryItem?) {
        _selectedItem.value = item
    }

    fun filterByType(type: ItineraryItemType?) {
        _selectedFilterType.value = type
    }

    fun searchItinerary(query: String) {
        _searchQuery.value = query
    }

    fun markItemStatus(itemId: String, newStatus: ItineraryStatus) {
        val current = _itinerary.value ?: return
        viewModelScope.launch {
            val updated = repository.updateActivityStatus(current, itemId, newStatus)
            _itinerary.value = updated
            _selectedDay.value = updated.days.find { it.dayNumber == _selectedDay.value?.dayNumber }
            _statusMessage.value = when (newStatus) {
                ItineraryStatus.COMPLETED -> "Activity marked as completed ✓"
                ItineraryStatus.SKIPPED -> "Activity skipped. Route recalculated ⊘"
                ItineraryStatus.IN_PROGRESS -> "Activity in progress 🔵"
                else -> "Status updated"
            }
        }
    }

    fun skipActivity(itemId: String) {
        markItemStatus(itemId, ItineraryStatus.SKIPPED)
    }

    fun addCustomActivity(
        dayNumber: Int,
        title: String,
        location: String,
        type: ItineraryItemType,
        startTime: String,
        durationMinutes: Int,
        cost: Int,
        notes: String
    ) {
        val current = _itinerary.value ?: return
        val newItem = ItineraryItem(
            id = "custom_${UUID.randomUUID().toString().take(8)}",
            startTime = startTime,
            title = title,
            description = notes,
            location = location,
            type = type,
            durationMinutes = durationMinutes,
            estimatedCost = cost,
            notes = notes,
            status = ItineraryStatus.UPCOMING
        )
        viewModelScope.launch {
            val updated = repository.addActivityToDay(current, dayNumber, newItem)
            _itinerary.value = updated
            _selectedDay.value = updated.days.find { it.dayNumber == dayNumber }
            _statusMessage.value = "Added \"$title\" to Day $dayNumber schedule"
        }
    }

    fun removeActivity(itemId: String) {
        val current = _itinerary.value ?: return
        viewModelScope.launch {
            val updated = repository.removeActivity(current, itemId)
            _itinerary.value = updated
            _selectedDay.value = updated.days.find { it.dayNumber == _selectedDay.value?.dayNumber }
            _statusMessage.value = "Activity removed from itinerary"
        }
    }

    fun optimizeSelectedDay() {
        val currentDay = _selectedDay.value ?: return
        viewModelScope.launch {
            _isOptimizing.value = true
            try {
                val optimized = repository.optimizeDay(currentDay)
                val updatedTrip = _itinerary.value?.let { trip ->
                    val updatedDays = trip.days.map { if (it.dayNumber == optimized.dayNumber) optimized else it }
                    trip.copy(days = updatedDays, totalEstimatedCost = updatedDays.sumOf { it.estimatedCost })
                }
                _itinerary.value = updatedTrip
                _selectedDay.value = optimized
                _statusMessage.value = "✨ Day ${currentDay.dayNumber} itinerary optimized for minimum travel!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to optimize route."
            } finally {
                _isOptimizing.value = false
            }
        }
    }

    fun saveItinerary() {
        val current = _itinerary.value ?: return
        viewModelScope.launch {
            repository.saveItinerary(current)
            _isSaved.value = true
            _statusMessage.value = "Itinerary saved to My Trip"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun getShareableText(): String {
        val trip = _itinerary.value ?: return "TripDirect Itinerary"
        val sb = StringBuilder()
        sb.append("TRIPDIRECT — ${trip.destination.uppercase()} TRIP\n\n")
        sb.append("${trip.days.size} Days • ${trip.travelers} Travelers\n")
        sb.append("Estimated Cost: ₹${trip.totalEstimatedCost} (Budget: ₹${trip.budget})\n\n")

        trip.days.forEach { day ->
            sb.append("DAY ${day.dayNumber}: ${day.title}\n")
            day.activities.forEach { act ->
                sb.append(" • ${act.startTime} - ${act.title} (${act.location})\n")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

