package com.tripdirect.data.repository

import com.tripdirect.data.local.database.TripDirectDatabase
import com.tripdirect.data.local.entity.EmergencyContactEntity
import com.tripdirect.data.model.EmergencyContact
import com.tripdirect.data.model.EmergencyService
import com.tripdirect.data.model.EmergencyServiceType
import com.tripdirect.data.model.SafetyStatus
import com.tripdirect.data.model.SafetyTip
import com.tripdirect.data.model.UserLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface EmergencyRepository {
    suspend fun getNearbyServices(latitude: Double, longitude: Double): List<EmergencyService>
    suspend fun getEmergencyContacts(): List<EmergencyContact>
    suspend fun addEmergencyContact(contact: EmergencyContact): List<EmergencyContact>
    suspend fun updateEmergencyContact(contact: EmergencyContact): List<EmergencyContact>
    suspend fun deleteEmergencyContact(contactId: String): List<EmergencyContact>
    suspend fun getSafetyTips(): List<SafetyTip>
    suspend fun getSafetyStatus(): SafetyStatus
    suspend fun getUserLocation(): UserLocation
}

class MockEmergencyRepository(
    private val database: TripDirectDatabase? = null
) : EmergencyRepository {

    private val defaultContacts = listOf(
        EmergencyContact("c_1", "Ramesh Kumar (Father)", "+91 98765 43210", "Family"),
        EmergencyContact("c_2", "Priya S. (Friend)", "+91 98765 12345", "Friend")
    )

    private val services = listOf(
        EmergencyService("es_1", "Government Hospital Ooty", EmergencyServiceType.HOSPITAL, "+91 423 2444108", "Hospital Road, Ooty, Tamil Nadu 643001", 11.4115, 76.7021, 2.4, true, true),
        EmergencyService("es_2", "Ooty Town Police Station", EmergencyServiceType.POLICE, "+91 423 2442222", "Commercial Road, Ooty, Tamil Nadu 643001", 11.4080, 76.6980, 1.8, true, true),
        EmergencyService("es_3", "Apollo Pharmacy Ooty", EmergencyServiceType.PHARMACY, "+91 423 2441111", "Main Bazaar, Ooty, Tamil Nadu 643001", 11.4095, 76.6960, 0.9, true, true),
        EmergencyService("es_4", "Ooty Fire & Rescue Station", EmergencyServiceType.FIRE, "+91 423 2442101", "Etiennes Road, Ooty, Tamil Nadu 643001", 11.4140, 76.7010, 2.1, true, true),
        EmergencyService("es_5", "Tourist Assistance Center Ooty", EmergencyServiceType.TOURIST_HELP, "1800-425-4646", "TTDC Information Center, Ooty", 11.4050, 76.6940, 0.8, true, false),
        EmergencyService("es_6", "24/7 Emergency Medical Service (108)", EmergencyServiceType.AMBULANCE, "108", "State Ambulance Toll Free", 11.4102, 76.6950, 0.0, true, true)
    )

    private val tips = listOf(
        SafetyTip("st_1", "Keep Digital Copies of Documents", "Store digital copies of your passport, ID proof, and travel tickets in cloud storage or email.", "Before Travel"),
        SafetyTip("st_2", "Share Your Live Itinerary", "Always inform a family member or trusted friend about your daily travel itinerary.", "Before Travel"),
        SafetyTip("st_3", "Hotel Room Door Locks", "Verify room lock mechanisms immediately after check-in.", "Accommodation"),
        SafetyTip("st_4", "Use Verified Transport", "Use registered taxis or buses rather than unmarked private vehicles.", "Transport"),
        SafetyTip("st_5", "Mountain Trek Precautions", "Carry extra thermal layers, flashlight, and water when trekking.", "Outdoor"),
        SafetyTip("st_6", "Emergency Contact Setup", "Keep local police (100) and medical (108) emergency numbers saved.", "Emergency")
    )

    override suspend fun getNearbyServices(latitude: Double, longitude: Double): List<EmergencyService> {
        return services.sortedBy { it.distanceKm }
    }

    override suspend fun getEmergencyContacts(): List<EmergencyContact> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                val list = db.emergencyContactDao().getEmergencyContactsList()
                if (list.isNotEmpty()) {
                    return@withContext list.map { it.toDomainModel() }
                } else {
                    // Seed initial contacts
                    val entities = defaultContacts.map { EmergencyContactEntity.fromDomainModel(it) }
                    db.emergencyContactDao().insertEmergencyContacts(entities)
                    return@withContext defaultContacts
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        defaultContacts
    }

    override suspend fun addEmergencyContact(contact: EmergencyContact): List<EmergencyContact> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.emergencyContactDao().insertEmergencyContact(EmergencyContactEntity.fromDomainModel(contact))
                return@withContext db.emergencyContactDao().getEmergencyContactsList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        (getEmergencyContacts() + contact)
    }

    override suspend fun updateEmergencyContact(contact: EmergencyContact): List<EmergencyContact> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.emergencyContactDao().updateEmergencyContact(EmergencyContactEntity.fromDomainModel(contact))
                return@withContext db.emergencyContactDao().getEmergencyContactsList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        val current = getEmergencyContacts().toMutableList()
        val index = current.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            current[index] = contact
        } else {
            current.add(contact)
        }
        current
    }

    override suspend fun deleteEmergencyContact(contactId: String): List<EmergencyContact> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.emergencyContactDao().deleteEmergencyContact(contactId)
                return@withContext db.emergencyContactDao().getEmergencyContactsList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        getEmergencyContacts().filter { it.id != contactId }
    }

    override suspend fun getSafetyTips(): List<SafetyTip> {
        return tips
    }

    override suspend fun getSafetyStatus(): SafetyStatus {
        val contacts = getEmergencyContacts()
        return SafetyStatus(
            locationAvailable = true,
            sosReady = true,
            savedContactsCount = contacts.size,
            itineraryActive = true,
            statusText = "Ready / Safe"
        )
    }

    override suspend fun getUserLocation(): UserLocation {
        return UserLocation()
    }
}
