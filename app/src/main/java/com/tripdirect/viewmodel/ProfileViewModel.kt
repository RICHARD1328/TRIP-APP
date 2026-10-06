package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.BudgetPreference
import com.tripdirect.data.model.NotificationSettings
import com.tripdirect.data.model.SavedPlace
import com.tripdirect.data.model.TravelPreference
import com.tripdirect.data.model.UserProfile
import com.tripdirect.data.model.UserStats
import com.tripdirect.data.model.UserTripSummary
import com.tripdirect.data.repository.MockProfileRepository
import com.tripdirect.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = MockProfileRepository()
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _stats = MutableStateFlow(UserStats())
    val stats: StateFlow<UserStats> = _stats.asStateFlow()

    private val _savedPlaces = MutableStateFlow<List<SavedPlace>>(emptyList())
    val savedPlaces: StateFlow<List<SavedPlace>> = _savedPlaces.asStateFlow()

    private val _trips = MutableStateFlow<List<UserTripSummary>>(emptyList())
    val trips: StateFlow<List<UserTripSummary>> = _trips.asStateFlow()

    private val _travelPreferences = MutableStateFlow(TravelPreference())
    val travelPreferences: StateFlow<TravelPreference> = _travelPreferences.asStateFlow()

    private val _budgetPreferences = MutableStateFlow(BudgetPreference())
    val budgetPreferences: StateFlow<BudgetPreference> = _budgetPreferences.asStateFlow()

    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _profile.value = repository.getProfile()
                _stats.value = repository.getUserStats()
                _savedPlaces.value = repository.getSavedPlaces()
                _trips.value = repository.getUserTrips()
                _travelPreferences.value = repository.getTravelPreferences()
                _budgetPreferences.value = repository.getBudgetPreferences()
                _notificationSettings.value = repository.getNotificationSettings()
            } catch (e: Exception) {
                _statusMessage.value = "Failed to load profile data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String, homeLocation: String) {
        viewModelScope.launch {
            val updated = _profile.value.copy(
                name = name,
                email = email,
                phone = phone,
                homeLocation = homeLocation
            )
            val saved = repository.updateProfile(updated)
            _profile.value = saved
            _statusMessage.value = "✓ Profile updated successfully!"
        }
    }

    fun addSavedPlace(place: SavedPlace) {
        viewModelScope.launch {
            val updatedList = repository.addSavedPlace(place)
            _savedPlaces.value = updatedList
            _stats.value = _stats.value.copy(savedPlaces = updatedList.size)
            _statusMessage.value = "Place saved to Favorites!"
        }
    }

    fun removeSavedPlace(placeId: String) {
        viewModelScope.launch {
            val updatedList = repository.removeSavedPlace(placeId)
            _savedPlaces.value = updatedList
            _stats.value = _stats.value.copy(savedPlaces = updatedList.size)
            _statusMessage.value = "Place removed from Saved."
        }
    }

    fun addTrip(trip: UserTripSummary) {
        viewModelScope.launch {
            val updated = repository.addTrip(trip)
            _trips.value = updated
            _stats.value = _stats.value.copy(totalTrips = updated.size)
            _statusMessage.value = "Trip \"${trip.title}\" saved!"
        }
    }

    fun updateTrip(trip: UserTripSummary) {
        viewModelScope.launch {
            val updated = repository.updateTrip(trip)
            _trips.value = updated
            _statusMessage.value = "Trip updated."
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            val updated = repository.deleteTrip(tripId)
            _trips.value = updated
            _stats.value = _stats.value.copy(totalTrips = updated.size)
            _statusMessage.value = "Trip deleted."
        }
    }

    fun updateTravelPreferences(prefs: TravelPreference) {
        viewModelScope.launch {
            val saved = repository.updateTravelPreferences(prefs)
            _travelPreferences.value = saved
            _statusMessage.value = "✓ Travel preferences saved!"
        }
    }

    fun updateBudgetPreferences(prefs: BudgetPreference) {
        viewModelScope.launch {
            val saved = repository.updateBudgetPreferences(prefs)
            _budgetPreferences.value = saved
            _statusMessage.value = "✓ Budget preferences saved!"
        }
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            val saved = repository.updateNotificationSettings(settings)
            _notificationSettings.value = saved
        }
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        _statusMessage.value = "Language set to $lang"
    }

    fun logout() {
        viewModelScope.launch {
            _statusMessage.value = "Logged out successfully."
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
