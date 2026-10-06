package com.tripdirect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tripdirect.data.model.EmergencyContact

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String
) {
    fun toDomainModel(): EmergencyContact {
        return EmergencyContact(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            relationship = relationship
        )
    }

    companion object {
        fun fromDomainModel(model: EmergencyContact): EmergencyContactEntity {
            return EmergencyContactEntity(
                id = model.id,
                name = model.name,
                phoneNumber = model.phoneNumber,
                relationship = model.relationship
            )
        }
    }
}
