package com.tripdirect.data.model

enum class LocationType {
    START,
    HOTEL,
    ATTRACTION,
    RESTAURANT,
    TRANSPORT,
    END,
    OTHER
}

enum class TransportMode {
    WALKING,
    BUS,
    CAR,
    TAXI,
    BICYCLE
}

data class LocationPoint(
    val id: String,
    val name: String,
    val latitude: Double, // X coordinate for mock / map
    val longitude: Double, // Y coordinate for mock / map
    val type: LocationType,
    val address: String = "",
    val estimatedVisitMinutes: Int = 0,
    val openingHours: String = "08:00 AM",
    val closingHours: String = "06:00 PM"
)

data class RouteSegment(
    val from: LocationPoint,
    val to: LocationPoint,
    val distanceKm: Double,
    val durationMinutes: Int,
    val transportMode: TransportMode,
    val estimatedCost: Int
)

data class TripRoute(
    val locations: List<LocationPoint>,
    val segments: List<RouteSegment>,
    val totalDistanceKm: Double,
    val totalDurationMinutes: Int,
    val totalEstimatedCost: Int
)

data class RouteOptimizationResult(
    val optimizedLocations: List<LocationPoint>,
    val distanceSavedKm: Double,
    val durationSavedMinutes: Int,
    val originalDistanceKm: Double,
    val originalDurationMinutes: Int
)

data class TimelineItem(
    val time: String,
    val title: String,
    val subtitle: String,
    val type: LocationType
)

