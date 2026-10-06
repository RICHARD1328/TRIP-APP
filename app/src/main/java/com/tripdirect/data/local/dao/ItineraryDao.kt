package com.tripdirect.data.local.dao

import androidx.room.*
import com.tripdirect.data.local.entity.ItineraryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itineraries WHERE tripId = :tripId OR id = :tripId LIMIT 1")
    fun getItineraryFlow(tripId: String): Flow<ItineraryEntity?>

    @Query("SELECT * FROM itineraries WHERE tripId = :tripId OR id = :tripId LIMIT 1")
    suspend fun getItineraryById(tripId: String): ItineraryEntity?

    @Query("SELECT * FROM itineraries ORDER BY id DESC")
    fun getAllItineraries(): Flow<List<ItineraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItinerary(itinerary: ItineraryEntity)

    @Update
    suspend fun updateItinerary(itinerary: ItineraryEntity)

    @Query("DELETE FROM itineraries WHERE id = :id OR tripId = :id")
    suspend fun deleteItinerary(id: String)
}
