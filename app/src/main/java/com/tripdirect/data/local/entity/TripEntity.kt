package com.tripdirect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tripdirect.data.model.UserTripSummary

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val destination: String,
    val dates: String,
    val durationDays: Int,
    val travelers: Int,
    val totalCost: Double,
    val status: String,
    val imageUrl: String? = null
) {
    fun toDomainModel(): UserTripSummary {
        return UserTripSummary(
            id = id,
            destination = destination,
            dates = dates,
            durationDays = durationDays,
            travelers = travelers,
            totalCost = totalCost,
            status = status
        )
    }

    companion object {
        fun fromDomainModel(model: UserTripSummary, imageUrl: String? = null): TripEntity {
            return TripEntity(
                id = model.id,
                destination = model.destination,
                dates = model.dates,
                durationDays = model.durationDays,
                travelers = model.travelers,
                totalCost = model.totalCost,
                status = model.status,
                imageUrl = imageUrl
            )
        }
    }
}
