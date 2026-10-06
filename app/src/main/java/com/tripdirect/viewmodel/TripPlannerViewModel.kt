package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.TripDay
import com.tripdirect.data.model.TripPlan
import com.tripdirect.data.model.TripPlanRequest
import com.tripdirect.data.repository.MockTripPlannerRepository
import com.tripdirect.data.repository.NetworkTripPlannerRepository
import com.tripdirect.data.repository.TripPlannerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class PlanningStep {
    ANALYZING_PREFERENCES,
    FINDING_PLACES,
    FINDING_HOTELS,
    FINDING_TRANSPORT,
    OPTIMIZING_ROUTE,
    CALCULATING_BUDGET,
    GENERATING_ITINERARY,
    COMPLETED
}

class TripPlannerViewModel(
    private val repository: TripPlannerRepository = NetworkTripPlannerRepository()
) : ViewModel() {

    // Inputs
    private val _fromLocation = MutableStateFlow("")
    val fromLocation: StateFlow<String> = _fromLocation.asStateFlow()

    private val _destination = MutableStateFlow("")
    val destination: StateFlow<String> = _destination.asStateFlow()

    private val _startDate = MutableStateFlow<LocalDate?>(null)
    val startDate: StateFlow<LocalDate?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<LocalDate?>(null)
    val endDate: StateFlow<LocalDate?> = _endDate.asStateFlow()

    private val _travelers = MutableStateFlow(2)
    val travelers: StateFlow<Int> = _travelers.asStateFlow()

    private val _budget = MutableStateFlow("₹10,000")
    val budget: StateFlow<String> = _budget.asStateFlow()

    private val _interests = MutableStateFlow<Set<String>>(emptySet())
    val interests: StateFlow<Set<String>> = _interests.asStateFlow()

    private val _travelStyle = MutableStateFlow("Balanced")
    val travelStyle: StateFlow<String> = _travelStyle.asStateFlow()

    private val _hotelPreference = MutableStateFlow("3-Star Hotel")
    val hotelPreference: StateFlow<String> = _hotelPreference.asStateFlow()

    private val _transportPreference = MutableStateFlow("Any")
    val transportPreference: StateFlow<String> = _transportPreference.asStateFlow()

    private val _pace = MutableStateFlow("Balanced")
    val pace: StateFlow<String> = _pace.asStateFlow()

    // State
    private val _planningStep = MutableStateFlow<PlanningStep?>(null)
    val planningStep: StateFlow<PlanningStep?> = _planningStep.asStateFlow()

    private val _tripPlan = MutableStateFlow<TripPlan?>(null)
    val tripPlan: StateFlow<TripPlan?> = _tripPlan.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _networkError = MutableStateFlow<com.tripdirect.util.NetworkErrorState?>(null)
    val networkError: StateFlow<com.tripdirect.util.NetworkErrorState?> = _networkError.asStateFlow()

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent: StateFlow<String?> = _navigationEvent.asStateFlow()

    fun consumeNavigationEvent() {
        _navigationEvent.value = null
    }

    fun clearError() {
        _errorMessage.value = null
        _networkError.value = null
    }

    fun retryGenerateTrip(onComplete: () -> Unit = {}) {
        if (_isGenerating.value) return // Prevent duplicate simultaneous retries
        generateTrip(onComplete)
    }

    // Actions
    fun setFromLocation(loc: String) { _fromLocation.value = loc }
    fun setDestination(dest: String) { _destination.value = dest }
    fun setDates(start: LocalDate?, end: LocalDate?) {
        _startDate.value = start
        _endDate.value = end
    }
    fun setTravelers(count: Int) { if (count > 0) _travelers.value = count }
    fun setBudget(b: String) { _budget.value = b }
    
    fun toggleInterest(interest: String) {
        val current = _interests.value.toMutableSet()
        if (current.contains(interest)) current.remove(interest) else current.add(interest)
        _interests.value = current
    }

    fun setTravelStyle(style: String) { _travelStyle.value = style }
    fun setHotelPreference(pref: String) { _hotelPreference.value = pref }
    fun setTransportPreference(pref: String) { _transportPreference.value = pref }
    fun setPace(p: String) { _pace.value = p }

    fun generateTrip(onComplete: () -> Unit = {}) {
        if (_isGenerating.value) return // Prevent duplicate requests

        val start = _startDate.value
        val end = _endDate.value

        if (_fromLocation.value.isBlank()) {
            _errorMessage.value = "Please select your starting location."
            return
        }
        if (_destination.value.isBlank()) {
            _errorMessage.value = "Please select a destination."
            return
        }
        if (start == null || end == null || start.isAfter(end)) {
            _errorMessage.value = "Please select valid travel dates."
            return
        }
        if (_interests.value.isEmpty()) {
            _errorMessage.value = "Please select at least one interest."
            return
        }
        if (_budget.value.isBlank()) {
            _errorMessage.value = "Please enter a valid budget."
            return
        }

        val numericBudget = _budget.value.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 10000

        val request = TripPlanRequest(
            from = _fromLocation.value,
            destination = _destination.value,
            startDate = start.toString(),
            endDate = end.toString(),
            travelers = _travelers.value,
            budget = numericBudget,
            interests = _interests.value.toList(),
            travelStyle = _travelStyle.value,
            hotelPreference = _hotelPreference.value,
            transportPreference = _transportPreference.value,
            pace = _pace.value
        )

        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            _networkError.value = null
            _tripPlan.value = null
            _navigationEvent.value = null

            // Simulate Steps
            val steps = PlanningStep.values()
            for (step in steps) {
                _planningStep.value = step
                delay(400) // UI animation delay
            }

            try {
                val plan = repository.generateTripPlan(request)
                _tripPlan.value = plan
                _navigationEvent.value = plan.id
                onComplete()
            } catch (e: Exception) {
                val netErr = com.tripdirect.util.NetworkErrorState.fromThrowable(e)
                _networkError.value = netErr
                _errorMessage.value = if (netErr.isOffline) {
                    "Unable to create your trip plan.\nCheck your internet connection and try again."
                } else {
                    netErr.message
                }
                _navigationEvent.value = null
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun optimizeTrip() {
        val plan = _tripPlan.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                _tripPlan.value = repository.optimizeTrip(plan)
            } catch (e: Exception) {
                _errorMessage.value = "Unable to optimize your trip right now."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun modifyTrip(instruction: String) {
        val plan = _tripPlan.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            _navigationEvent.value = null
            try {
                val modified = repository.modifyTrip(plan, instruction)
                _tripPlan.value = modified
                _navigationEvent.value = modified.id
            } catch (e: Exception) {
                _errorMessage.value = "Unable to modify your trip right now. Please try again."
                _navigationEvent.value = null
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun getDayDetails(dayNumber: Int): TripDay? {
        return _tripPlan.value?.days?.find { it.dayNumber == dayNumber }
    }
}
