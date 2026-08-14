package com.tripdirect.data.repository

import com.tripdirect.data.model.LocationPoint
import com.tripdirect.data.model.LocationType
import com.tripdirect.data.model.RouteSegment
import com.tripdirect.data.model.TransportMode
import com.tripdirect.data.model.TripRoute
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt

interface RouteRepository {
    suspend fun getInitialLocations(): List<LocationPoint>
    suspend fun calculateRoute(locations: List<LocationPoint>, transportMode: TransportMode): TripRoute
    suspend fun searchPlacesNearRoute(query: String): List<LocationPoint>
}

class MockRouteRepository : RouteRepository {

    private val allMockPlaces = listOf(
        LocationPoint("start1", "Chennai Central", 10.0, 10.0, LocationType.START, "Chennai, TN", 0, "24 Hours", "24 Hours"),
        LocationPoint("h1", "Ooty Heritage Hotel", 50.0, 50.0, LocationType.HOTEL, "Ooty City Center", 0, "24 Hours", "24 Hours"),
        LocationPoint("a1", "Botanical Garden", 40.0, 70.0, LocationType.ATTRACTION, "Vannarapettai, Ooty", 90, "08:00 AM", "06:30 PM"),
        LocationPoint("a2", "Ooty Lake", 20.0, 40.0, LocationType.ATTRACTION, "West Ooty", 120, "09:00 AM", "06:00 PM"),
        LocationPoint("a3", "Tea Museum & Factory", 80.0, 90.0, LocationType.ATTRACTION, "Doddabetta Road", 60, "09:00 AM", "05:30 PM"),
        LocationPoint("a4", "Doddabetta Peak", 90.0, 20.0, LocationType.ATTRACTION, "Ooty-Kotagiri Road", 90, "07:00 AM", "06:00 PM"),
        LocationPoint("a5", "Coonoor Sim's Park", 95.0, 85.0, LocationType.ATTRACTION, "Coonoor, Nilgiris", 75, "08:30 AM", "06:00 PM"),
        LocationPoint("r1", "Nilgiri Spice Restaurant", 45.0, 55.0, LocationType.RESTAURANT, "Commercial Road, Ooty", 45, "11:00 AM", "10:30 PM"),
        LocationPoint("t1", "Ooty Railway Station", 30.0, 35.0, LocationType.TRANSPORT, "Station Road, Ooty", 15, "05:00 AM", "09:00 PM")
    )

    override suspend fun getInitialLocations(): List<LocationPoint> {
        delay(200)
        // Default itinerary route for Ooty
        return listOf(
            allMockPlaces[1], // Ooty Heritage Hotel
            allMockPlaces[2], // Botanical Garden
            allMockPlaces[3], // Ooty Lake
            allMockPlaces[4], // Tea Museum
            allMockPlaces[5], // Doddabetta Peak
            allMockPlaces[6]  // Coonoor Sim's Park
        )
    }

    override suspend fun calculateRoute(locations: List<LocationPoint>, transportMode: TransportMode): TripRoute {
        delay(200)
        val segments = mutableListOf<RouteSegment>()
        var totalDist = 0.0
        var totalDur = 0
        var totalCost = 0

        for (i in 0 until locations.size - 1) {
            val from = locations[i]
            val to = locations[i + 1]
            
            // Calculate distance scaled to km
            val rawDist = sqrt((to.latitude - from.latitude).pow(2) + (to.longitude - from.longitude).pow(2))
            val distanceKm = (rawDist / 10.0).coerceAtLeast(0.8)

            // Speed and cost metrics per transport mode
            val (speedKmH, costPerKm) = when (transportMode) {
                TransportMode.WALKING -> Pair(4.5, 0)
                TransportMode.BICYCLE -> Pair(14.0, 0)
                TransportMode.BUS -> Pair(28.0, 3)
                TransportMode.CAR -> Pair(38.0, 12)
                TransportMode.TAXI -> Pair(42.0, 22)
            }

            val durationMinutes = ((distanceKm / speedKmH) * 60).toInt().coerceAtLeast(3)
            val estimatedCost = (distanceKm * costPerKm).toInt()

            segments.add(RouteSegment(from, to, distanceKm, durationMinutes, transportMode, estimatedCost))
            
            totalDist += distanceKm
            totalDur += durationMinutes
            totalCost += estimatedCost
        }

        return TripRoute(locations, segments, totalDist, totalDur, totalCost)
    }

    override suspend fun searchPlacesNearRoute(query: String): List<LocationPoint> {
        delay(150)
        if (query.isBlank()) return emptyList()
        return allMockPlaces.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.address.contains(query, ignoreCase = true) ||
            it.type.name.contains(query, ignoreCase = true)
        }
    }
}

