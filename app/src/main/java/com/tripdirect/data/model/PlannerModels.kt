package com.tripdirect.data.model

data class TripPlanRequest(
    val from: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val budget: Int,
    val interests: List<String>,
    val travelStyle: String,
    val hotelPreference: String,
    val transportPreference: String,
    val pace: String
)

data class TripPlan(
    val id: String,
    val from: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val budget: Int,
    val interests: List<String>,
    val travelStyle: String,
    val hotelPreference: String,
    val transportPreference: String,
    val days: List<TripDay>,
    val budgetSummary: TripBudget,
    val summary: String
)

data class TripDay(
    val dayNumber: Int,
    val title: String,
    val date: String,
    val items: List<ItineraryItem>,
    val estimatedCost: Int
)

data class TripBudget(
    val transport: Int,
    val accommodation: Int,
    val food: Int,
    val activities: Int,
    val localTransport: Int,
    val miscellaneous: Int,
    val total: Int,
    val userBudget: Int
)

