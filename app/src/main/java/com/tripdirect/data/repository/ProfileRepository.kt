package com.tripdirect.data.repository

import com.tripdirect.data.local.database.TripDirectDatabase
import com.tripdirect.data.local.entity.SavedPlaceEntity
import com.tripdirect.data.local.entity.TripEntity
import com.tripdirect.data.model.BudgetPreference
import com.tripdirect.data.model.NotificationSettings
import com.tripdirect.data.model.SavedPlace
import com.tripdirect.data.model.TravelPreference
import com.tripdirect.data.model.UserProfile
import com.tripdirect.data.model.UserStats
import com.tripdirect.data.model.UserTripSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun updateProfile(profile: UserProfile): UserProfile
    suspend fun getUserStats(): UserStats
    suspend fun getSavedPlaces(): List<SavedPlace>
    suspend fun addSavedPlace(place: SavedPlace): List<SavedPlace>
    suspend fun removeSavedPlace(placeId: String): List<SavedPlace>
    suspend fun isPlaceSaved(placeId: String): Boolean
    suspend fun getUserTrips(): List<UserTripSummary>
    suspend fun addTrip(trip: UserTripSummary): List<UserTripSummary>
    suspend fun updateTrip(trip: UserTripSummary): List<UserTripSummary>
    suspend fun deleteTrip(tripId: String): List<UserTripSummary>
    suspend fun getTravelPreferences(): TravelPreference
    suspend fun updateTravelPreferences(prefs: TravelPreference): TravelPreference
    suspend fun getBudgetPreferences(): BudgetPreference
    suspend fun updateBudgetPreferences(prefs: BudgetPreference): BudgetPreference
    suspend fun getNotificationSettings(): NotificationSettings
    suspend fun updateNotificationSettings(settings: NotificationSettings): NotificationSettings
}

class MockProfileRepository(
    private val database: TripDirectDatabase? = null
) : ProfileRepository {

    private var currentProfile = UserProfile(
        id = "user_101",
        name = "Richard Ruban Kumar",
        email = "richardrubankumar.cse2024@citchennai.net",
        phone = "+91 98765 43210",
        profileImage = null,
        homeLocation = "Chennai, Tamil Nadu"
    )

    private val defaultSavedPlaces = listOf(
        SavedPlace("sp_1", "Ooty Botanical Garden", "Ooty, Tamil Nadu", 4.6f, "Attraction", null, "Ooty"),
        SavedPlace("sp_2", "Munnar Tea Gardens", "Munnar, Kerala", 4.7f, "Nature", null, "Munnar"),
        SavedPlace("sp_3", "Kodaikanal Lake", "Kodaikanal, Tamil Nadu", 4.5f, "Lake", null, "Kodaikanal"),
        SavedPlace("sp_4", "Doddabetta Peak", "Ooty, Tamil Nadu", 4.6f, "Viewpoint", null, "Ooty"),
        SavedPlace("sp_5", "Pondicherry Promenade Beach", "Pondicherry", 4.4f, "Beach", null, "Pondicherry")
    )

    private val defaultUserTrips = listOf(
        UserTripSummary("t_1", "Ooty Family Nature Trip", "12 Aug - 15 Aug 2026", 3, 2, 8500.0, "UPCOMING"),
        UserTripSummary("t_2", "Munnar Weekend Getaway", "24 Sep - 26 Sep 2026", 2, 2, 6200.0, "UPCOMING"),
        UserTripSummary("t_3", "Pondicherry Beach & Heritage", "10 Jun - 12 Jun 2026", 2, 1, 4800.0, "COMPLETED"),
        UserTripSummary("t_4", "Kodaikanal Monsoon Trail", "15 May - 18 May 2026", 3, 4, 11500.0, "COMPLETED")
    )

    private var travelPrefs = TravelPreference()
    private var budgetPrefs = BudgetPreference()
    private var notificationPrefs = NotificationSettings()

    override suspend fun getProfile(): UserProfile {
        return currentProfile
    }

    override suspend fun updateProfile(profile: UserProfile): UserProfile {
        currentProfile = profile
        return currentProfile
    }

    override suspend fun getUserStats(): UserStats = withContext(Dispatchers.IO) {
        val places = getSavedPlaces()
        val trips = getUserTrips()
        UserStats(
            totalTrips = trips.size,
            savedPlaces = places.size,
            itineraries = 3,
            reviews = 8
        )
    }

    override suspend fun getSavedPlaces(): List<SavedPlace> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                val list = db.savedPlaceDao().getSavedPlacesList()
                if (list.isNotEmpty()) {
                    return@withContext list.map { it.toDomainModel() }
                } else {
                    val entities = defaultSavedPlaces.map { SavedPlaceEntity.fromDomainModel(it) }
                    db.savedPlaceDao().insertSavedPlaces(entities)
                    return@withContext defaultSavedPlaces
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        defaultSavedPlaces
    }

    override suspend fun addSavedPlace(place: SavedPlace): List<SavedPlace> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.savedPlaceDao().insertSavedPlace(SavedPlaceEntity.fromDomainModel(place))
                return@withContext db.savedPlaceDao().getSavedPlacesList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        (defaultSavedPlaces + place).distinctBy { it.id }
    }

    override suspend fun removeSavedPlace(placeId: String): List<SavedPlace> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.savedPlaceDao().deleteSavedPlace(placeId)
                return@withContext db.savedPlaceDao().getSavedPlacesList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        defaultSavedPlaces.filter { it.id != placeId }
    }

    override suspend fun isPlaceSaved(placeId: String): Boolean = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                return@withContext db.savedPlaceDao().isSaved(placeId)
            } catch (e: Exception) {
                // Ignore
            }
        }
        defaultSavedPlaces.any { it.id == placeId }
    }

    override suspend fun getUserTrips(): List<UserTripSummary> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                val list = db.tripDao().getAllTripsList()
                if (list.isNotEmpty()) {
                    return@withContext list.map { it.toDomainModel() }
                } else {
                    val entities = defaultUserTrips.map { TripEntity.fromDomainModel(it) }
                    db.tripDao().insertTrips(entities)
                    return@withContext defaultUserTrips
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        defaultUserTrips
    }

    override suspend fun addTrip(trip: UserTripSummary): List<UserTripSummary> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.tripDao().insertTrip(TripEntity.fromDomainModel(trip))
                return@withContext db.tripDao().getAllTripsList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        (defaultUserTrips + trip).distinctBy { it.id }
    }

    override suspend fun updateTrip(trip: UserTripSummary): List<UserTripSummary> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.tripDao().updateTrip(TripEntity.fromDomainModel(trip))
                return@withContext db.tripDao().getAllTripsList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        val current = defaultUserTrips.toMutableList()
        val index = current.indexOfFirst { it.id == trip.id }
        if (index != -1) current[index] = trip
        current
    }

    override suspend fun deleteTrip(tripId: String): List<UserTripSummary> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.tripDao().deleteTrip(tripId)
                db.itineraryDao().deleteItinerary(tripId)
                return@withContext db.tripDao().getAllTripsList().map { it.toDomainModel() }
            } catch (e: Exception) {
                // Ignore
            }
        }
        defaultUserTrips.filter { it.id != tripId }
    }

    override suspend fun getTravelPreferences(): TravelPreference {
        return travelPrefs
    }

    override suspend fun updateTravelPreferences(prefs: TravelPreference): TravelPreference {
        travelPrefs = prefs
        return travelPrefs
    }

    override suspend fun getBudgetPreferences(): BudgetPreference {
        return budgetPrefs
    }

    override suspend fun updateBudgetPreferences(prefs: BudgetPreference): BudgetPreference {
        budgetPrefs = prefs
        return budgetPrefs
    }

    override suspend fun getNotificationSettings(): NotificationSettings {
        return notificationPrefs
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings): NotificationSettings {
        notificationPrefs = settings
        return notificationPrefs
    }
}
