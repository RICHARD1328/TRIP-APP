package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Hotel
import com.tripdirect.data.repository.HotelRepository
import com.tripdirect.data.repository.MockHotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class HotelSortOption {
    Recommended, PriceLowToHigh, PriceHighToLow, HighestRated, Nearest
}

data class HotelFilterState(
    val price: String,
    val rating: String,
    val facilities: List<String>,
    val propertyType: String
)

class HotelSearchViewModel(
    private val repository: HotelRepository = MockHotelRepository()
) : ViewModel() {

    private val _destination = MutableStateFlow("")
    val destination: StateFlow<String> = _destination.asStateFlow()

    private val _checkInDate = MutableStateFlow<LocalDate?>(null)
    val checkInDate: StateFlow<LocalDate?> = _checkInDate.asStateFlow()

    private val _checkOutDate = MutableStateFlow<LocalDate?>(null)
    val checkOutDate: StateFlow<LocalDate?> = _checkOutDate.asStateFlow()

    private val _adults = MutableStateFlow(2)
    val adults: StateFlow<Int> = _adults.asStateFlow()

    private val _children = MutableStateFlow(0)
    val children: StateFlow<Int> = _children.asStateFlow()

    private val _rooms = MutableStateFlow(1)
    val rooms: StateFlow<Int> = _rooms.asStateFlow()

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedSort = MutableStateFlow(HotelSortOption.Recommended)
    val selectedSort: StateFlow<HotelSortOption> = _selectedSort.asStateFlow()
    
    private val _filterState = MutableStateFlow(HotelFilterState("Any", "Any", emptyList(), "Any"))
    val filterState: StateFlow<HotelFilterState> = _filterState.asStateFlow()

    private val _selectedHotelsForCompare = MutableStateFlow<List<Hotel>>(emptyList())
    val selectedHotelsForCompare: StateFlow<List<Hotel>> = _selectedHotelsForCompare.asStateFlow()

    val filteredHotels: StateFlow<List<Hotel>> = combine(
        _hotels, _filterState, _selectedSort
    ) { hotelList, filters, sort ->
        var result = hotelList

        // Price Filter
        when (filters.price) {
            "Under ₹1,000" -> result = result.filter { it.pricePerNight < 1000 }
            "₹1,000 – ₹2,000" -> result = result.filter { it.pricePerNight in 1000..2000 }
            "₹2,000 – ₹4,000" -> result = result.filter { it.pricePerNight in 2001..4000 }
            "Above ₹4,000" -> result = result.filter { it.pricePerNight > 4000 }
        }

        // Rating Filter
        when (filters.rating) {
            "⭐ 3+" -> result = result.filter { it.rating >= 3.0 }
            "⭐ 4+" -> result = result.filter { it.rating >= 4.0 }
            "⭐ 4.5+" -> result = result.filter { it.rating >= 4.5 }
        }

        // Facilities Filter
        if (filters.facilities.isNotEmpty()) {
            result = result.filter { hotel ->
                filters.facilities.all { fac -> hotel.facilities.contains(fac) }
            }
        }

        // Sort
        when (sort) {
            HotelSortOption.Recommended -> result = result.sortedByDescending { it.rating } // mocked
            HotelSortOption.PriceLowToHigh -> result = result.sortedBy { it.pricePerNight }
            HotelSortOption.PriceHighToLow -> result = result.sortedByDescending { it.pricePerNight }
            HotelSortOption.HighestRated -> result = result.sortedByDescending { it.rating }
            HotelSortOption.Nearest -> result = result.sortedBy { it.distanceFromCenter }
        }

        result
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Actions
    fun setDestination(dest: String) {
        _destination.value = dest
    }

    fun setCheckInDate(date: LocalDate) {
        _checkInDate.value = date
        // Auto adjust checkout if invalid
        if (_checkOutDate.value != null && !_checkOutDate.value!!.isAfter(date)) {
            _checkOutDate.value = date.plusDays(1)
        }
    }

    fun setCheckOutDate(date: LocalDate) {
        if (_checkInDate.value != null && date.isAfter(_checkInDate.value)) {
            _checkOutDate.value = date
        }
    }

    fun setGuests(adults: Int, children: Int, rooms: Int) {
        if (adults >= 1) _adults.value = adults
        if (children >= 0) _children.value = children
        if (rooms >= 1) _rooms.value = rooms
    }

    fun searchHotels() {
        val dest = _destination.value
        val inDate = _checkInDate.value
        val outDate = _checkOutDate.value

        if (dest.isBlank()) {
            _errorMessage.value = "Please select a destination."
            return
        }
        if (inDate == null || outDate == null) {
            _errorMessage.value = "Please select your travel dates."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _hotels.value = repository.searchHotels(
                    destination = dest,
                    checkIn = inDate.toString(),
                    checkOut = outDate.toString(),
                    adults = _adults.value,
                    children = _children.value,
                    rooms = _rooms.value
                )
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load hotels. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyFilters(price: String, rating: String, facilities: List<String>, propertyType: String) {
        _filterState.value = HotelFilterState(price, rating, facilities, propertyType)
    }

    fun clearFilters() {
        _filterState.value = HotelFilterState("Any", "Any", emptyList(), "Any")
    }

    fun setSortOption(sort: HotelSortOption) {
        _selectedSort.value = sort
    }

    fun toggleFavorite(hotelId: String) {
        val currentHotels = _hotels.value.toMutableList()
        val index = currentHotels.indexOfFirst { it.id == hotelId }
        if (index != -1) {
            val hotel = currentHotels[index]
            currentHotels[index] = hotel.copy(isFavorite = !hotel.isFavorite)
            _hotels.value = currentHotels
            
            // Also update compare list if present
            val compareList = _selectedHotelsForCompare.value.toMutableList()
            val compareIndex = compareList.indexOfFirst { it.id == hotelId }
            if (compareIndex != -1) {
                compareList[compareIndex] = currentHotels[index]
                _selectedHotelsForCompare.value = compareList
            }
        }
    }

    fun toggleCompare(hotel: Hotel) {
        val current = _selectedHotelsForCompare.value.toMutableList()
        if (current.any { it.id == hotel.id }) {
            current.removeIf { it.id == hotel.id }
        } else {
            if (current.size < 3) {
                current.add(hotel)
            }
        }
        _selectedHotelsForCompare.value = current
    }

    fun clearCompare() {
        _selectedHotelsForCompare.value = emptyList()
    }

    fun calculateNumberOfNights(): Long {
        val inDate = _checkInDate.value
        val outDate = _checkOutDate.value
        if (inDate != null && outDate != null) {
            return ChronoUnit.DAYS.between(inDate, outDate)
        }
        return 1
    }

    // Single Hotel Details
    private val _selectedHotel = MutableStateFlow<Hotel?>(null)
    val selectedHotel: StateFlow<Hotel?> = _selectedHotel.asStateFlow()

    fun loadHotelDetails(id: String) {
        viewModelScope.launch {
            _selectedHotel.value = null
            try {
                _selectedHotel.value = repository.getHotelById(id)
            } catch (e: Exception) {
                // handle
            }
        }
    }
}
