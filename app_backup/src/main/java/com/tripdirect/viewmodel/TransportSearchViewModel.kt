package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.PricingType
import com.tripdirect.data.model.TransportOption
import com.tripdirect.data.model.TransportType
import com.tripdirect.data.repository.MockTransportRepository
import com.tripdirect.data.repository.TransportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class TransportSortOption {
    Recommended, Cheapest, Fastest, HighestRated, EarliestDeparture
}

data class TransportFilterState(
    val type: TransportType?, // null means All
    val maxPrice: String,
    val maxDuration: String,
    val rating: String
)

class TransportSearchViewModel(
    private val repository: TransportRepository = MockTransportRepository()
) : ViewModel() {

    private val _fromLocation = MutableStateFlow("")
    val fromLocation: StateFlow<String> = _fromLocation.asStateFlow()

    private val _toLocation = MutableStateFlow("")
    val toLocation: StateFlow<String> = _toLocation.asStateFlow()

    private val _travelDate = MutableStateFlow<LocalDate?>(null)
    val travelDate: StateFlow<LocalDate?> = _travelDate.asStateFlow()

    private val _travelers = MutableStateFlow(2)
    val travelers: StateFlow<Int> = _travelers.asStateFlow()

    private val _children = MutableStateFlow(0)
    val children: StateFlow<Int> = _children.asStateFlow()

    private val _transportOptions = MutableStateFlow<List<TransportOption>>(emptyList())
    val transportOptions: StateFlow<List<TransportOption>> = _transportOptions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedSort = MutableStateFlow(TransportSortOption.Recommended)
    val selectedSort: StateFlow<TransportSortOption> = _selectedSort.asStateFlow()

    private val _filterState = MutableStateFlow(TransportFilterState(null, "Any", "Any", "Any"))
    val filterState: StateFlow<TransportFilterState> = _filterState.asStateFlow()

    private val _selectedTransportsForCompare = MutableStateFlow<List<TransportOption>>(emptyList())
    val selectedTransportsForCompare: StateFlow<List<TransportOption>> = _selectedTransportsForCompare.asStateFlow()

    val filteredOptions: StateFlow<List<TransportOption>> = combine(
        _transportOptions, _filterState, _selectedSort
    ) { options, filters, sort ->
        var result = options

        // Filter by Type
        if (filters.type != null) {
            result = result.filter { it.type == filters.type }
        }

        // Filter by Max Price
        when (filters.maxPrice) {
            "Under ₹500" -> result = result.filter { getEstimatedTotal(it) < 500 }
            "Under ₹1,000" -> result = result.filter { getEstimatedTotal(it) < 1000 }
            "Under ₹2,000" -> result = result.filter { getEstimatedTotal(it) < 2000 }
            "Under ₹5,000" -> result = result.filter { getEstimatedTotal(it) < 5000 }
        }

        // Filter by Duration
        when (filters.maxDuration) {
            "Under 3 hours" -> result = result.filter { it.durationMinutes < 180 }
            "Under 6 hours" -> result = result.filter { it.durationMinutes < 360 }
            "Under 10 hours" -> result = result.filter { it.durationMinutes < 600 }
        }

        // Filter by Rating
        when (filters.rating) {
            "⭐ 3+" -> result = result.filter { it.rating >= 3.0 }
            "⭐ 4+" -> result = result.filter { it.rating >= 4.0 }
            "⭐ 4.5+" -> result = result.filter { it.rating >= 4.5 }
        }

        // Sort
        when (sort) {
            TransportSortOption.Recommended -> result = result.sortedByDescending { it.isRecommended }
            TransportSortOption.Cheapest -> result = result.sortedBy { getEstimatedTotal(it) }
            TransportSortOption.Fastest -> result = result.sortedBy { it.durationMinutes }
            TransportSortOption.HighestRated -> result = result.sortedByDescending { it.rating }
            TransportSortOption.EarliestDeparture -> result // Keep original mocked order for now
        }

        result
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Actions
    fun setFromLocation(loc: String) { _fromLocation.value = loc }
    fun setToLocation(loc: String) { _toLocation.value = loc }
    
    fun swapLocations() {
        val temp = _fromLocation.value
        _fromLocation.value = _toLocation.value
        _toLocation.value = temp
    }

    fun setTravelDate(date: LocalDate) { _travelDate.value = date }
    
    fun setTravelers(adults: Int, children: Int) {
        if (adults >= 1) _travelers.value = adults
        if (children >= 0) _children.value = children
    }

    fun searchTransport() {
        val from = _fromLocation.value
        val to = _toLocation.value
        val date = _travelDate.value

        if (from.isBlank()) {
            _errorMessage.value = "Please select your starting location."
            return
        }
        if (to.isBlank()) {
            _errorMessage.value = "Please select your destination."
            return
        }
        if (from.trim().equals(to.trim(), ignoreCase = true)) {
            _errorMessage.value = "Starting point and destination must be different."
            return
        }
        if (date == null) {
            _errorMessage.value = "Please select your travel date."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _transportOptions.value = repository.searchTransport(
                    from = from,
                    to = to,
                    date = date.toString(),
                    travelers = _travelers.value
                )
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load transport options. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getEstimatedTotal(option: TransportOption): Int {
        return when (option.pricingType) {
            PricingType.PER_PERSON -> option.price * _travelers.value
            PricingType.PER_TRIP, PricingType.PER_DAY -> option.price // constant
        }
    }

    fun applyFilters(type: TransportType?, maxPrice: String, maxDuration: String, rating: String) {
        _filterState.value = TransportFilterState(type, maxPrice, maxDuration, rating)
    }

    fun clearFilters() {
        _filterState.value = TransportFilterState(null, "Any", "Any", "Any")
    }

    fun setSortOption(sort: TransportSortOption) { _selectedSort.value = sort }

    fun toggleCompare(option: TransportOption) {
        val current = _selectedTransportsForCompare.value.toMutableList()
        if (current.any { it.id == option.id }) {
            current.removeIf { it.id == option.id }
        } else {
            if (current.size < 3) {
                current.add(option)
            }
        }
        _selectedTransportsForCompare.value = current
    }

    // Single Details
    private val _selectedTransport = MutableStateFlow<TransportOption?>(null)
    val selectedTransport: StateFlow<TransportOption?> = _selectedTransport.asStateFlow()

    fun loadTransportDetails(id: String) {
        viewModelScope.launch {
            _selectedTransport.value = null
            try {
                _selectedTransport.value = repository.getTransportById(id)
            } catch (e: Exception) {
                // handle
            }
        }
    }
}
