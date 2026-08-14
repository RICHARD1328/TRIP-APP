package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.LocationPoint
import com.tripdirect.data.model.LocationType
import com.tripdirect.data.model.RouteOptimizationResult
import com.tripdirect.data.model.TransportMode
import com.tripdirect.data.model.TripRoute
import com.tripdirect.data.repository.MockRouteRepository
import com.tripdirect.data.repository.RouteRepository
import com.tripdirect.domain.RouteOptimizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteMapViewModel(
    private val repository: RouteRepository = MockRouteRepository()
) : ViewModel() {

    private val _allLocations = MutableStateFlow<List<LocationPoint>>(emptyList())
    
    private val _locations = MutableStateFlow<List<LocationPoint>>(emptyList())
    val locations: StateFlow<List<LocationPoint>> = _locations.asStateFlow()

    private val _currentRoute = MutableStateFlow<TripRoute?>(null)
    val currentRoute: StateFlow<TripRoute?> = _currentRoute.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationPoint?>(null)
    val selectedLocation: StateFlow<LocationPoint?> = _selectedLocation.asStateFlow()

    private val _selectedTransportMode = MutableStateFlow(TransportMode.CAR)
    val selectedTransportMode: StateFlow<TransportMode> = _selectedTransportMode.asStateFlow()

    private val _selectedFilterType = MutableStateFlow<LocationType?>(null)
    val selectedFilterType: StateFlow<LocationType?> = _selectedFilterType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LocationPoint>>(emptyList())
    val searchResults: StateFlow<List<LocationPoint>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing.asStateFlow()

    private val _optimizationResult = MutableStateFlow<RouteOptimizationResult?>(null)
    val optimizationResult: StateFlow<RouteOptimizationResult?> = _optimizationResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _userLocation = MutableStateFlow<LocationPoint?>(null)
    val userLocation: StateFlow<LocationPoint?> = _userLocation.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        loadInitialRoute()
    }

    fun loadInitialRoute() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val locs = repository.getInitialLocations()
                _allLocations.value = locs
                _locations.value = locs
                calculateRoute(locs, _selectedTransportMode.value)
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load route."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setTransportMode(mode: TransportMode) {
        _selectedTransportMode.value = mode
        if (_locations.value.isNotEmpty()) {
            calculateRoute(_locations.value, mode)
        }
    }

    fun selectLocation(location: LocationPoint?) {
        _selectedLocation.value = location
    }

    fun filterByLocationType(type: LocationType?) {
        _selectedFilterType.value = type
        if (type == null) {
            _locations.value = _allLocations.value
        } else {
            _locations.value = _allLocations.value.filter { it.type == type || it.type == LocationType.START }
        }
        if (_locations.value.isNotEmpty()) {
            calculateRoute(_locations.value, _selectedTransportMode.value)
        }
    }

    fun searchNearRoute(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                _searchResults.value = repository.searchPlacesNearRoute(query)
            }
        }
    }

    fun optimizeRoute() {
        val currentLocs = _locations.value
        if (currentLocs.size > 2) {
            _isOptimizing.value = true
            viewModelScope.launch {
                val result = RouteOptimizer.optimizeWithDetails(currentLocs)
                _allLocations.value = result.optimizedLocations
                _locations.value = result.optimizedLocations
                _optimizationResult.value = result
                calculateRoute(result.optimizedLocations, _selectedTransportMode.value)
                _isOptimizing.value = false
            }
        }
    }

    fun dismissOptimizationBanner() {
        _optimizationResult.value = null
    }

    fun enableLocationPermission() {
        _hasLocationPermission.value = true
        _userLocation.value = LocationPoint(
            id = "user_loc",
            name = "My Location",
            latitude = 48.0,
            longitude = 48.0,
            type = LocationType.START,
            address = "Current Location (GPS Active)"
        )
        _statusMessage.value = "GPS Location Updated"
    }

    fun addTransportCostToBudget(budgetViewModel: BudgetCalculatorViewModel) {
        val route = _currentRoute.value ?: return
        budgetViewModel.addExternalTransportExpense(route.totalEstimatedCost)
        _statusMessage.value = "Added ₹${route.totalEstimatedCost} transport cost to Trip Budget"
    }

    fun saveRoute() {
        _isSaved.value = true
        _statusMessage.value = "Route saved to My Trip"
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun getShareableText(): String {
        val route = _currentRoute.value ?: return "TripDirect Route"
        val sb = StringBuilder()
        sb.append("TripDirect Route — Ooty\n\n")
        route.locations.forEachIndexed { index, loc ->
            sb.append("${index + 1}. ${loc.name} (${loc.type})\n")
        }
        sb.append("\nTotal Distance: ${String.format("%.1f", route.totalDistanceKm)} km\n")
        sb.append("Estimated Time: ${route.totalDurationMinutes} min\n")
        sb.append("Transport Cost (${_selectedTransportMode.value}): ₹${route.totalEstimatedCost}\n")
        return sb.toString()
    }

    private fun calculateRoute(locs: List<LocationPoint>, mode: TransportMode) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val route = repository.calculateRoute(locs, mode)
                _currentRoute.value = route
            } catch (e: Exception) {
                _errorMessage.value = "Failed to calculate route metrics."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

