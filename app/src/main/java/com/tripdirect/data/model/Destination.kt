package com.tripdirect.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class AttractionInfo(
    val id: String,
    val name: String,
    val category: String, // e.g. "Boating", "Viewpoint", "Nature"
    val rating: Double,
    val description: String,
    val ticketPriceStatus: String = "Check current price", // "Check current price" or verified string
    val imageUrl: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class HotelInfo(
    val id: String,
    val name: String,
    val rating: Double,
    val priceInfo: String = "Prices vary — check current availability", // Transparent pricing requirement
    val distance: String,
    val facilities: List<String>,
    val imageUrl: String
)

data class TransportModeInfo(
    val title: String,
    val details: String,
    val iconType: String // "train", "flight", "bus", "road"
)

data class TransportInfo(
    val railway: String,
    val airport: String,
    val bus: String,
    val road: String
)

data class LocalTransportOption(
    val mode: String, // "Taxi", "Auto", "Bus", "Rental Car", "Rental Bike"
    val priceNote: String = "Price varies by operator and season."
)

data class BestTimeInfo(
    val season: String,
    val months: String,
    val description: String
)

data class ThingToDoInfo(
    val category: String, // "🌿 Nature", "🏞 Sightseeing", "🍴 Food", "📸 Photography", "🥾 Adventure", "🛍 Shopping"
    val title: String,
    val description: String
)

data class LocalFoodInfo(
    val dishName: String,
    val description: String,
    val type: String = "Local Speciality"
)

data class ReviewInfo(
    val author: String,
    val rating: Double,
    val text: String,
    val date: String
)

data class EstimatedTripCost(
    val transport: Int = 2500,
    val hotel: Int = 3000,
    val food: Int = 1500,
    val localTransport: Int = 1000,
    val activities: Int = 500
) {
    val total: Int get() = transport + hotel + food + localTransport + activities
}

data class Destination(
    val id: String,
    val name: String,
    val officialName: String? = null,
    val tagline: String? = null,
    val state: String,
    val country: String = "India",
    val region: String,
    val imageRes: Int? = null,
    val vectorIcon: ImageVector? = null,
    val rating: Double,
    val category: String,
    val description: String,
    val estimatedDailyBudget: String,
    val weather: String,
    val latitude: Double,
    val longitude: Double,
    val googleSearchQuery: String,
    val fallbackImageUrl: String,
    val popularAttractions: List<AttractionInfo> = emptyList(),
    val hotels: List<HotelInfo> = emptyList(),
    val transportInfo: TransportInfo,
    val localTransport: List<LocalTransportOption> = emptyList(),
    val bestTime: List<BestTimeInfo> = emptyList(),
    val thingsToDo: List<ThingToDoInfo> = emptyList(),
    val localFood: List<LocalFoodInfo> = emptyList(),
    val safetyInformation: String,
    val reviews: List<ReviewInfo> = emptyList(),
    val isSaved: Boolean = false,
    val costBreakdown: EstimatedTripCost = EstimatedTripCost()
) {
    val image: String get() = fallbackImageUrl.ifBlank { DestinationImageProvider.getDestinationImage(name) ?: "" }
}

