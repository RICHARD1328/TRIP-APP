package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Attraction
import com.tripdirect.data.model.Destination
import com.tripdirect.data.model.Hotel
import com.tripdirect.data.repository.MockSearchRepository
import com.tripdirect.data.repository.SearchRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class SearchCategory {
    All, Destinations, Attractions, Hotels
}

class SearchViewModel(
    private val repository: SearchRepository = MockSearchRepository()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(SearchCategory.All)
    val selectedCategory: StateFlow<SearchCategory> = _selectedCategory.asStateFlow()

    private val _destinations = MutableStateFlow<List<Destination>>(emptyList())
    val destinations: StateFlow<List<Destination>> = _destinations.asStateFlow()

    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions.asStateFlow()

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _popularSearches = MutableStateFlow(listOf("Ooty", "Munnar", "Goa", "Manali", "Kodaikanal", "Jaipur", "Coorg", "Wayanad"))
    val popularSearches: StateFlow<List<String>> = _popularSearches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        setupSearchDebounce()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(400) // 400ms debounce
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) {
                    performSearch(query)
                } else {
                    clearResults()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: SearchCategory) {
        _selectedCategory.value = category
    }

    fun onSearchAction(query: String) {
        if (query.isNotBlank()) {
            saveRecentSearch(query.trim())
            // Perform search is handled by debounce, but we could force it here if needed
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // In a real app we could parallelize these
                val dests = repository.searchDestinations(query)
                val attrs = repository.searchAttractions(query)
                val hots = repository.searchHotels(query)

                _destinations.value = dests
                _attractions.value = attrs
                _hotels.value = hots
            } catch (e: Exception) {
                _errorMessage.value = "Unable to search right now."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun clearResults() {
        _destinations.value = emptyList()
        _attractions.value = emptyList()
        _hotels.value = emptyList()
        _errorMessage.value = null
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    private fun saveRecentSearch(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query) // Remove if exists to put at top
        currentList.add(0, query) // Add to top
        if (currentList.size > 5) {
            currentList.removeLast() // Keep only 5
        }
        _recentSearches.value = currentList
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }
}
