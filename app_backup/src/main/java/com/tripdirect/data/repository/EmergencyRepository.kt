package com.tripdirect.data.repository

import com.tripdirect.data.model.EmergencyContact
import com.tripdirect.data.model.EmergencyService
import com.tripdirect.data.model.EmergencyServiceType
import com.tripdirect.data.model.SafetyStatus
import com.tripdirect.data.model.SafetyTip
import com.tripdirect.data.model.UserLocation
import kotlinx.coroutines.delay

interface EmergencyRepository {
    suspend fun getNearbyServices(latitude: Double, longitude: Double): List<EmergencyService>
    suspend fun getEmergencyContacts(): List<EmergencyContact>
    suspend fun addEmergencyContact(contact: EmergencyContact): List<EmergencyContact>
    suspend fun deleteEmergencyContact(contactId: String): List<EmergencyContact>
    suspend fun getSafetyTips(): List<SafetyTip>
    suspend fun getSafetyStatus(): SafetyStatus
    suspend fun getUserLocation(): UserLocation
}

class MockEmergencyRepository : EmergencyRepository {

    private val contacts = mutableListOf(
        EmergencyContact("c_1", "Ramesh Kumar (Father)", "+91 98765 43210", "Family"),
        EmergencyContact("c_2", "Priya S. (Friend)", "+91 98765 12345", "Friend")
    )

    private val services = listOf(
        EmergencyService(
            id = "es_1",
            name = "Government Hospital Ooty",
            type = EmergencyServiceType.HOSPITAL,
            phoneNumber = "+91 423 2444108",
            address = "Hospital Road, Ooty, Tamil Nadu 643001",
            latitude = 11.4115,
            longitude = 76.7021,
            distanceKm = 2.4,
            isOpen = true,
            available24x7 = true
        ),
        EmergencyService(
            id = "es_2",
            name = "Ooty Town Police Station",
            type = EmergencyServiceType.POLICE,
            phoneNumber = "+91 423 2442222",
            address = "Commercial Road, Ooty, Tamil Nadu 643001",
            latitude = 11.4080,
            longitude = 76.6980,
            distanceKm = 1.8,
            isOpen = true,
            available24x7 = true
        ),
        EmergencyService(
            id = "es_3",
            name = "Apollo Pharmacy Ooty",
            type = EmergencyServiceType.PHARMACY,
            phoneNumber = "+91 423 2441111",
            address = "Main Bazaar, Ooty, Tamil Nadu 643001",
            latitude = 11.4095,
            longitude = 76.6960,
            distanceKm = 0.9,
            isOpen = true,
            available24x7 = true
        ),
        EmergencyService(
            id = "es_4",
            name = "Ooty Fire & Rescue Station",
            type = EmergencyServiceType.FIRE,
            phoneNumber = "+91 423 2442101",
            address = "Etiennes Road, Ooty, Tamil Nadu 643001",
            latitude = 11.4140,
            longitude = 76.7010,
            distanceKm = 2.1,
            isOpen = true,
            available24x7 = true
        ),
        EmergencyService(
            id = "es_5",
            name = "Tourist Assistance Center Ooty",
            type = EmergencyServiceType.TOURIST_HELP,
            phoneNumber = "1800-425-4646",
            address = "TTDC Information Center, Ooty",
            latitude = 11.4050,
            longitude = 76.6940,
            distanceKm = 0.8,
            isOpen = true,
            available24x7 = false
        ),
        EmergencyService(
            id = "es_6",
            name = "24/7 Emergency Medical Service (108)",
            type = EmergencyServiceType.AMBULANCE,
            phoneNumber = "108",
            address = "State Ambulance Toll Free",
            latitude = 11.4102,
            longitude = 76.6950,
            distanceKm = 0.0,
            isOpen = true,
            available24x7 = true
        )
    )

    private val tips = listOf(
        SafetyTip(
            id = "st_1",
            title = "Keep Digital Copies of Documents",
            description = "Store digital copies of your passport, ID proof, and travel tickets in cloud storage or email so you can access them anytime.",
            category = "Before Travel"
        ),
        SafetyTip(
            id = "st_2",
            title = "Share Your Live Itinerary",
            description = "Always inform a family member or trusted friend about your daily travel itinerary and hotel locations.",
            category = "Before Travel"
        ),
        SafetyTip(
            id = "st_3",
            title = "Hotel Room Door Locks",
            description = "Verify room lock mechanisms immediately after check-in and locate the nearest hotel fire exit stairs.",
            category = "Accommodation"
        ),
        SafetyTip(
            id = "st_4",
            title = "Use Verified Transport",
            description = "Use registered taxis or buses rather than unmarked private vehicles, especially when traveling at night.",
            category = "Transport"
        ),
        SafetyTip(
            id = "st_5",
            title = "Mountain Trek Precautions",
            description = "Carry extra thermal layers, flashlight, and water when trekking Doddabetta or Coonoor trails. Weather changes fast.",
            category = "Outdoor"
        ),
        SafetyTip(
            id = "st_6",
            title = "Emergency Contact Setup",
            description = "Keep local police (100) and medical (108) emergency numbers saved on your phone speed dial.",
            category = "Emergency"
        )
    )

    override suspend fun getNearbyServices(latitude: Double, longitude: Double): List<EmergencyService> {
        delay(200)
        return services.sortedBy { it.distanceKm }
    }

    override suspend fun getEmergencyContacts(): List<EmergencyContact> {
        delay(150)
        return contacts.toList()
    }

    override suspend fun addEmergencyContact(contact: EmergencyContact): List<EmergencyContact> {
        delay(150)
        contacts.add(contact)
        return contacts.toList()
    }

    override suspend fun deleteEmergencyContact(contactId: String): List<EmergencyContact> {
        delay(150)
        contacts.removeAll { it.id == contactId }
        return contacts.toList()
    }

    override suspend fun getSafetyTips(): List<SafetyTip> {
        delay(150)
        return tips
    }

    override suspend fun getSafetyStatus(): SafetyStatus {
        delay(100)
        return SafetyStatus(
            locationAvailable = true,
            sosReady = true,
            savedContactsCount = contacts.size,
            itineraryActive = true,
            statusText = "Ready / Safe"
        )
    }

    override suspend fun getUserLocation(): UserLocation {
        delay(100)
        return UserLocation()
    }
}
