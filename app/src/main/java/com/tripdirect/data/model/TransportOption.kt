package com.tripdirect.data.model

enum class TransportType {
    BUS,
    TRAIN,
    FLIGHT,
    TAXI,
    CAB,
    CAR_RENTAL
}

enum class PricingType {
    PER_PERSON,
    PER_TRIP,
    PER_DAY
}

data class TransportOption(
    val id: String,
    val type: TransportType,
    val providerName: String,
    val from: String,
    val to: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationMinutes: Int,
    val price: Int,
    val pricingType: PricingType,
    val rating: Double,
    val availableSeats: Int,
    val facilities: List<String>,
    val routeDescription: String,
    val isRecommended: Boolean
)
