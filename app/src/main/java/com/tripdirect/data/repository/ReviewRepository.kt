package com.tripdirect.data.repository

import com.tripdirect.data.local.database.TripDirectDatabase
import com.tripdirect.data.local.entity.ReviewEntity
import com.tripdirect.data.model.Review
import com.tripdirect.data.model.ReviewCategory
import com.tripdirect.data.model.ReviewSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ReviewRepository {
    suspend fun getReviews(entityId: String): List<Review>
    suspend fun getReviewSummary(entityId: String): ReviewSummary
    suspend fun submitReview(review: Review): Review
    suspend fun reportReview(reviewId: String, reason: String)
    suspend fun markHelpful(reviewId: String): Int
    suspend fun getUserReviews(userId: String): List<Review>
    suspend fun deleteReview(reviewId: String)
}

class MockReviewRepository(
    private val database: TripDirectDatabase? = null
) : ReviewRepository {

    private val defaultReviews = listOf(
        Review("rev_1", "Arun Kumar", null, 5.0f, "Great experience for families!", "The garden was extremely well maintained and clean. Wonderful variety of exotic plants and a peaceful glasshouse.", "10 Aug 2026", ReviewCategory.ATTRACTION, "ooty_botanical_garden", "Ooty Botanical Garden", 24, emptyList(), true),
        Review("rev_2", "Priya Sharma", null, 4.0f, "Beautiful location but crowded in afternoons", "Very peaceful environment if you visit early morning around 8:30 AM.", "08 Aug 2026", ReviewCategory.ATTRACTION, "ooty_botanical_garden", "Ooty Botanical Garden", 18, emptyList(), true),
        Review("rev_3", "Rahul Verma", null, 5.0f, "Excellent stay at Ooty Comfort Residency", "Staff was welcoming and room service was prompt. Close to commercial road.", "05 Aug 2026", ReviewCategory.HOTEL, "ooty_hotel_1", "Ooty Comfort Residency", 31, emptyList(), true),
        Review("rev_4", "Kavitha R.", null, 5.0f, "Authentic Nilgiri Thali & Local Flavor", "Nilgiri Spice Restaurant served fresh hot food. The local mushroom curry and tea were highlights.", "02 Aug 2026", ReviewCategory.RESTAURANT, "ooty_rest_1", "Nilgiri Spice Restaurant", 15, emptyList(), true),
        Review("rev_5", "Suresh Menon", null, 4.0f, "Punctual bus ride from Chennai", "Bus left on time. Seats were comfortable with charging ports.", "28 Jul 2026", ReviewCategory.TRANSPORT, "ooty_trans_1", "Chennai - Ooty Express Bus", 12, emptyList(), true),
        Review("rev_6", "Deepak Patel", null, 3.0f, "Nice lake but long queue for boating", "Boating experience at Ooty Lake was good, but long line.", "25 Jul 2026", ReviewCategory.ATTRACTION, "ooty_lake", "Ooty Lake", 9, emptyList(), false),
        Review("rev_7", "Anita Roy", null, 2.0f, "Parking was difficult during peak hours", "Finding a parking spot took more than 30 minutes.", "20 Jul 2026", ReviewCategory.DESTINATION, "doddabetta_peak", "Doddabetta Peak", 22, emptyList(), true),
        Review("rev_8", "Vikram Singh", null, 1.0f, "Overpriced vendor stalls near entrance", "The entrance vendors charge double the price.", "15 Jul 2026", ReviewCategory.ACTIVITY, "tea_museum_ooty", "Tea Factory & Tea Museum", 7, emptyList(), false),
        Review("rev_9", "Meera Nair", null, 5.0f, "Fascinating tea history & fresh tasting", "Tata Tea Museum in Munnar was wonderful. The demonstration of tea processing and antique machinery was captivating.", "12 Jul 2026", ReviewCategory.ATTRACTION, "munnar_tea_museum", "Munnar Tea Museum", 16, emptyList(), true),
        Review("rev_10", "Siddharth Rao", null, 4.5f, "Stunning coastal view from lighthouse", "The Arabian sea view from Fort Aguada was spectacular. Great historical atmosphere.", "05 Jul 2026", ReviewCategory.ATTRACTION, "fort_aguada", "Fort Aguada", 28, emptyList(), true)
    )

    override suspend fun getReviews(entityId: String): List<Review> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                val list = db.reviewDao().getReviewsForEntityList(entityId)
                if (list.isNotEmpty()) {
                    return@withContext list.map { it.toDomainModel() }
                } else {
                    // Seed initial reviews
                    val entities = defaultReviews.map { ReviewEntity.fromDomainModel(it) }
                    db.reviewDao().insertReviews(entities)
                    return@withContext if (entityId.isEmpty() || entityId == "all") defaultReviews else defaultReviews.filter { it.entityId == entityId }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        if (entityId.isEmpty() || entityId == "all") defaultReviews else defaultReviews.filter { it.entityId == entityId }
    }

    override suspend fun getReviewSummary(entityId: String): ReviewSummary = withContext(Dispatchers.IO) {
        val targetReviews = getReviews(entityId)
        val count = targetReviews.size
        val avg = if (count > 0) targetReviews.map { it.rating }.average().toFloat() else 4.4f

        val targetId = if (entityId.isNotBlank() && entityId != "all") entityId else (targetReviews.firstOrNull()?.entityId ?: "ooty_botanical_garden")
        val targetName = targetReviews.firstOrNull()?.entityName ?: if (entityId == "ooty_botanical_garden") "Ooty Botanical Garden" else "Ooty Tourism"
        val targetLocation = when {
            targetId.contains("munnar") -> "Munnar, Kerala"
            targetId.contains("goa") || targetId == "fort_aguada" -> "Goa, Goa"
            targetId.contains("kodaikanal") -> "Kodaikanal, Tamil Nadu"
            else -> "Ooty, Tamil Nadu"
        }

        ReviewSummary(
            entityId = targetId,
            entityName = targetName,
            entityLocation = targetLocation,
            averageRating = (Math.round(avg * 10) / 10.0f),
            totalReviews = count,
            fiveStar = targetReviews.count { it.rating >= 4.5f },
            fourStar = targetReviews.count { it.rating in 3.5f..4.4f },
            threeStar = targetReviews.count { it.rating in 2.5f..3.4f },
            twoStar = targetReviews.count { it.rating in 1.5f..2.4f },
            oneStar = targetReviews.count { it.rating < 1.5f },
            aspectRatings = mapOf("Scenery / View" to 4.7f, "Cleanliness" to 4.4f, "Service / Staff" to 4.3f, "Value for Money" to 4.5f),
            pros = listOf("Clean & peaceful green environment", "Great for family strolls & photography", "Well maintained glasshouse and flower beds"),
            cons = listOf("Gets crowded during weekend afternoons", "Limited parking near main gates")
        )
    }

    override suspend fun submitReview(review: Review): Review = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.reviewDao().insertReview(ReviewEntity.fromDomainModel(review))
            } catch (e: Exception) {
                // Ignore
            }
        }
        review
    }

    override suspend fun reportReview(reviewId: String, reason: String) {
        // Report handled
    }

    override suspend fun markHelpful(reviewId: String): Int = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                val reviewEntity = db.reviewDao().getReviewById(reviewId)
                if (reviewEntity != null) {
                    val updated = reviewEntity.copy(helpfulCount = reviewEntity.helpfulCount + 1)
                    db.reviewDao().insertReview(updated)
                    return@withContext updated.helpfulCount
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        25
    }

    override suspend fun getUserReviews(userId: String): List<Review> = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                val list = db.reviewDao().getUserReviewsList()
                if (list.isNotEmpty()) {
                    return@withContext list.map { it.toDomainModel() }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        getReviews("all").filter { it.userName.contains("You") }
    }

    override suspend fun deleteReview(reviewId: String): Unit = withContext(Dispatchers.IO) {
        database?.let { db ->
            try {
                db.reviewDao().deleteReview(reviewId)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
