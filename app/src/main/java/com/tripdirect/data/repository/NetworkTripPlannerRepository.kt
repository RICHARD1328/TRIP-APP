package com.tripdirect.data.repository

import com.tripdirect.data.model.*
import com.tripdirect.data.network.*
import java.util.UUID

class NetworkTripPlannerRepository : TripPlannerRepository {

    private val api: AiPlannerApi
        get() = ApiClient.getAiPlannerApi()

    private val fallbackRepo = MockTripPlannerRepository()

    override suspend fun generateTripPlan(request: TripPlanRequest): TripPlan {
        return try {
            val netReq = AiPlannerNetworkRequest(
                origin = request.from.ifBlank { "Chennai" },
                destination = request.destination,
                start_date = request.startDate,
                end_date = request.endDate,
                travelers = request.travelers,
                budget = request.budget.toDouble(),
                interests = request.interests,
                travel_style = request.travelStyle.lowercase(),
                transport_preference = request.transportPreference.lowercase(),
                accommodation_preference = request.hotelPreference.lowercase()
            )

            val resp = api.generateTripPlan(netReq)
            if (resp.isSuccessful && resp.body() != null) {
                mapResponseToTripPlan(resp.body()!!, request)
            } else {
                throw retrofit2.HttpException(resp)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun optimizeTrip(tripPlan: TripPlan): TripPlan {
        return fallbackRepo.optimizeTrip(tripPlan)
    }

    override suspend fun modifyTrip(tripPlan: TripPlan, instruction: String): TripPlan {
        return fallbackRepo.modifyTrip(tripPlan, instruction)
    }

    private fun mapResponseToTripPlan(net: AiPlannerNetworkResponse, req: TripPlanRequest): TripPlan {
        val days = net.itinerary.map { dDto ->
            val items = dDto.slots.map { sDto ->
                val weatherNoteText = if (sDto.weather_note != null && sDto.weather_note.isNotBlank()) " (${sDto.weather_note})" else ""
                ItineraryItem(
                    id = if (sDto.slot_id.isNotBlank()) sDto.slot_id else UUID.randomUUID().toString(),
                    title = sDto.activity,
                    type = when (sDto.category.lowercase()) {
                        "restaurant", "cafe" -> ItineraryItemType.RESTAURANT
                        "hotel" -> ItineraryItemType.HOTEL
                        "travel", "transport" -> ItineraryItemType.TRANSPORT
                        "activity" -> ItineraryItemType.ACTIVITY
                        "free_time" -> ItineraryItemType.FREE_TIME
                        else -> ItineraryItemType.ATTRACTION
                    },
                    startTime = sDto.time_of_day.split("-").firstOrNull()?.trim() ?: "09:00 AM",
                    endTime = sDto.time_of_day.split("-").lastOrNull()?.trim() ?: "11:30 AM",
                    location = if (sDto.location.isNotBlank()) sDto.location else net.destination.name,
                    description = sDto.description + weatherNoteText,
                    estimatedCost = sDto.estimated_cost.toInt(),
                    travelTimeMinutes = 15,
                    durationMinutes = 120
                )
            }
            TripDay(
                dayNumber = dDto.day_number,
                title = dDto.title,
                date = dDto.date,
                items = items,
                estimatedCost = items.sumOf { it.estimatedCost }
            )
        }

        val bDto = net.budget
        val cDto = bDto.categories

        val budgetSummary = TripBudget(
            transport = cDto.transportation.toInt(),
            accommodation = cDto.accommodation.toInt(),
            food = cDto.food.toInt(),
            activities = cDto.activities.toInt(),
            localTransport = cDto.local_travel.toInt(),
            miscellaneous = (cDto.other + cDto.emergency_reserve).toInt(),
            total = bDto.total_estimated_cost.toInt(),
            userBudget = bDto.user_budget.toInt()
        )

        val liveLabel = if (net.live_data) "Powered by live OSM, Open-Meteo & OSRM data." else "Using cached regional travel data."
        val summary = "Customized ${req.pace.lowercase()} ${days.size}-day ${net.destination.name} itinerary. $liveLabel"

        return TripPlan(
            id = if (net.trip_id.isNotBlank()) net.trip_id else UUID.randomUUID().toString(),
            from = if (net.origin.name.isNotBlank()) net.origin.name else req.from,
            destination = if (net.destination.name.isNotBlank()) net.destination.name else req.destination,
            startDate = req.startDate,
            endDate = req.endDate,
            travelers = req.travelers,
            budget = req.budget,
            interests = req.interests,
            travelStyle = req.travelStyle,
            hotelPreference = req.hotelPreference,
            transportPreference = req.transportPreference,
            days = days,
            budgetSummary = budgetSummary,
            summary = summary
        )
    }
}
