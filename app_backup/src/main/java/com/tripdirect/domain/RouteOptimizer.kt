package com.tripdirect.domain

import com.tripdirect.data.model.LocationPoint
import com.tripdirect.data.model.RouteOptimizationResult
import kotlin.math.pow
import kotlin.math.sqrt

object RouteOptimizer {

    /**
     * Optimizes location order using nearest-neighbor heuristic.
     * Calculates distance and duration savings compared to the initial unoptimized order.
     */
    fun optimize(locations: List<LocationPoint>): List<LocationPoint> {
        return optimizeWithDetails(locations).optimizedLocations
    }

    fun optimizeWithDetails(locations: List<LocationPoint>): RouteOptimizationResult {
        if (locations.size <= 2) {
            return RouteOptimizationResult(
                optimizedLocations = locations,
                distanceSavedKm = 0.0,
                durationSavedMinutes = 0,
                originalDistanceKm = calculateTotalDistance(locations),
                originalDurationMinutes = 0
            )
        }

        val originalDistance = calculateTotalDistance(locations)
        val result = mutableListOf<LocationPoint>()
        val unvisited = locations.toMutableList()

        // Assume first location is always the starting point (e.g., Hotel/Start)
        var current = unvisited.first()
        result.add(current)
        unvisited.remove(current)

        while (unvisited.isNotEmpty()) {
            val nearest = unvisited.minByOrNull { calculateEuclideanDistance(current, it) }
            if (nearest != null) {
                result.add(nearest)
                unvisited.remove(nearest)
                current = nearest
            } else {
                break
            }
        }

        val optimizedDistance = calculateTotalDistance(result)
        val distanceSaved = (originalDistance - optimizedDistance).coerceAtLeast(0.0)
        // Assume average travel speed of 30 km/h -> ~2 mins per km saved
        val durationSaved = (distanceSaved * 2.0).toInt()

        return RouteOptimizationResult(
            optimizedLocations = result,
            distanceSavedKm = distanceSaved,
            durationSavedMinutes = durationSaved,
            originalDistanceKm = originalDistance,
            originalDurationMinutes = (originalDistance * 2.0).toInt()
        )
    }

    private fun calculateTotalDistance(points: List<LocationPoint>): Double {
        var dist = 0.0
        for (i in 0 until points.size - 1) {
            dist += calculateEuclideanDistance(points[i], points[i + 1])
        }
        return dist / 10.0 // Scale coordinates to km
    }

    private fun calculateEuclideanDistance(p1: LocationPoint, p2: LocationPoint): Double {
        return sqrt((p2.latitude - p1.latitude).pow(2) + (p2.longitude - p1.longitude).pow(2))
    }
}

