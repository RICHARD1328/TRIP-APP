package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.network.NominatimSearchResultItem
import com.tripdirect.data.repository.NominatimRepository
import com.tripdirect.data.repository.NominatimResult
import com.tripdirect.util.NetworkErrorState
import com.tripdirect.util.NetworkErrorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationSearchViewModel(
    private val nominatimRepository: NominatimRepository = NominatimRepository()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _nominatimResults = MutableStateFlow<List<NominatimSearchResultItem>>(emptyList())
    val nominatimResults: StateFlow<List<NominatimSearchResultItem>> = _nominatimResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _networkError = MutableStateFlow<NetworkErrorState?>(null)
    val networkError: StateFlow<NetworkErrorState?> = _networkError.asStateFlow()

    private val _selectedLocation = MutableStateFlow<NominatimSearchResultItem?>(null)
    val selectedLocation: StateFlow<NominatimSearchResultItem?> = _selectedLocation.asStateFlow()

    private val _hasSubmitted = MutableStateFlow(false)
    val hasSubmitted: StateFlow<Boolean> = _hasSubmitted.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // Do NOT execute search on typing change per Nominatim policy requirement
    }

    fun submitSearch() {
        if (_isLoading.value) return // Prevent duplicate simultaneous requests
        val query = _searchQuery.value.trim()
        if (query.isBlank()) return

        _hasSubmitted.value = true
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _networkError.value = null

            when (val result = nominatimRepository.searchPlace(query)) {
                is NominatimResult.Success -> {
                    _nominatimResults.value = result.items
                    _isOffline.value = false
                    _isLoading.value = false
                }
                is NominatimResult.Error -> {
                    _nominatimResults.value = emptyList()
                    _errorMessage.value = result.message
                    _isOffline.value = result.isOffline
                    _networkError.value = if (result.isOffline) {
                        NetworkErrorState.offline(result.message)
                    } else {
                        NetworkErrorState(
                            type = NetworkErrorType.SERVER_UNAVAILABLE,
                            message = result.message,
                            isOffline = false
                        )
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    fun retrySearch() {
        if (_isLoading.value) return // Guard against duplicate rapid clicks
        submitSearch()
    }

    fun selectLocation(location: NominatimSearchResultItem) {
        _selectedLocation.value = location
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _nominatimResults.value = emptyList()
        _errorMessage.value = null
        _networkError.value = null
        _isOffline.value = false
        _hasSubmitted.value = false
    }
}
