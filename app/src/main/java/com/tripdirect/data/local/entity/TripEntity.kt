package com.tripdirect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tripdirect.data.model.UserTripSummary

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val title: String,
    val dates: String,
    val durationDays: Int,
    val travelersCount: Int,
    val estimatedBudget: Double,
    val status: String,
    val imageUrl: String? = null
) {
    fun toDomainModel(): UserTripSummary {
        return UserTripSummary(
            id = id,
            title = title,
            dates = dates,
            durationDays = durationDays,
            travelersCount = travelersCount,
            estimatedBudget = estimatedBudget,
            status = status
        )
    }

    companion object {
        fun fromDomainModel(model: UserTripSummary, imageUrl: String? = null): TripEntity {
            return TripEntity(
                id = model.id,
                title = model.title,
                dates = model.dates,
                durationDays = model.durationDays,
                travelersCount = model.travelersCount,
                estimatedBudget = model.estimatedBudget,
                status = model.status,
                imageUrl = imageUrl
            )
        }
    }
}
