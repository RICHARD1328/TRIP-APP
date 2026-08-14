package com.tripdirect.data.repository

import com.tripdirect.data.model.BudgetPreference
import com.tripdirect.data.model.NotificationSettings
import com.tripdirect.data.model.SavedPlace
import com.tripdirect.data.model.TravelPreference
import com.tripdirect.data.model.UserProfile
import com.tripdirect.data.model.UserStats
import com.tripdirect.data.model.UserTripSummary
import kotlinx.coroutines.delay

interface ProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun updateProfile(profile: UserProfile): UserProfile
    suspend fun getUserStats(): UserStats
    suspend fun getSavedPlaces(): List<SavedPlace>
    suspend fun removeSavedPlace(placeId: String): List<SavedPlace>
    suspend fun getUserTrips(): List<UserTripSummary>
    suspend fun getTravelPreferences(): TravelPreference
    suspend fun updateTravelPreferences(prefs: TravelPreference): TravelPreference
    suspend fun getBudgetPreferences(): BudgetPreference
    suspend fun updateBudgetPreferences(prefs: BudgetPreference): BudgetPreference
    suspend fun getNotificationSettings(): NotificationSettings
    suspend fun updateNotificationSettings(settings: NotificationSettings): NotificationSettings
}

class MockProfileRepository : ProfileRepository {

    private var currentProfile = UserProfile(
        id = "user_101",
        name = "Richard Ruban Kumar",
        email = "richard@example.com",
        phone = "+91 98765 43210",
        profileImage = null,
        homeLocation = "Chennai, Tamil Nadu"
    )

    private val savedPlaces = mutableListOf(
        SavedPlace("sp_1", "Ooty Botanical Garden", "Ooty, Tamil Nadu", 4.6f, "Attraction"),
        SavedPlace("sp_2", "Munnar Tea Gardens", "Munnar, Kerala", 4.7f, "Nature"),
        SavedPlace("sp_3", "Kodaikanal Lake", "Kodaikanal, Tamil Nadu", 4.5f, "Lake"),
        SavedPlace("sp_4", "Doddabetta Peak", "Ooty, Tamil Nadu", 4.6f, "Viewpoint"),
        SavedPlace("sp_5", "Pondicherry Promenade Beach", "Pondicherry", 4.4f, "Beach")
    )

    private val userTrips = listOf(
        UserTripSummary("t_1", "Ooty Family Nature Trip", "12 Aug - 15 Aug 2026", 3, 2, 8500.0, "UPCOMING"),
        UserTripSummary("t_2", "Munnar Weekend Getaway", "24 Sep - 26 Sep 2026", 2, 2, 6200.0, "UPCOMING"),
        UserTripSummary("t_3", "Pondicherry Beach & Heritage", "10 Jun - 12 Jun 2026", 2, 1, 4800.0, "COMPLETED"),
        UserTripSummary("t_4", "Kodaikanal Monsoon Trail", "15 May - 18 May 2026", 3, 4, 11500.0, "COMPLETED")
    )

    private var travelPrefs = TravelPreference()
    private var budgetPrefs = BudgetPreference()
    private var notificationPrefs = NotificationSettings()

    override suspend fun getProfile(): UserProfile {
        delay(150)
        return currentProfile
    }

    override suspend fun updateProfile(profile: UserProfile): UserProfile {
        delay(200)
        currentProfile = profile
        return currentProfile
    }

    override suspend fun getUserStats(): UserStats {
        delay(100)
        return UserStats(
            totalTrips = userTrips.size,
            savedPlaces = savedPlaces.size,
            itineraries = 3,
            reviews = 8
        )
    }

    override suspend fun getSavedPlaces(): List<SavedPlace> {
        delay(150)
        return savedPlaces.toList()
    }

    override suspend fun removeSavedPlace(placeId: String): List<SavedPlace> {
        delay(150)
        savedPlaces.removeAll { it.id == placeId }
        return savedPlaces.toList()
    }

    override suspend fun getUserTrips(): List<UserTripSummary> {
        delay(150)
        return userTrips
    }

    override suspend fun getTravelPreferences(): TravelPreference {
        delay(100)
        return travelPrefs
    }

    override suspend fun updateTravelPreferences(prefs: TravelPreference): TravelPreference {
        delay(150)
        travelPrefs = prefs
        return travelPrefs
    }

    override suspend fun getBudgetPreferences(): BudgetPreference {
        delay(100)
        return budgetPrefs
    }

    override suspend fun updateBudgetPreferences(prefs: BudgetPreference): BudgetPreference {
        delay(150)
        budgetPrefs = prefs
        return budgetPrefs
    }

    override suspend fun getNotificationSettings(): NotificationSettings {
        delay(100)
        return notificationPrefs
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings): NotificationSettings {
        delay(150)
        notificationPrefs = settings
        return notificationPrefs
    }
}
