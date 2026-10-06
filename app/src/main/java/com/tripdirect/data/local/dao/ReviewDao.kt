package com.tripdirect.data.local.dao

import androidx.room.*
import com.tripdirect.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE entityId = :entityId OR :entityId = 'all' OR :entityId = '' ORDER BY id DESC")
    fun getReviewsForEntityFlow(entityId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE entityId = :entityId OR :entityId = 'all' OR :entityId = '' ORDER BY id DESC")
    suspend fun getReviewsForEntityList(entityId: String): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE id = :id LIMIT 1")
    suspend fun getReviewById(id: String): ReviewEntity?

    @Query("SELECT * FROM reviews WHERE userName LIKE '%You%' ORDER BY id DESC")
    suspend fun getUserReviewsList(): List<ReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: String)
}
