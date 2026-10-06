package com.tripdirect.data.local.dao

import androidx.room.*
import com.tripdirect.data.local.entity.SavedPlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlaceDao {
    @Query("SELECT * FROM saved_places ORDER BY name ASC")
    fun getSavedPlacesFlow(): Flow<List<SavedPlaceEntity>>

    @Query("SELECT * FROM saved_places ORDER BY name ASC")
    suspend fun getSavedPlacesList(): List<SavedPlaceEntity>

    @Query("SELECT * FROM saved_places WHERE id = :id LIMIT 1")
    suspend fun getSavedPlaceById(id: String): SavedPlaceEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM saved_places WHERE id = :id)")
    fun isSavedFlow(id: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_places WHERE id = :id)")
    suspend fun isSaved(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPlace(place: SavedPlaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPlaces(places: List<SavedPlaceEntity>)

    @Update
    suspend fun updateSavedPlace(place: SavedPlaceEntity)

    @Query("DELETE FROM saved_places WHERE id = :id")
    suspend fun deleteSavedPlace(id: String)
}
