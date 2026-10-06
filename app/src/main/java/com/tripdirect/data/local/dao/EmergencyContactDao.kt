package com.tripdirect.data.local.dao

import androidx.room.*
import com.tripdirect.data.local.entity.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY name ASC")
    fun getEmergencyContactsFlow(): Flow<List<EmergencyContactEntity>>

    @Query("SELECT * FROM emergency_contacts ORDER BY name ASC")
    suspend fun getEmergencyContactsList(): List<EmergencyContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyContact(contact: EmergencyContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyContacts(contacts: List<EmergencyContactEntity>)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteEmergencyContact(id: String)
}
