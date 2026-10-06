package com.tripdirect.data.model

enum class EmergencyServiceType {
    AMBULANCE,
    POLICE,
    FIRE,
    HOSPITAL,
    PHARMACY,
    TOURIST_HELP
}

data class EmergencyService(
    val id: String,
    val name: String,
    val type: EmergencyServiceType,
    val phoneNumber: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val isOpen: Boolean = true,
    val available24x7: Boolean = true
)

data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String = "Family"
)

data class SafetyTip(
    val id: String,
    val title: String,
    val description: String,
    val category: String // "Before Travel", "Accommodation", "Transport", "Outdoor", "Money & Docs", "Emergency"
)

data class SafetyStatus(
    val locationAvailable: Boolean = true,
    val sosReady: Boolean = true,
    val savedContactsCount: Int = 2,
    val itineraryActive: Boolean = true,
    val statusText: String = "Ready / Safe"
)

data class UserLocation(
    val latitude: Double = 11.4102,
    val longitude: Double = 76.6950,
    val locationName: String = "Ooty, Tamil Nadu",
    val address: String = "City Center, Ooty, Tamil Nadu 643001"
)
