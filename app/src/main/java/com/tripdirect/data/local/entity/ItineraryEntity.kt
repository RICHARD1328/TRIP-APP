package com.tripdirect.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "itineraries",
    indices = [Index(value = ["tripId"])]
)
data class ItineraryEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val totalEstimatedCost: Int,
    val budget: Int,
    val daysJson: String
)
