package com.tripdirect.data.model

data class Trip(
    val id: String,
    val destinationName: String,
    val durationDays: Int,
    val travelersCount: Int,
    val budget: Int,
    val progressPercentage: Int
)
