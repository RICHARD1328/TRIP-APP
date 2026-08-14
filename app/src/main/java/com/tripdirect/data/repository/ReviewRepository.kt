package com.tripdirect.data.repository

import com.tripdirect.data.model.Review
import com.tripdirect.data.model.ReviewCategory
import com.tripdirect.data.model.ReviewSummary
import kotlinx.coroutines.delay

interface ReviewRepository {
    suspend fun getReviews(entityId: String): List<Review>
    suspend fun getReviewSummary(entityId: String): ReviewSummary
    suspend fun submitReview(review: Review): Review
    suspend fun reportReview(reviewId: String, reason: String)
    suspend fun markHelpful(reviewId: String): Int
    suspend fun getUserReviews(userId: String): List<Review>
    suspend fun deleteReview(reviewId: String)
}

class MockReviewRepository : ReviewRepository {

    private val reviewsList = mutableListOf(
        Review(
            id = "rev_1",
            userName = "Arun Kumar",
            rating = 5.0f,
            title = "Great experience for families!",
            comment = "The garden was extremely well maintained and clean. Wonderful variety of exotic plants and a peaceful glasshouse. Highly recommended for families visiting Ooty.",
            date = "10 Aug 2026",
            category = ReviewCategory.ATTRACTION,
            entityId = "ooty_botanical_garden",
            entityName = "Ooty Botanical Garden",
            helpfulCount = 24,
            verifiedVisit = true,
            categoryRatings = mapOf("Scenery" to 5.0f, "Cleanliness" to 4.8f, "Crowd" to 4.0f, "Value" to 4.5f)
        ),
        Review(
            id = "rev_2",
            userName = "Priya Sharma",
            rating = 4.0f,
            title = "Beautiful location but crowded in afternoons",
            comment = "Very peaceful environment if you visit early morning around 8:30 AM. After 1:00 PM it gets quite crowded near the main lawn.",
            date = "08 Aug 2026",
            category = ReviewCategory.ATTRACTION,
            entityId = "ooty_botanical_garden",
            entityName = "Ooty Botanical Garden",
            helpfulCount = 18,
            verifiedVisit = true,
            categoryRatings = mapOf("Scenery" to 4.5f, "Cleanliness" to 4.2f, "Crowd" to 3.2f, "Value" to 4.0f)
        ),
        Review(
            id = "rev_3",
            userName = "Rahul Verma",
            rating = 5.0f,
            title = "Excellent stay at Ooty Comfort Residency",
            comment = "Staff was welcoming and room service was prompt. Close to commercial road and market. Clean linen and spacious rooms.",
            date = "05 Aug 2026",
            category = ReviewCategory.HOTEL,
            entityId = "ooty_hotel_1",
            entityName = "Ooty Comfort Residency",
            helpfulCount = 31,
            verifiedVisit = true,
            categoryRatings = mapOf("Cleanliness" to 4.8f, "Service" to 4.7f, "Location" to 4.5f, "Value" to 4.6f)
        ),
        Review(
            id = "rev_4",
            userName = "Kavitha R.",
            rating = 5.0f,
            title = "Authentic Nilgiri Thali & Local Flavor",
            comment = "Nilgiri Spice Restaurant served fresh hot food. The local mushroom curry and tea were highlights of our evening.",
            date = "02 Aug 2026",
            category = ReviewCategory.RESTAURANT,
            entityId = "ooty_rest_1",
            entityName = "Nilgiri Spice Restaurant",
            helpfulCount = 15,
            verifiedVisit = true,
            categoryRatings = mapOf("Taste" to 4.9f, "Service" to 4.3f, "Cleanliness" to 4.5f, "Value" to 4.7f)
        ),
        Review(
            id = "rev_5",
            userName = "Suresh Menon",
            rating = 4.0f,
            title = "Punctual bus ride from Chennai",
            comment = "Bus left on time. Seats were comfortable with charging ports. Driver was safe and polite throughout the ghat road curve drive.",
            date = "28 Jul 2026",
            category = ReviewCategory.TRANSPORT,
            entityId = "ooty_trans_1",
            entityName = "Chennai - Ooty Express Bus",
            helpfulCount = 12,
            verifiedVisit = true,
            categoryRatings = mapOf("Punctuality" to 4.5f, "Comfort" to 4.0f, "Safety" to 4.7f, "Value" to 4.2f)
        ),
        Review(
            id = "rev_6",
            userName = "Deepak Patel",
            rating = 3.0f,
            title = "Nice lake but long queue for boating",
            comment = "Boating experience at Ooty Lake was good, but we had to wait in line for almost 45 minutes on a weekend.",
            date = "25 Jul 2026",
            category = ReviewCategory.ATTRACTION,
            entityId = "ooty_lake",
            entityName = "Ooty Lake",
            helpfulCount = 9,
            verifiedVisit = false
        ),
        Review(
            id = "rev_7",
            userName = "Anita Roy",
            rating = 2.0f,
            title = "Parking was difficult during peak hours",
            comment = "Finding a parking spot near Doddabetta Peak took more than 30 minutes. Better to take a local taxi instead of driving self.",
            date = "20 Jul 2026",
            category = ReviewCategory.DESTINATION,
            entityId = "doddabetta_peak",
            entityName = "Doddabetta Peak",
            helpfulCount = 22,
            verifiedVisit = true
        ),
        Review(
            id = "rev_8",
            userName = "Vikram Singh",
            rating = 1.0f,
            title = "Overpriced vendor stalls near entrance",
            comment = "The entrance vendors charge double the price for water bottles and snacks. Carry your own provisions.",
            date = "15 Jul 2026",
            category = ReviewCategory.ACTIVITY,
            entityId = "ooty_tea_museum",
            entityName = "Tea Museum",
            helpfulCount = 7,
            verifiedVisit = false
        )
    )

    override suspend fun getReviews(entityId: String): List<Review> {
        delay(300)
        return if (entityId.isEmpty() || entityId == "all") {
            reviewsList
        } else {
            reviewsList.filter { it.entityId == entityId }
        }
    }

    override suspend fun getReviewSummary(entityId: String): ReviewSummary {
        delay(200)
        val targetReviews = if (entityId.isEmpty() || entityId == "all") reviewsList else reviewsList.filter { it.entityId == entityId }
        val count = targetReviews.size
        val avg = if (count > 0) targetReviews.map { it.rating }.average().toFloat() else 4.4f

        return ReviewSummary(
            entityName = if (entityId == "ooty_botanical_garden") "Ooty Botanical Garden" else "Ooty Tourism",
            entityLocation = "Ooty, Tamil Nadu",
            averageRating = (Math.round(avg * 10) / 10.0f),
            totalReviews = count,
            fiveStar = targetReviews.count { it.rating >= 4.5f },
            fourStar = targetReviews.count { it.rating in 3.5f..4.4f },
            threeStar = targetReviews.count { it.rating in 2.5f..3.4f },
            twoStar = targetReviews.count { it.rating in 1.5f..2.4f },
            oneStar = targetReviews.count { it.rating < 1.5f },
            aspectRatings = mapOf(
                "Scenery / View" to 4.7f,
                "Cleanliness" to 4.4f,
                "Service / Staff" to 4.3f,
                "Value for Money" to 4.5f
            ),
            pros = listOf(
                "Clean & peaceful green environment",
                "Great for family strolls & photography",
                "Well maintained glasshouse and flower beds"
            ),
            cons = listOf(
                "Gets crowded during weekend afternoons",
                "Limited parking near main gates"
            )
        )
    }

    override suspend fun submitReview(review: Review): Review {
        delay(300)
        reviewsList.add(0, review)
        return review
    }

    override suspend fun reportReview(reviewId: String, reason: String) {
        delay(200)
    }

    override suspend fun markHelpful(reviewId: String): Int {
        delay(150)
        val idx = reviewsList.indexOfFirst { it.id == reviewId }
        if (idx != -1) {
            val updated = reviewsList[idx].copy(helpfulCount = reviewsList[idx].helpfulCount + 1)
            reviewsList[idx] = updated
            return updated.helpfulCount
        }
        return 0
    }

    override suspend fun getUserReviews(userId: String): List<Review> {
        delay(200)
        return reviewsList.take(3)
    }

    override suspend fun deleteReview(reviewId: String) {
        delay(150)
        reviewsList.removeAll { it.id == reviewId }
    }
}
