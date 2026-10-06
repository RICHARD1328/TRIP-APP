package com.tripdirect.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tripdirect.data.model.Review
import com.tripdirect.data.model.ReviewCategory

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["entityId"])]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val userName: String,
    val userAvatarUrl: String? = null,
    val rating: Float,
    val title: String,
    val comment: String,
    val date: String,
    val categoryName: String,
    val entityId: String,
    val entityName: String,
    val helpfulCount: Int,
    val verifiedVisit: Boolean
) {
    fun toDomainModel(): Review {
        val cat = try {
            ReviewCategory.valueOf(categoryName)
        } catch (e: Exception) {
            ReviewCategory.ATTRACTION
        }
        return Review(
            id = id,
            userName = userName,
            userAvatarUrl = userAvatarUrl,
            rating = rating,
            title = title,
            comment = comment,
            date = date,
            category = cat,
            entityId = entityId,
            entityName = entityName,
            helpfulCount = helpfulCount,
            verifiedVisit = verifiedVisit
        )
    }

    companion object {
        fun fromDomainModel(model: Review): ReviewEntity {
            return ReviewEntity(
                id = model.id,
                userName = model.userName,
                userAvatarUrl = model.userAvatarUrl,
                rating = model.rating,
                title = model.title,
                comment = model.comment,
                date = model.date,
                categoryName = model.category.name,
                entityId = model.entityId,
                entityName = model.entityName,
                helpfulCount = model.helpfulCount,
                verifiedVisit = model.verifiedVisit
            )
        }
    }
}
