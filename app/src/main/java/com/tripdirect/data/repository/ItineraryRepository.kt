package com.tripdirect.data.repository

import com.tripdirect.data.model.Itinerary
import com.tripdirect.data.model.ItineraryDay
import com.tripdirect.data.model.ItineraryItem
import com.tripdirect.data.model.ItineraryItemType
import com.tripdirect.data.model.ItineraryStatus
import kotlinx.coroutines.delay
import java.util.UUID

interface ItineraryRepository {
    suspend fun getItinerary(tripId: String): Itinerary
    suspend fun saveItinerary(itinerary: Itinerary)
    suspend fun optimizeDay(day: ItineraryDay): ItineraryDay
    suspend fun addActivityToDay(itinerary: Itinerary, dayNumber: Int, item: ItineraryItem): Itinerary
    suspend fun removeActivity(itinerary: Itinerary, itemId: String): Itinerary
    suspend fun updateActivityStatus(itinerary: Itinerary, itemId: String, status: ItineraryStatus): Itinerary
}

class MockItineraryRepository : ItineraryRepository {

    override suspend fun getItinerary(tripId: String): Itinerary {
        delay(300) // Mock network delay
        
        val day1 = ItineraryDay(
            dayNumber = 1,
            date = "15 Aug",
            title = "Arrival & Ooty Exploration",
            totalDistanceKm = 18.4,
            totalTravelMinutes = 52,
            estimatedCost = 2450,
            activities = listOf(
                ItineraryItem(
                    id = "d1_1",
                    startTime = "08:00 AM",
                    endTime = "01:30 PM",
                    title = "Chennai → Ooty",
                    description = "Bus travel to destination",
                    location = "Ooty",
                    type = ItineraryItemType.TRAVEL,
                    durationMinutes = 330,
                    distanceFromPreviousKm = 265.0,
                    travelTimeMinutes = 330,
                    transportMode = "Bus",
                    estimatedCost = 1700
                ),
                ItineraryItem(
                    id = "d1_2",
                    startTime = "02:00 PM",
                    endTime = "02:30 PM",
                    title = "Hotel Check-in",
                    description = "Ooty Comfort Residency",
                    location = "Ooty City Center",
                    type = ItineraryItemType.HOTEL,
                    durationMinutes = 30,
                    distanceFromPreviousKm = 2.0,
                    travelTimeMinutes = 10,
                    transportMode = "Taxi",
                    estimatedCost = 1500
                ),
                ItineraryItem(
                    id = "d1_3",
                    startTime = "03:30 PM",
                    endTime = "04:45 PM",
                    title = "Ooty Lake",
                    description = "Boating and scenic walking",
                    location = "Ooty Lake",
                    type = ItineraryItemType.ATTRACTION,
                    durationMinutes = 75,
                    distanceFromPreviousKm = 1.2,
                    travelTimeMinutes = 10,
                    transportMode = "Taxi",
                    estimatedCost = 50,
                    openingTime = "09:00 AM",
                    closingTime = "06:00 PM"
                ),
                ItineraryItem(
                    id = "d1_4",
                    startTime = "05:00 PM",
                    endTime = "06:30 PM",
                    title = "Botanical Garden",
                    description = "Explore exotic flora and glasshouse",
                    location = "Botanical Garden",
                    type = ItineraryItemType.ATTRACTION,
                    durationMinutes = 90,
                    distanceFromPreviousKm = 2.4,
                    travelTimeMinutes = 12,
                    transportMode = "Taxi",
                    estimatedCost = 50,
                    openingTime = "08:00 AM",
                    closingTime = "06:00 PM"
                ),
                ItineraryItem(
                    id = "d1_5",
                    startTime = "07:30 PM",
                    endTime = "08:30 PM",
                    title = "Dinner",
                    description = "Nilgiri Spice Restaurant",
                    location = "City Center",
                    type = ItineraryItemType.RESTAURANT,
                    durationMinutes = 60,
                    distanceFromPreviousKm = 1.0,
                    travelTimeMinutes = 5,
                    transportMode = "Walk",
                    estimatedCost = 300
                )
            )
        )

        val day2 = ItineraryDay(
            dayNumber = 2,
            date = "16 Aug",
            title = "Peaks, Tea & Coonoor",
            totalDistanceKm = 24.5,
            totalTravelMinutes = 75,
            estimatedCost = 3200,
            activities = listOf(
                ItineraryItem("d2_1", "08:30 AM", "Breakfast", "Hotel breakfast", "Hotel", ItineraryItemType.RESTAURANT, 45, 0.0, 0, "Walk", 0),
                ItineraryItem("d2_2", "09:30 AM", "Doddabetta Peak", "Panoramic viewpoint", "Doddabetta", ItineraryItemType.ATTRACTION, 120, 8.0, 30, "Taxi", 50, "07:00 AM", "06:00 PM"),
                ItineraryItem("d2_3", "12:00 PM", "Tea Museum & Factory", "Tea processing demonstration", "Tea Factory", ItineraryItemType.ATTRACTION, 60, 4.0, 15, "Taxi", 100, "09:00 AM", "05:30 PM"),
                ItineraryItem("d2_4", "01:30 PM", "Lunch", "Nilgiri Thali", "Commercial Road", ItineraryItemType.RESTAURANT, 60, 2.0, 10, "Walk", 300),
                ItineraryItem("d2_5", "03:00 PM", "Coonoor Trip & Sim's Park", "Botanical park in Coonoor", "Coonoor", ItineraryItemType.ACTIVITY, 180, 15.0, 45, "Toy Train", 200)
            )
        )

        val day3 = ItineraryDay(
            dayNumber = 3,
            date = "17 Aug",
            title = "Rose Garden, Shopping & Departure",
            totalDistanceKm = 265.0,
            totalTravelMinutes = 360,
            estimatedCost = 1600,
            activities = listOf(
                ItineraryItem("d3_1", "09:00 AM", "Rose Garden", "Stroll among thousands of rose varieties", "Elk Hill", ItineraryItemType.ATTRACTION, 90, 2.5, 12, "Taxi", 50),
                ItineraryItem("d3_2", "11:00 AM", "Shopping at Commercial Street", "Handmade chocolates and tea leaves", "Commercial Street", ItineraryItemType.ACTIVITY, 120, 2.0, 10, "Walk", 500),
                ItineraryItem("d3_3", "01:30 PM", "Hotel Checkout", "Ooty Comfort Residency", "Ooty", ItineraryItemType.HOTEL, 30, 0.0, 0, "Walk", 0),
                ItineraryItem("d3_4", "03:00 PM", "Ooty → Chennai", "Return bus journey", "Chennai", ItineraryItemType.TRAVEL, 360, 265.0, 360, "Bus", 1700)
            )
        )

        return Itinerary(
            id = tripId.ifEmpty { "mock_ooty_123" },
            destination = "Ooty",
            startDate = "15 Aug",
            endDate = "17 Aug",
            travelers = 2,
            days = listOf(day1, day2, day3),
            totalEstimatedCost = 8950,
            budget = 10000
        )
    }

    override suspend fun saveItinerary(itinerary: Itinerary) {
        delay(200)
    }

    override suspend fun optimizeDay(day: ItineraryDay): ItineraryDay {
        delay(300)
        return day.copy(
            totalDistanceKm = (day.totalDistanceKm * 0.82).coerceAtLeast(1.0),
            totalTravelMinutes = ((day.totalTravelMinutes * 0.82)).toInt().coerceAtLeast(10),
            estimatedCost = (day.estimatedCost * 0.95).toInt()
        )
    }

    override suspend fun addActivityToDay(itinerary: Itinerary, dayNumber: Int, item: ItineraryItem): Itinerary {
        delay(150)
        val updatedDays = itinerary.days.map { day ->
            if (day.dayNumber == dayNumber) {
                val newActivities = day.activities + item
                day.copy(
                    activities = newActivities,
                    estimatedCost = day.estimatedCost + item.estimatedCost
                )
            } else day
        }
        val newTotalCost = updatedDays.sumOf { it.estimatedCost }
        return itinerary.copy(days = updatedDays, totalEstimatedCost = newTotalCost)
    }

    override suspend fun removeActivity(itinerary: Itinerary, itemId: String): Itinerary {
        delay(150)
        val updatedDays = itinerary.days.map { day ->
            val filtered = day.activities.filter { it.id != itemId }
            day.copy(
                activities = filtered,
                estimatedCost = filtered.sumOf { it.estimatedCost }
            )
        }
        val newTotalCost = updatedDays.sumOf { it.estimatedCost }
        return itinerary.copy(days = updatedDays, totalEstimatedCost = newTotalCost)
    }

    override suspend fun updateActivityStatus(itinerary: Itinerary, itemId: String, status: ItineraryStatus): Itinerary {
        delay(150)
        val updatedDays = itinerary.days.map { day ->
            val updated = day.activities.map { item ->
                if (item.id == itemId) item.copy(status = status) else item
            }
            day.copy(activities = updated)
        }
        return itinerary.copy(days = updatedDays)
    }
}

