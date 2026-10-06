package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.EmergencyContact
import com.tripdirect.data.model.EmergencyService
import com.tripdirect.data.model.EmergencyServiceType
import com.tripdirect.data.model.SafetyStatus
import com.tripdirect.data.model.SafetyTip
import com.tripdirect.data.model.UserLocation
import com.tripdirect.data.repository.EmergencyRepository
import com.tripdirect.data.repository.MockEmergencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class EmergencyViewModel(
    private val repository: EmergencyRepository = MockEmergencyRepository()
) : ViewModel() {

    private val _location = MutableStateFlow<UserLocation?>(null)
    val location: StateFlow<UserLocation?> = _location.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(true)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _nearbyServices = MutableStateFlow<List<EmergencyService>>(emptyList())
    val nearbyServices: StateFlow<List<EmergencyService>> = _nearbyServices.asStateFlow()

    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    private val _safetyTips = MutableStateFlow<List<SafetyTip>>(emptyList())
    val safetyTips: StateFlow<List<SafetyTip>> = _safetyTips.asStateFlow()

    private val _safetyStatus = MutableStateFlow<SafetyStatus>(SafetyStatus())
    val safetyStatus: StateFlow<SafetyStatus> = _safetyStatus.asStateFlow()

    private val _selectedFilterType = MutableStateFlow<EmergencyServiceType?>(null)
    val selectedFilterType: StateFlow<EmergencyServiceType?> = _selectedFilterType.asStateFlow()

    private val _isSosActive = MutableStateFlow(false)
    val isSosActive: StateFlow<Boolean> = _isSosActive.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loc = repository.getUserLocation()
                _location.value = loc
                _nearbyServices.value = repository.getNearbyServices(loc.latitude, loc.longitude)
                _emergencyContacts.value = repository.getEmergencyContacts()
                _safetyTips.value = repository.getSafetyTips()
                _safetyStatus.value = repository.getSafetyStatus()
            } catch (e: Exception) {
                _statusMessage.value = "Failed to load safety data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLocationPermission(granted: Boolean) {
        _hasLocationPermission.value = granted
        _safetyStatus.value = _safetyStatus.value.copy(locationAvailable = granted)
        if (granted) {
            loadData()
        }
    }

    fun filterServices(type: EmergencyServiceType?) {
        _selectedFilterType.value = type
    }

    fun addEmergencyContact(name: String, phone: String, relationship: String) {
        viewModelScope.launch {
            val newContact = EmergencyContact(
                id = "c_${UUID.randomUUID().toString().take(8)}",
                name = name,
                phoneNumber = phone,
                relationship = relationship
            )
            val updated = repository.addEmergencyContact(newContact)
            _emergencyContacts.value = updated
            _safetyStatus.value = _safetyStatus.value.copy(savedContactsCount = updated.size)
            _statusMessage.value = "Emergency contact \"$name\" saved."
        }
    }

    fun updateEmergencyContact(id: String, name: String, phone: String, relationship: String) {
        viewModelScope.launch {
            val contact = EmergencyContact(
                id = id,
                name = name,
                phoneNumber = phone,
                relationship = relationship
            )
            val updated = repository.updateEmergencyContact(contact)
            _emergencyContacts.value = updated
            _statusMessage.value = "Emergency contact updated."
        }
    }

    fun deleteEmergencyContact(contactId: String) {
        viewModelScope.launch {
            val updated = repository.deleteEmergencyContact(contactId)
            _emergencyContacts.value = updated
            _safetyStatus.value = _safetyStatus.value.copy(savedContactsCount = updated.size)
            _statusMessage.value = "Contact removed."
        }
    }

    fun activateSos() {
        _isSosActive.value = true
        _statusMessage.value = "🚨 Emergency SOS Activated!"
    }

    fun cancelSos() {
        _isSosActive.value = false
        _statusMessage.value = "Emergency SOS cancelled."
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun getShareableLocationText(): String {
        val loc = _location.value ?: return "My current location: Ooty, Tamil Nadu (TripDirect Safety)"
        return "EMERGENCY / LOCATION SHARE — TRIPDIRECT\n\nI am currently at: ${loc.locationName}\nAddress: ${loc.address}\nGPS Coordinates: ${loc.latitude}, ${loc.longitude}\nGoogle Maps: https://maps.google.com/?q=${loc.latitude},${loc.longitude}\n\nSent via TripDirect Travel Safety Center."
    }
}
