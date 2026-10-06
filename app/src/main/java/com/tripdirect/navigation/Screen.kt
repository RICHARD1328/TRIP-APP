package com.tripdirect.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    // Main flow hosted inside MainScreen (which manages its own bottom bar routes)
    object Main : Screen("main")
    object Search : Screen("search")
    
    // Deeper routes
    object DestinationDetail : Screen("destination/{id}") {
        fun createRoute(id: String) = "destination/$id"
    }
    object AttractionDetail : Screen("attraction/{id}") {
        fun createRoute(id: String) = "attraction/$id"
    }
    object Hotels : Screen("hotels")
    object HotelDetail : Screen("hotel/{id}") {
        fun createRoute(id: String) = "hotel/$id"
    }
    object HotelCompare : Screen("hotelCompare")
    
    // Transport
    object Transport : Screen("transport")
    object TransportDetail : Screen("transport/{id}") {
        fun createRoute(id: String) = "transport/$id"
    }
    object TransportCompare : Screen("transportCompare")
    
    // Planner
    object Planner : Screen("planner")
    object PlanningProgress : Screen("planningProgress")
    object GeneratedTrip : Screen("generatedTrip")
    object TripDayDetail : Screen("tripDay/{day}") {
        fun createRoute(day: Int) = "tripDay/$day"
    }
    object EditTrip : Screen("editTrip")
    
    // Budget Calculator
    object Budget : Screen("budget")
    object BudgetResult : Screen("budgetResult")
    object BudgetBreakdown : Screen("budgetBreakdown")
    object BudgetCompare : Screen("budgetCompare")
    
    // Route Map
    object RouteMap : Screen("routeMap")
    object RouteDetails : Screen("routeDetails")
    object Navigation : Screen("navigation")
    
    // Core Features
    object Comparison : Screen("comparison")
    object PackageBreakdown : Screen("packageBreakdown")
    val AITripPlanner = Planner


    
    // Explore Places Routes
    object PlaceDetail : Screen("place/{id}") {
        fun createRoute(id: String) = "place/$id"
    }
    object Route : Screen("route/{id}") {
        fun createRoute(id: String) = "route/$id"
    }
    
    // Bottom nav destinations
    object Home : Screen("home_tab")
    object ItineraryMain : Screen("itineraryMain")
    object ItineraryDayDetails : Screen("itineraryDayDetails")
    object ItineraryEdit : Screen("itineraryEdit")
    object ItinerarySummary : Screen("itinerarySummary")

    // Reviews Routes
    object Reviews : Screen("reviews?entityId={entityId}") {
        fun createRoute(entityId: String? = null): String {
            return if (!entityId.isNullOrBlank()) "reviews?entityId=$entityId" else "reviews"
        }
    }
    object WriteReview : Screen("writeReview")
    object ReviewDetails : Screen("reviewDetails")
    object MyReviews : Screen("myReviews")

    // Emergency & Safety Routes
    object Emergency : Screen("emergency")
    object SOSActive : Screen("sosActive")
    object NearbyServices : Screen("nearbyServices")
    object EmergencyContacts : Screen("emergencyContacts")
    object SafetyTips : Screen("safetyTips")

    // Profile Routes
    object ProfileMain : Screen("profileMain")
    object EditProfile : Screen("editProfile")
    object TravelPreferences : Screen("travelPreferences")
    object BudgetPreferences : Screen("budgetPreferences")
    object SavedPlaces : Screen("savedPlaces")
    object MyTrips : Screen("myTrips")
    object NotificationSettings : Screen("notificationSettings")
    object Language : Screen("language")
    object Privacy : Screen("privacy")
    object HelpSupport : Screen("helpSupport")
    object About : Screen("about")
    object ServerSettings : Screen("serverSettings")
}



