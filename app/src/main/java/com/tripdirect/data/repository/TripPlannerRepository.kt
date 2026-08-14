package com.tripdirect.data.repository

import com.tripdirect.data.model.*
import kotlinx.coroutines.delay
import java.util.UUID

interface TripPlannerRepository {
    suspend fun generateTripPlan(request: TripPlanRequest): TripPlan
    suspend fun optimizeTrip(tripPlan: TripPlan): TripPlan
    suspend fun modifyTrip(tripPlan: TripPlan, instruction: String): TripPlan
}

class MockTripPlannerRepository : TripPlannerRepository {

    override suspend fun generateTripPlan(request: TripPlanRequest): TripPlan {
        // Simulate network/AI delay. In reality, the ViewModel might simulate the steps,
        // but we'll add a short delay here for realism.
        delay(1000)

        val transportCost = 2000 * request.travelers
        val accommodationCost = 1500 * 2 // assuming 2 nights
        val foodCost = 1000 * request.travelers * 3 // 3 days
        val activitiesCost = 500 * request.travelers
        val localTransportCost = 500
        val miscellaneousCost = 500
        val totalCost = transportCost + accommodationCost + foodCost + activitiesCost + localTransportCost + miscellaneousCost

        val budgetSummary = TripBudget(
            transport = transportCost,
            accommodation = accommodationCost,
            food = foodCost,
            activities = activitiesCost,
            localTransport = localTransportCost,
            miscellaneous = miscellaneousCost,
            total = totalCost,
            userBudget = request.budget
        )

        val itemsDay1 = listOf(
            ItineraryItem(id = "1", title = "Travel to ${request.destination}", type = ItineraryItemType.TRANSPORT, startTime = "08:00 AM", endTime = "01:30 PM", location = request.from, description = "Bus travel", estimatedCost = 850 * request.travelers, travelTimeMinutes = 330, durationMinutes = 330),
            ItineraryItem(id = "2", title = "Hotel Check-in", type = ItineraryItemType.HOTEL, startTime = "01:30 PM", endTime = "02:30 PM", location = request.destination, description = "Mountain View Hotel", estimatedCost = accommodationCost / 2, travelTimeMinutes = 15, durationMinutes = 60),
            ItineraryItem(id = "3", title = "${request.destination} Lake", type = ItineraryItemType.ATTRACTION, startTime = "03:00 PM", endTime = "05:00 PM", location = request.destination, description = "Relaxing boat ride", estimatedCost = 200 * request.travelers, travelTimeMinutes = 30, durationMinutes = 120),
            ItineraryItem(id = "4", title = "Botanical Garden", type = ItineraryItemType.ATTRACTION, startTime = "05:00 PM", endTime = "07:00 PM", location = request.destination, description = "Stroll through nature", estimatedCost = 50 * request.travelers, travelTimeMinutes = 15, durationMinutes = 120),
            ItineraryItem(id = "5", title = "Dinner", type = ItineraryItemType.RESTAURANT, startTime = "07:30 PM", endTime = "09:00 PM", location = request.destination, description = "Local cuisine", estimatedCost = 500 * request.travelers, travelTimeMinutes = 15, durationMinutes = 90)
        )

        val itemsDay2 = listOf(
            ItineraryItem(id = "6", title = "Breakfast", type = ItineraryItemType.RESTAURANT, startTime = "08:30 AM", endTime = "09:30 AM", location = request.destination, description = "Hotel breakfast", estimatedCost = 0, travelTimeMinutes = 0, durationMinutes = 60),
            ItineraryItem(id = "7", title = "Doddabetta Peak", type = ItineraryItemType.ATTRACTION, startTime = "10:00 AM", endTime = "01:00 PM", location = request.destination, description = "Highest peak viewpoint", estimatedCost = 100 * request.travelers, travelTimeMinutes = 45, durationMinutes = 180),
            ItineraryItem(id = "8", title = "Lunch", type = ItineraryItemType.RESTAURANT, startTime = "01:30 PM", endTime = "02:30 PM", location = request.destination, description = "South Indian meals", estimatedCost = 300 * request.travelers, travelTimeMinutes = 30, durationMinutes = 60),
            ItineraryItem(id = "9", title = "Tea Museum", type = ItineraryItemType.ATTRACTION, startTime = "03:00 PM", endTime = "05:00 PM", location = request.destination, description = "Learn about tea making", estimatedCost = 150 * request.travelers, travelTimeMinutes = 30, durationMinutes = 120),
            ItineraryItem(id = "10", title = "Free Time", type = ItineraryItemType.FREE_TIME, startTime = "05:00 PM", endTime = "07:00 PM", location = request.destination, description = "Explore local market", estimatedCost = 0, travelTimeMinutes = 0, durationMinutes = 120),
            ItineraryItem(id = "11", title = "Dinner", type = ItineraryItemType.RESTAURANT, startTime = "07:00 PM", endTime = "08:30 PM", location = request.destination, description = "Dinner at center", estimatedCost = 400 * request.travelers, travelTimeMinutes = 15, durationMinutes = 90)
        )

        val itemsDay3 = listOf(
            ItineraryItem(id = "12", title = "Breakfast", type = ItineraryItemType.RESTAURANT, startTime = "08:30 AM", endTime = "09:30 AM", location = request.destination, description = "Hotel breakfast", estimatedCost = 0, travelTimeMinutes = 0, durationMinutes = 60),
            ItineraryItem(id = "13", title = "Coonoor Trip", type = ItineraryItemType.ATTRACTION, startTime = "10:00 AM", endTime = "02:00 PM", location = "Coonoor", description = "Sightseeing nearby town", estimatedCost = 200 * request.travelers, travelTimeMinutes = 60, durationMinutes = 240),
            ItineraryItem(id = "14", title = "Travel back to ${request.from}", type = ItineraryItemType.TRANSPORT, startTime = "03:00 PM", endTime = "08:30 PM", location = request.destination, description = "Return journey", estimatedCost = 850 * request.travelers, travelTimeMinutes = 330, durationMinutes = 330)
        )


        val days = listOf(
            TripDay(1, "Arrival & Exploration", "Day 1", itemsDay1, itemsDay1.sumOf { it.estimatedCost }),
            TripDay(2, "Peaks & Culture", "Day 2", itemsDay2, itemsDay2.sumOf { it.estimatedCost }),
            TripDay(3, "Departure", "Day 3", itemsDay3, itemsDay3.sumOf { it.estimatedCost })
        )

        val summary = "A ${request.pace.lowercase()} 3-day ${request.destination} trip focused on ${request.interests.joinToString(", ")} while staying within your ₹${request.budget} budget."

        return TripPlan(
            id = UUID.randomUUID().toString(),
            from = request.from,
            destination = request.destination,
            startDate = request.startDate,
            endDate = request.endDate,
            travelers = request.travelers,
            budget = request.budget,
            interests = request.interests,
            travelStyle = request.travelStyle,
            hotelPreference = request.hotelPreference,
            transportPreference = request.transportPreference,
            days = days,
            budgetSummary = budgetSummary,
            summary = summary
        )
    }

    override suspend fun optimizeTrip(tripPlan: TripPlan): TripPlan {
        delay(1000)
        // Mock optimization: simply reduce some costs and reorder an item slightly
        val updatedBudget = tripPlan.budgetSummary.copy(total = tripPlan.budgetSummary.total - 500)
        return tripPlan.copy(budgetSummary = updatedBudget, summary = "Optimized: " + tripPlan.summary)
    }

    override suspend fun modifyTrip(tripPlan: TripPlan, instruction: String): TripPlan {
        delay(1000)
        // Mock modification
        val updatedSummary = "Modified based on: \"$instruction\".\n" + tripPlan.summary
        return tripPlan.copy(summary = updatedSummary)
    }
}
