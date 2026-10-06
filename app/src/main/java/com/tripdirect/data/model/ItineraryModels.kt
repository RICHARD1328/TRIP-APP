package com.tripdirect.data.model

enum class ItineraryItemType {
    TRAVEL,
    TRANSPORT,
    HOTEL,
    ATTRACTION,
    RESTAURANT,
    ACTIVITY,
    BREAK,
    FREE_TIME,
    OTHER
}

enum class ItineraryStatus {
    UPCOMING,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED
}

data class ItineraryItem(
    val id: String = "",
    val startTime: String = "09:00 AM",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val type: ItineraryItemType = ItineraryItemType.ATTRACTION,
    val durationMinutes: Int = 0,
    val distanceFromPreviousKm: Double = 0.0,
    val travelTimeMinutes: Int = 0,
    val transportMode: String = "Car",
    val estimatedCost: Int = 0,
    val endTime: String = "10:00 AM",
    val openingTime: String? = null,
    val closingTime: String? = null,
    val notes: String? = null,
    val status: ItineraryStatus = ItineraryStatus.UPCOMING
) {
    val time: String get() = startTime
}


data class ItineraryDay(
    val dayNumber: Int,
    val date: String,
    val title: String,
    val activities: List<ItineraryItem>,
    val totalDistanceKm: Double,
    val totalTravelMinutes: Int,
    val estimatedCost: Int
) {
    val completedActivitiesCount: Int get() = activities.count { it.status == ItineraryStatus.COMPLETED }
    val activeActivities: List<ItineraryItem> get() = activities.filter { it.status != ItineraryStatus.SKIPPED }
}

data class Itinerary(
    val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val days: List<ItineraryDay>,
    val totalEstimatedCost: Int,
    val budget: Int
) {
    val totalDistanceKm: Double get() = days.sumOf { it.totalDistanceKm }
    val totalTravelMinutes: Int get() = days.sumOf { it.totalTravelMinutes }
    val isWithinBudget: Boolean get() = totalEstimatedCost <= budget
    val remainingBudget: Int get() = budget - totalEstimatedCost
}


