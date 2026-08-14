package com.tripdirect.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import com.tripdirect.data.model.Attraction
import com.tripdirect.data.model.AttractionImageProvider
import com.tripdirect.data.model.Destination
import com.tripdirect.data.model.Hotel
import kotlinx.coroutines.delay

interface SearchRepository {
    suspend fun searchDestinations(query: String): List<Destination>
    suspend fun searchAttractions(query: String): List<Attraction>
    suspend fun searchHotels(query: String): List<Hotel>
}

class MockSearchRepository(
    private val destinationRepository: DestinationRepository = DestinationRepository()
) : SearchRepository {

    private val mockAttractions = listOf(
        Attraction("ooty_lake", "Ooty Lake", "Ooty", "Tamil Nadu", "Nature", 4.4, "₹30 entry", 30, photoUrl = AttractionImageProvider.getAttractionImage("Ooty Lake")),
        Attraction("doddabetta_peak", "Doddabetta Peak", "Ooty", "Tamil Nadu", "Adventure", 4.6, "₹10 entry", 10, photoUrl = AttractionImageProvider.getAttractionImage("Doddabetta Peak")),
        Attraction("botanical_garden_ooty", "Government Botanical Garden", "Ooty", "Tamil Nadu", "Nature", 4.5, "₹50 entry", 50, photoUrl = AttractionImageProvider.getAttractionImage("Government Botanical Garden")),
        Attraction("tea_museum_ooty", "Tea Factory & Tea Museum", "Ooty", "Tamil Nadu", "Historical", 4.3, "₹20 entry", 20, photoUrl = AttractionImageProvider.getAttractionImage("Tea Museum")),
        Attraction("eravikulam_national_park", "Eravikulam National Park", "Munnar", "Kerala", "Nature", 4.7, "₹200 entry", 200, photoUrl = AttractionImageProvider.getAttractionImage("Eravikulam National Park")),
        Attraction("fort_aguada", "Fort Aguada", "Goa", "Goa", "Historical", 4.6, "₹50 entry", 50, photoUrl = AttractionImageProvider.getAttractionImage("Fort Aguada")),
        Attraction("hadimba_temple", "Hadimba Devi Temple", "Manali", "Himachal Pradesh", "Historical", 4.6, "Free entry", 0, photoUrl = AttractionImageProvider.getAttractionImage("Hadimba Devi Temple")),
        Attraction("abbey_falls", "Abbey Falls", "Coorg", "Karnataka", "Nature", 4.5, "₹15 entry", 15, photoUrl = AttractionImageProvider.getAttractionImage("Abbey Falls"))
    )

    private val mockHotels = listOf(
        Hotel(
            id = "1", name = "Mountain View Hotel", destination = "Ooty", location = "Ooty", 
            description = "A comfortable stay.", rating = 4.3, reviewCount = 120, pricePerNight = 1500, 
            imageRes = null, vectorIcon = Icons.Default.Hotel, distanceFromCenter = 1.2, 
            facilities = listOf("Free Wi-Fi", "Breakfast"), roomType = "Double", 
            cancellationPolicy = "Free cancellation", breakfastIncluded = true, isFavorite = false
        )
    )

    override suspend fun searchDestinations(query: String): List<Destination> {
        delay(100)
        return destinationRepository.searchDestinations(query)
    }

    override suspend fun searchAttractions(query: String): List<Attraction> {
        delay(100)
        val lowerQuery = query.lowercase().trim()
        if (lowerQuery.isEmpty()) return emptyList()
        return mockAttractions.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.destination.lowercase().contains(lowerQuery) ||
            it.location.lowercase().contains(lowerQuery)
        }
    }

    override suspend fun searchHotels(query: String): List<Hotel> {
        delay(100)
        val lowerQuery = query.lowercase().trim()
        if (lowerQuery.isEmpty()) return emptyList()
        return mockHotels.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.location.lowercase().contains(lowerQuery)
        }
    }
}
