package com.tripdirect.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class AiPlannerNetworkRequest(
    val origin: String = "Chennai",
    val destination: String,
    val start_date: String,
    val end_date: String,
    val travelers: Int = 2,
    val budget: Double,
    val interests: List<String> = emptyList(),
    val travel_style: String = "balanced",
    val transport_preference: String = "car",
    val accommodation_preference: String = "budget"
)

data class DestinationInfoDto(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String = "",
    val state: String = "",
    val country: String = ""
)

data class WeatherTempDto(
    val min: Double = 0.0,
    val max: Double = 0.0,
    val unit: String = "°C"
)

data class WeatherDto(
    val date: String = "",
    val temperature: WeatherTempDto = WeatherTempDto(),
    val precipitation_probability: Int = 0,
    val weather_condition: String = "",
    val wind_speed: Double = 0.0,
    val forecast_available: Boolean = true
)

data class AiPlannerPlaceDto(
    val place_id: String = "",
    val name: String = "",
    val category: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String? = null,
    val website: String? = null,
    val phone: String? = null,
    val opening_hours: String? = null,
    val image_url: String? = null,
    val score: Double = 0.0,
    val reasons: List<String> = emptyList(),
    val source: String = "OpenStreetMap"
)

data class RouteDto(
    val from_location: String = "",
    val to_location: String = "",
    val distance_km: Double = 0.0,
    val duration_minutes: Double = 0.0,
    val source: String = "OSRM Routing"
)

data class ItinerarySlotDto(
    val slot_id: String = "",
    val time_of_day: String = "",
    val activity: String = "",
    val place_id: String? = null,
    val description: String = "",
    val location: String = "",
    val category: String = "",
    val image_url: String? = null,
    val estimated_cost: Double = 0.0,
    val weather_note: String? = null
)

data class DailyItineraryDto(
    val day_number: Int = 1,
    val date: String = "",
    val title: String = "",
    val weather_summary: String = "",
    val slots: List<ItinerarySlotDto> = emptyList()
)

data class BudgetCategoryDto(
    val transportation: Double = 0.0,
    val accommodation: Double = 0.0,
    val food: Double = 0.0,
    val activities: Double = 0.0,
    val local_travel: Double = 0.0,
    val other: Double = 0.0,
    val emergency_reserve: Double = 0.0
)

data class BudgetSummaryDto(
    val categories: BudgetCategoryDto = BudgetCategoryDto(),
    val total_estimated_cost: Double = 0.0,
    val cost_per_traveler: Double = 0.0,
    val cost_per_day: Double = 0.0,
    val user_budget: Double = 0.0,
    val remaining_budget: Double = 0.0,
    val is_within_budget: Boolean = true,
    val is_live_pricing: Boolean = false,
    val price_data_source: String = "Estimated"
)

data class DataSourceMetaDto(
    val source: String = "",
    val fetched_at: String = "",
    val status: String = "",
    val cached: Boolean = false
)

data class SocialContentDto(
    val id: String = "",
    val destination_id: String = "",
    val platform: String = "instagram",
    val title: String = "",
    val reel_url: String = "",
    val thumbnail_url: String? = null,
    val creator_name: String? = null,
    val description: String? = null,
    val verified: Boolean = true,
    val active: Boolean = true
)

data class DestinationDetailResponseDto(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = "",
    val images: List<String> = emptyList(),
    val social_content: List<SocialContentDto> = emptyList()
)

data class AiPlannerNetworkResponse(
    val trip_id: String = "",
    val destination: DestinationInfoDto = DestinationInfoDto(),
    val origin: DestinationInfoDto = DestinationInfoDto(),
    val weather: List<WeatherDto> = emptyList(),
    val recommended_places: List<AiPlannerPlaceDto> = emptyList(),
    val itinerary: List<DailyItineraryDto> = emptyList(),
    val routes: List<RouteDto> = emptyList(),
    val budget: BudgetSummaryDto = BudgetSummaryDto(),
    val data_sources: List<DataSourceMetaDto> = emptyList(),
    val generated_at: String = "",
    val live_data: Boolean = false,
    val real_visuals: List<SocialContentDto> = emptyList()
)

interface AiPlannerApi {
    @GET("api/v1/health")
    suspend fun getHealth(): Response<HealthCheckResponseDto>

    @POST("api/v1/ai-planner/plan")
    suspend fun generateTripPlan(@Body request: AiPlannerNetworkRequest): Response<AiPlannerNetworkResponse>

    @POST("api/v1/ai-planner/regenerate")
    suspend fun regenerateTripPlan(@Body request: AiPlannerNetworkRequest): Response<AiPlannerNetworkResponse>

    @GET("api/v1/trips/{trip_id}")
    suspend fun getSavedTrip(@Path("trip_id") tripId: String): Response<AiPlannerNetworkResponse>

    @GET("api/v1/destinations/{destination_id}")
    suspend fun getDestinationDetail(@Path("destination_id") destinationId: String): Response<DestinationDetailResponseDto>
}

data class HealthCheckResponseDto(
    val status: String = "",
    val service: String = ""
)
