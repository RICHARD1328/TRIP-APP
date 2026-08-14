package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Place
import com.tripdirect.data.repository.MockPlacesRepository
import com.tripdirect.data.repository.PlacesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption {
    Recommended, HighestRated, LowestEntryFee, Nearest, MostPopular
}

class ExplorePlacesViewModel(
    private val repository: PlacesRepository = MockPlacesRepository()
) : ViewModel() {

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedRating = MutableStateFlow("Any Rating")
    val selectedRating: StateFlow<String> = _selectedRating.asStateFlow()

    private val _selectedBudget = MutableStateFlow("Any Price")
    val selectedBudget: StateFlow<String> = _selectedBudget.asStateFlow()

    private val _selectedDistance = MutableStateFlow("Any Distance")
    val selectedDistance: StateFlow<String> = _selectedDistance.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.Recommended)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private data class FilterState(
        val category: String,
        val rating: String,
        val budget: String,
        val distance: String,
        val sort: SortOption
    )

    private val filterStateFlow = combine(
        _selectedCategory, _selectedRating, _selectedBudget, _selectedDistance, _selectedSort
    ) { category, rating, budget, distance, sort ->
        FilterState(category, rating, budget, distance, sort)
    }

    val filteredPlaces: StateFlow<List<Place>> = combine(
        _places, filterStateFlow
    ) { placesList, filters ->
        var result = placesList

        if (filters.category != "All") {
            result = result.filter { it.category == filters.category }
        }

        when (filters.rating) {
            "⭐ 4+" -> result = result.filter { it.rating >= 4.0 }
            "⭐ 4.5+" -> result = result.filter { it.rating >= 4.5 }
        }

        when (filters.budget) {
            "Free" -> result = result.filter { it.entryFee == 0 }
            "Under ₹100" -> result = result.filter { it.entryFee < 100 }
            "₹100 – ₹500" -> result = result.filter { it.entryFee in 100..500 }
            "Above ₹500" -> result = result.filter { it.entryFee > 500 }
        }

        when (filters.sort) {
            SortOption.Recommended -> result = result.sortedByDescending { it.isFeatured }
            SortOption.HighestRated -> result = result.sortedByDescending { it.rating }
            SortOption.LowestEntryFee -> result = result.sortedBy { it.entryFee }
            SortOption.MostPopular -> result = result.sortedByDescending { it.rating } // mocked
            SortOption.Nearest -> result // mocked
        }

        result
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadPlaces()
        setupSearchDebounce()
    }

    private fun loadPlaces() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _places.value = repository.getPlaces()
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load places."
            } finally {
                _isLoading.value = false
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(400)
            .distinctUntilChanged()
            .onEach { query ->
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    _places.value = repository.searchPlaces(query)
                } catch (e: Exception) {
                    _errorMessage.value = "Unable to load places."
                } finally {
                    _isLoading.value = false
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun applyFilters(rating: String, budget: String, distance: String) {
        _selectedRating.value = rating
        _selectedBudget.value = budget
        _selectedDistance.value = distance
    }

    fun clearFilters() {
        _selectedRating.value = "Any Rating"
        _selectedBudget.value = "Any Price"
        _selectedDistance.value = "Any Distance"
    }

    fun onSortSelect(sort: SortOption) {
        _selectedSort.value = sort
    }

    fun toggleFavorite(placeId: String) {
        val currentPlaces = _places.value.toMutableList()
        val index = currentPlaces.indexOfFirst { it.id == placeId }
        if (index != -1) {
            val place = currentPlaces[index]
            currentPlaces[index] = place.copy(isFavorite = !place.isFavorite)
            _places.value = currentPlaces
        }
    }

    // Single Place Details
    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    fun loadPlaceDetails(id: String) {
        viewModelScope.launch {
            _selectedPlace.value = null
            try {
                _selectedPlace.value = repository.getPlaceById(id)
            } catch (e: Exception) {
                // handle
            }
        }
    }
}
