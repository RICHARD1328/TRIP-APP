package com.tripdirect.data.repository

import com.google.gson.Gson
import com.tripdirect.data.local.database.DatabaseConverters
import com.tripdirect.data.local.database.TripDirectDatabase
import com.tripdirect.data.local.entity.ItineraryEntity
import com.tripdirect.data.model.Itinerary
import com.tripdirect.data.model.ItineraryDay
import com.tripdirect.data.model.ItineraryItem
import com.tripdirect.data.model.ItineraryItemType
import com.tripdirect.data.model.ItineraryStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ItineraryRepository {
    suspend fun getItinerary(tripId: String): Itinerary
    suspend fun saveItinerary(itinerary: Itinerary)
    suspend fun optimizeDay(day: ItineraryDay): ItineraryDay
    suspend fun addActivityToDay(itinerary: Itinerary, dayNumber: Int, item: ItineraryItem): Itinerary
    suspend fun removeActivity(itinerary: Itinerary, itemId: String): Itinerary
    suspend fun updateActivityStatus(itinerary: Itinerary, itemId: String, status: ItineraryStatus): Itinerary
}

class MockItineraryRepository(
    private val database: TripDirectDatabase? = null
) : ItineraryRepository {

    private val gson = Gson()

    override suspend fun getItinerary(tripId: String): Itinerary = withContext(Dispatchers.IO) {
        val targetId = tripId.ifEmpty { "mock_ooty_123" }

        // 1. Try Room Database if available
        database?.let { db ->
            try {
                val entity = db.itineraryDao().getItineraryById(targetId)
                if (entity != null) {
                    val converters = DatabaseConverters()
                    val days = converters.toItineraryDaysList(entity.daysJson)
                    if (days.isNotEmpty()) {
                        return@withContext Itinerary(
                            id = entity.id,
                            destination = entity.destination,
                            startDate = entity.startDate,
                            endDate = entity.endDate,
                            travelers = entity.travelers,
                            days = days,
                            totalEstimatedCost = entity.totalEstimatedCost,
                            budget = entity.budget
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback to mock initialization
            }
        }

        // 2. Initial Default Itinerary
        val defaultItinerary = createDefaultItinerary(targetId)

        // Save default to Room Database
        database?.let { db ->
            try {
                val daysJson = gson.toJson(defaultItinerary.days)
                val entity = ItineraryEntity(
                    id = defaultItinerary.id,
                    tripId = defaultItinerary.id,
                    destination = defaultItinerary.destination,
                    startDate = defaultItinerary.startDate,
                    endDate = defaultItinerary.endDate,
                    travelers = defaultItinerary.travelers,
                    totalEstimatedCost = defaultItinerary.totalEstimatedCost,
                    budget = defaultItinerary.budget,
                    daysJson = daysJson
                )
                db.itineraryDao().insertItinerary(entity)
            } catch (e: Exception) {
                // Ignore
            }
        }

        defaultItinerary
    }

    override suspend fun saveItinerary(itinerary: Itinerary): Unit = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                val daysJson = gson.toJson(itinerary.days)
                val entity = ItineraryEntity(
                    id = itinerary.id,
                    tripId = itinerary.id,
                    destination = itinerary.destination,
                    startDate = itinerary.startDate,
                    endDate = itinerary.endDate,
                    travelers = itinerary.travelers,
                    totalEstimatedCost = itinerary.totalEstimatedCost,
                    budget = itinerary.budget,
                    daysJson = daysJson
                )
                db.itineraryDao().insertItinerary(entity)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    override suspend fun optimizeDay(day: ItineraryDay): ItineraryDay {
        return day.copy(
            totalDistanceKm = (day.totalDistanceKm * 0.82).coerceAtLeast(1.0),
            totalTravelMinutes = ((day.totalTravelMinutes * 0.82)).toInt().coerceAtLeast(10),
            estimatedCost = (day.estimatedCost * 0.95).toInt()
        )
    }

    override suspend fun addActivityToDay(itinerary: Itinerary, dayNumber: Int, item: ItineraryItem): Itinerary {
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
        val updatedItinerary = itinerary.copy(days = updatedDays, totalEstimatedCost = newTotalCost)
        saveItinerary(updatedItinerary)
        return updatedItinerary
    }

    override suspend fun removeActivity(itinerary: Itinerary, itemId: String): Itinerary {
        val updatedDays = itinerary.days.map { day ->
            val filtered = day.activities.filter { it.id != itemId }
            day.copy(
                activities = filtered,
                estimatedCost = filtered.sumOf { it.estimatedCost }
            )
        }
        val newTotalCost = updatedDays.sumOf { it.estimatedCost }
        val updatedItinerary = itinerary.copy(days = updatedDays, totalEstimatedCost = newTotalCost)
        saveItinerary(updatedItinerary)
        return updatedItinerary
    }

    override suspend fun updateActivityStatus(itinerary: Itinerary, itemId: String, status: ItineraryStatus): Itinerary {
        val updatedDays = itinerary.days.map { day ->
            val updated = day.activities.map { item ->
                if (item.id == itemId) item.copy(status = status) else item
            }
            day.copy(activities = updated)
        }
        val updatedItinerary = itinerary.copy(days = updatedDays)
        saveItinerary(updatedItinerary)
        return updatedItinerary
    }

    private fun createDefaultItinerary(tripId: String): Itinerary {
        val day1 = ItineraryDay(
            dayNumber = 1,
            date = "15 Aug",
            title = "Arrival & Ooty Exploration",
            totalDistanceKm = 18.4,
            totalTravelMinutes = 52,
            estimatedCost = 2450,
            activities = listOf(
                ItineraryItem("d1_1", "08:00 AM", "Chennai → Ooty", "Bus travel to destination", "Ooty", ItineraryItemType.TRAVEL, 330, 265.0, 330, "Bus", 1700),
                ItineraryItem("d1_2", "02:00 PM", "Hotel Check-in", "Ooty Comfort Residency", "Ooty City Center", ItineraryItemType.HOTEL, 30, 2.0, 10, "Taxi", 1500),
                ItineraryItem("d1_3", "03:30 PM", "Ooty Lake", "Boating and scenic walking", "Ooty Lake", ItineraryItemType.ATTRACTION, 75, 1.2, 10, "Taxi", 50, "09:00 AM", "06:00 PM"),
                ItineraryItem("d1_4", "05:00 PM", "Botanical Garden", "Explore exotic flora and glasshouse", "Botanical Garden", ItineraryItemType.ATTRACTION, 90, 2.4, 12, "Taxi", 50, "08:00 AM", "06:00 PM"),
                ItineraryItem("d1_5", "07:30 PM", "Dinner", "Nilgiri Spice Restaurant", "City Center", ItineraryItemType.RESTAURANT, 60, 1.0, 5, "Walk", 300)
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
            id = tripId,
            destination = "Ooty",
            startDate = "15 Aug",
            endDate = "17 Aug",
            travelers = 2,
            days = listOf(day1, day2, day3),
            totalEstimatedCost = 8950,
            budget = 10000
        )
    }
}
