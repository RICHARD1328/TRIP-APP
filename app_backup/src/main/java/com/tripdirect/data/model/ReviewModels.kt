package com.tripdirect.data.model

enum class ReviewCategory {
    ALL,
    HOTEL,
    ATTRACTION,
    RESTAURANT,
    TRANSPORT,
    ACTIVITY,
    DESTINATION
}

enum class ReviewSortOption {
    RELEVANT,
    NEWEST,
    HIGHEST_RATING,
    LOWEST_RATING,
    MOST_HELPFUL
}

data class Review(
    val id: String,
    val userName: String,
    val userAvatarUrl: String? = null,
    val rating: Float,
    val title: String,
    val comment: String,
    val date: String,
    val category: ReviewCategory,
    val entityId: String = "ooty_botanical_garden",
    val entityName: String = "Ooty Botanical Garden",
    val helpfulCount: Int = 0,
    val photos: List<String> = emptyList(),
    val verifiedVisit: Boolean = true,
    val categoryRatings: Map<String, Float> = emptyMap()
)

data class ReviewSummary(
    val entityName: String,
    val entityLocation: String,
    val averageRating: Float,
    val totalReviews: Int,
    val fiveStar: Int,
    val fourStar: Int,
    val threeStar: Int,
    val twoStar: Int,
    val oneStar: Int,
    val aspectRatings: Map<String, Float> = emptyMap(),
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList()
) {
    val fiveStarPct: Int get() = if (totalReviews > 0) ((fiveStar.toDouble() / totalReviews) * 100).toInt() else 0
    val fourStarPct: Int get() = if (totalReviews > 0) ((fourStar.toDouble() / totalReviews) * 100).toInt() else 0
    val threeStarPct: Int get() = if (totalReviews > 0) ((threeStar.toDouble() / totalReviews) * 100).toInt() else 0
    val twoStarPct: Int get() = if (totalReviews > 0) ((twoStar.toDouble() / totalReviews) * 100).toInt() else 0
    val oneStarPct: Int get() = if (totalReviews > 0) ((oneStar.toDouble() / totalReviews) * 100).toInt() else 0
}
