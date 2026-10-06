package com.tripdirect.domain

import com.tripdirect.data.model.BudgetInput
import com.tripdirect.data.model.LocationPoint
import com.tripdirect.data.model.LocationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainLogicUnitTest {

    @Test
    fun testBudgetCalculator_computesCorrectTotalAndRemaining() {
        val input = BudgetInput(
            destination = "Ooty",
            totalBudget = 25000,
            days = 4,
            adults = 2,
            children = 1,
            transportCost = 5000,
            accommodationPerNight = 2500,
            foodPerPersonPerDay = 500,
            attractionCost = 1500,
            activityCost = 1000,
            localTransportPerDay = 400,
            miscellaneousCost = 1000
        )

        val breakdown = BudgetCalculator.calculate(input)

        // Nights = 4 - 1 = 3 -> Accommodation = 2500 * 3 = 7500
        assertEquals(7500, breakdown.accommodation)
        // Food = 500 * 3 travelers * 4 days = 6000
        assertEquals(6000, breakdown.food)
        // Local transport = 400 * 4 = 1600
        assertEquals(1600, breakdown.localTransport)
        // Total = 5000 + 7500 + 6000 + 1500 + 1000 + 1600 + 1000 = 23600
        assertEquals(23600, breakdown.total)
        // Remaining = 25000 - 23600 = 1400
        assertEquals(1400, breakdown.remaining)
        assertTrue(breakdown.remaining >= 0)
    }

    @Test
    fun testRouteOptimizer_ordersLocationsNearestNeighbor() {
        val points = listOf(
            LocationPoint("1", "Origin Hotel", 13.0827, 80.2707, LocationType.HOTEL),
            LocationPoint("2", "Far Attraction", 13.5000, 80.8000, LocationType.ATTRACTION),
            LocationPoint("3", "Near Restaurant", 13.0900, 80.2800, LocationType.RESTAURANT)
        )

        val result = RouteOptimizer.optimizeWithDetails(points)

        // Starting point should remain first
        assertEquals("1", result.optimizedLocations.first().id)
        // Second should be Near Restaurant (id: 3)
        assertEquals("3", result.optimizedLocations[1].id)
        // Third should be Far Attraction (id: 2)
        assertEquals("2", result.optimizedLocations[2].id)
        assertTrue(result.distanceSavedKm >= 0.0)
    }
}
