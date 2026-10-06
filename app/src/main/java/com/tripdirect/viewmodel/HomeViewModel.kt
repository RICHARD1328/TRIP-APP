package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Attraction
import com.tripdirect.data.model.Destination
import com.tripdirect.data.model.Hotel
import com.tripdirect.data.model.Trip
import com.tripdirect.data.model.User
import com.tripdirect.data.repository.HomeRepository
import com.tripdirect.data.repository.MockHomeRepository
import com.tripdirect.data.repository.SearchResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository = MockHomeRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _searchResults = MutableStateFlow<SearchResults?>(null)
    val searchResults: StateFlow<SearchResults?> = _searchResults.asStateFlow()

    private val _popularDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val popularDestinations: StateFlow<List<Destination>> = _popularDestinations.asStateFlow()

    private val _popularAttractions = MutableStateFlow<List<Attraction>>(emptyList())
    val popularAttractions: StateFlow<List<Attraction>> = _popularAttractions.asStateFlow()

    private val _recommendedHotels = MutableStateFlow<List<Hotel>>(emptyList())
    val recommendedHotels: StateFlow<List<Hotel>> = _recommendedHotels.asStateFlow()

    private val _activeTrip = MutableStateFlow<Trip?>(null)
    val activeTrip: StateFlow<Trip?> = _activeTrip.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // In a real app, you might use async/await to load these in parallel
                homeRepository.getCurrentUser().onSuccess { _currentUser.value = it }
                homeRepository.getPopularDestinations().onSuccess { _popularDestinations.value = it }
                homeRepository.getPopularAttractions().onSuccess { _popularAttractions.value = it }
                homeRepository.getRecommendedHotels().onSuccess { _recommendedHotels.value = it }
                homeRepository.getActiveTrip().onSuccess { _activeTrip.value = it }
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load travel information."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _isSearchActive.value = query.isNotEmpty()
        
        if (query.isEmpty()) {
            _searchResults.value = null
        } else {
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            homeRepository.search(query).onSuccess { results ->
                _searchResults.value = results
            }.onFailure {
                // Optionally handle search failure silently or show a toast
                _searchResults.value = SearchResults(emptyList(), emptyList(), emptyList())
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _isSearchActive.value = false
        _searchResults.value = null
    }
}
