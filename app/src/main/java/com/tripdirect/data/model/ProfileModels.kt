package com.tripdirect.data.model

data class UserProfile(
    val id: String = "user_101",
    val name: String = "Richard Ruban Kumar",
    val email: String = "richardrubankumar.cse2024@citchennai.net",
    val phone: String = "+91 98765 43210",
    val profileImage: String? = null,
    val homeLocation: String = "Chennai, Tamil Nadu",
    val country: String = "India",
    val dateOfBirth: String = "15 Aug 2002",
    val defaultTravelers: Int = 2,
    val preferredCurrency: String = "INR ₹",
    val preferredLanguage: String = "English",
    val distanceUnit: String = "Kilometers",
    val totalSavings: Int = 8500,
    val tourPackageSpent: Int = 42000,
    val selfPlanSpent: Int = 33500,
    val placesVisitedCount: Int = 12,
    val favoritesCount: Int = 18
) {
    val initials: String
        get() {
            val parts = name.trim().split(" ")
            return if (parts.size >= 2) {
                "${parts.first().take(1)}${parts.last().take(1)}".uppercase()
            } else {
                name.take(2).uppercase()
            }
        }
}

data class UserStats(
    val totalTrips: Int = 7,
    val upcomingTrips: Int = 2,
    val completedTrips: Int = 5,
    val placesVisited: Int = 12,
    val totalSaved: Int = 8500,
    val favorites: Int = 18,
    val savedPlaces: Int = 12,
    val itineraries: Int = 3,
    val reviews: Int = 8
)

enum class TravelStyle {
    BUDGET,
    COMFORT,
    LUXURY,
    BACKPACKING,
    FAMILY,
    ADVENTURE,
    SOLO,
    BALANCED
}

enum class TravelPace {
    RELAXED,
    BALANCED,
    FAST_PACED
}

enum class AccommodationType {
    HOTEL,
    HOSTEL,
    HOMESTAY,
    RESORT
}

data class TravelPreference(
    val selectedInterests: Set<String> = setOf("Nature", "Food", "Photography", "Culture"),
    val travelStyle: TravelStyle = TravelStyle.BALANCED,
    val pace: TravelPace = TravelPace.BALANCED,
    val accommodation: AccommodationType = AccommodationType.HOTEL,
    val styles: Set<TravelStyle> = setOf(TravelStyle.BUDGET, TravelStyle.FAMILY),
    val interests: Set<String> = setOf("Nature", "Food", "Mountains", "History"),
    val transportModes: Set<String> = setOf("Bus", "Train", "Taxi")
)

data class BudgetPreference(
    val minTripBudget: Double = 5000.0,
    val maxTripBudget: Double = 15000.0,
    val preferredDailyBudget: Double = 2500.0,
    val stayCategory: String = "Budget / Comfort (₹1,500 - ₹3,000)",
    val foodCategory: String = "Moderate Local Food (₹500 - ₹1,000/day)",
    val dailyBudget: Int = 2500,
    val hotelBudgetPerNight: Int = 1500,
    val foodBudgetPerDay: Int = 500,
    val preferredTransport: String = "Bus / Train / Cab"
)

data class SavedPlace(
    val id: String,
    val name: String,
    val location: String = "Ooty, Tamil Nadu",
    val rating: Float = 4.5f,
    val category: String = "Nature",
    val imageUrl: String? = null,
    val destination: String = "Ooty"
)

data class UserTripSummary(
    val id: String,
    val title: String,
    val dates: String,
    val durationDays: Int,
    val travelersCount: Int,
    val estimatedBudget: Double,
    val status: String // "UPCOMING", "COMPLETED", "DRAFT"
)

data class NotificationSettings(
    val tripReminders: Boolean = true,
    val hotelPriceAlerts: Boolean = true,
    val transportUpdates: Boolean = true,
    val weatherAlerts: Boolean = true,
    val aiRecommendations: Boolean = true,
    val safetyAlerts: Boolean = true,
    val budgetAlerts: Boolean = false,
    val itineraryUpdates: Boolean = true,
    val priceUpdates: Boolean = true,
    val hotelUpdates: Boolean = false,
    val travelRecommendations: Boolean = true,
    val reviewReminders: Boolean = false
)

data class LocationSettings(
    val useCurrentLocation: Boolean = true,
    val allowLocationAccess: Boolean = true,
    val defaultStartingLocation: String = "Chennai"
)

data class SecuritySettings(
    val emailVerified: Boolean = true,
    val phoneVerified: Boolean = true,
    val passwordProtected: Boolean = true,
    val biometricEnabled: Boolean = false,
    val rememberLogin: Boolean = true
)
