package com.tripdirect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tripdirect.data.model.SavedPlace

@Entity(tableName = "saved_places")
data class SavedPlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val rating: Float,
    val category: String,
    val imageUrl: String? = null,
    val destination: String = "Ooty"
) {
    fun toDomainModel(): SavedPlace {
        return SavedPlace(
            id = id,
            name = name,
            location = location,
            rating = rating,
            category = category,
            imageUrl = imageUrl,
            destination = destination
        )
    }

    companion object {
        fun fromDomainModel(model: SavedPlace): SavedPlaceEntity {
            return SavedPlaceEntity(
                id = model.id,
                name = model.name,
                location = model.location,
                rating = model.rating,
                category = model.category,
                imageUrl = model.imageUrl,
                destination = model.destination
            )
        }
    }
}
