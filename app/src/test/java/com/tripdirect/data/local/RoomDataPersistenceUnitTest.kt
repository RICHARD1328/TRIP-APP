package com.tripdirect.data.local

import com.tripdirect.data.local.database.DatabaseConverters
import com.tripdirect.data.local.entity.EmergencyContactEntity
import com.tripdirect.data.local.entity.ItineraryEntity
import com.tripdirect.data.local.entity.ReviewEntity
import com.tripdirect.data.local.entity.SavedPlaceEntity
import com.tripdirect.data.local.entity.TripEntity
import com.tripdirect.data.model.EmergencyContact
import com.tripdirect.data.model.ItineraryDay
import com.tripdirect.data.model.ItineraryItem
import com.tripdirect.data.model.ItineraryItemType
import com.tripdirect.data.model.Review
import com.tripdirect.data.model.ReviewCategory
import com.tripdirect.data.model.SavedPlace
import com.tripdirect.data.model.UserTripSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomDataPersistenceUnitTest {

    private val converters = DatabaseConverters()

    @Test
    fun tripEntity_mappingToAndFromDomainModel_preservesData() {
        val domain = UserTripSummary(
            id = "t_101",
            title = "Ooty Summer Getaway",
            dates = "10 May - 14 May 2026",
            durationDays = 4,
            travelersCount = 3,
            estimatedBudget = 15000.0,
            status = "UPCOMING"
        )
        val entity = TripEntity.fromDomainModel(domain, imageUrl = "https://example.com/ooty.jpg")

        assertEquals(domain.id, entity.id)
        assertEquals(domain.title, entity.title)
        assertEquals(domain.dates, entity.dates)
        assertEquals(domain.durationDays, entity.durationDays)
        assertEquals(domain.travelersCount, entity.travelersCount)
        assertEquals(domain.estimatedBudget, entity.estimatedBudget, 0.001)
        assertEquals(domain.status, entity.status)
        assertEquals("https://example.com/ooty.jpg", entity.imageUrl)

        val restored = entity.toDomainModel()
        assertEquals(domain, restored)
    }

    @Test
    fun itineraryEntity_jsonConverters_correctlySerializesAndDeserializes() {
        val days = listOf(
            ItineraryDay(
                dayNumber = 1,
                date = "2026-08-12",
                title = "Arrival in Ooty",
                activities = listOf(
                    ItineraryItem(
                        id = "act_1",
                        startTime = "09:00 AM",
                        title = "Check in to resort",
                        description = "Freshen up",
                        location = "Club Mahindra Ooty",
                        type = ItineraryItemType.HOTEL
                    )
                ),
                totalDistanceKm = 12.5,
                totalTravelMinutes = 45,
                estimatedCost = 3500
            )
        )

        val json = converters.fromItineraryDaysList(days)
        assertNotNull(json)
        assertTrue(json.contains("Check in to resort"))

        val deserialized = converters.toItineraryDaysList(json)
        assertEquals(1, deserialized.size)
        assertEquals("Arrival in Ooty", deserialized[0].title)
        assertEquals(1, deserialized[0].activities.size)
        assertEquals("act_1", deserialized[0].activities[0].id)
    }

    @Test
    fun savedPlaceEntity_mappingToAndFromDomainModel_preservesData() {
        val place = SavedPlace(
            id = "sp_101",
            name = "Pykara Lake & Waterfalls",
            location = "Pykara, Ooty",
            rating = 4.7f,
            category = "Waterfalls",
            imageUrl = "https://example.com/pykara.jpg",
            destination = "Ooty"
        )
        val entity = SavedPlaceEntity.fromDomainModel(place)
        assertEquals(place.id, entity.id)
        assertEquals(place.name, entity.name)
        assertEquals(place.location, entity.location)
        assertEquals(place.rating, entity.rating, 0.01f)
        assertEquals(place.category, entity.category)
        assertEquals(place.destination, entity.destination)

        val restored = entity.toDomainModel()
        assertEquals(place, restored)
    }

    @Test
    fun reviewEntity_mappingToAndFromDomainModel_preservesData() {
        val review = Review(
            id = "rev_99",
            userName = "Richard R.",
            userAvatarUrl = null,
            rating = 5.0f,
            title = "Unforgettable Experience",
            comment = "The scenic Nilgiri toy train ride is a must-try!",
            date = "05 Oct 2026",
            category = ReviewCategory.ACTIVITY,
            entityId = "ooty_toy_train",
            entityName = "Nilgiri Mountain Railway",
            helpfulCount = 14,
            photos = emptyList(),
            verifiedVisit = true
        )
        val entity = ReviewEntity.fromDomainModel(review)
        assertEquals(review.id, entity.id)
        assertEquals(review.title, entity.title)
        assertEquals(review.entityId, entity.entityId)
        assertEquals("ACTIVITY", entity.categoryName)

        val restored = entity.toDomainModel()
        assertEquals(review.id, restored.id)
        assertEquals(review.title, restored.title)
        assertEquals(review.category, restored.category)
    }

    @Test
    fun emergencyContactEntity_mappingToAndFromDomainModel_preservesData() {
        val contact = EmergencyContact(
            id = "c_99",
            name = "Emergency Helpline",
            phoneNumber = "+91 94444 00000",
            relationship = "Local Support"
        )
        val entity = EmergencyContactEntity.fromDomainModel(contact)
        assertEquals(contact.id, entity.id)
        assertEquals(contact.name, entity.name)
        assertEquals(contact.phoneNumber, entity.phoneNumber)
        assertEquals(contact.relationship, entity.relationship)

        val restored = entity.toDomainModel()
        assertEquals(contact, restored)
    }

    @Test
    fun databaseConverters_stringList_serializesAndDeserializes() {
        val list = listOf("photo1.jpg", "photo2.jpg", "photo3.jpg")
        val json = converters.fromStringList(list)
        val deserialized = converters.toStringList(json)
        assertEquals(list, deserialized)

        assertEquals("[]", converters.fromStringList(null))
        assertTrue(converters.toStringList(null).isEmpty())
        assertTrue(converters.toStringList("").isEmpty())
    }
}
