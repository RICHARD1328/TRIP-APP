package com.tripdirect.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import com.tripdirect.data.model.Attraction
import com.tripdirect.data.model.AttractionImageProvider
import com.tripdirect.data.model.Destination
import com.tripdirect.data.model.Hotel
import com.tripdirect.data.model.Trip
import com.tripdirect.data.model.User
import kotlinx.coroutines.delay

interface HomeRepository {
    suspend fun getCurrentUser(): Result<User?>
    suspend fun getPopularDestinations(): Result<List<Destination>>
    suspend fun getPopularAttractions(): Result<List<Attraction>>
    suspend fun getRecommendedHotels(): Result<List<Hotel>>
    suspend fun getActiveTrip(): Result<Trip?>
    suspend fun search(query: String): Result<SearchResults>
}

data class SearchResults(
    val destinations: List<Destination>,
    val hotels: List<Hotel>,
    val attractions: List<Attraction>
)

class MockHomeRepository(
    private val destinationRepository: DestinationRepository = DestinationRepository()
) : HomeRepository {

    private val popularAttractionsList = listOf(
        Attraction(
            id = "ooty_lake",
            name = "Ooty Lake",
            destination = "Ooty",
            state = "Tamil Nadu",
            category = "Boating & Nature",
            rating = 4.4,
            entryFee = 30,
            entryPrice = "₹30 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Ooty Lake"),
            description = "Artificial lake built by John Sullivan in 1824 with eucalyptus groves and pedal boating."
        ),
        Attraction(
            id = "doddabetta_peak",
            name = "Doddabetta Peak",
            destination = "Ooty",
            state = "Tamil Nadu",
            category = "Adventure & Viewpoint",
            rating = 4.6,
            entryFee = 10,
            entryPrice = "₹10 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Doddabetta Peak"),
            description = "Highest mountain in the Nilgiri Hills at 2,637 metres with 360° telescope view."
        ),
        Attraction(
            id = "botanical_garden_ooty",
            name = "Government Botanical Garden",
            destination = "Ooty",
            state = "Tamil Nadu",
            category = "Botanical Garden",
            rating = 4.5,
            entryFee = 50,
            entryPrice = "₹50 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Government Botanical Garden"),
            description = "55-hectare terraced gardens laid out in 1848 with rare plants and fossil trees."
        ),
        Attraction(
            id = "eravikulam_national_park",
            name = "Eravikulam National Park",
            destination = "Munnar",
            state = "Kerala",
            category = "Wildlife & Nature",
            rating = 4.7,
            entryFee = 200,
            entryPrice = "₹200 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Eravikulam National Park"),
            description = "High altitude sanctuary home to the Nilgiri Tahr goat and Neelakurinji blooms."
        ),
        Attraction(
            id = "fort_aguada",
            name = "Fort Aguada",
            destination = "Goa",
            state = "Goa",
            category = "Historical Fortress",
            rating = 4.6,
            entryFee = 50,
            entryPrice = "₹50 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Fort Aguada"),
            description = "17th-century Portuguese fortress overlooking Arabian Sea with ancient lighthouse."
        ),
        Attraction(
            id = "solang_valley",
            name = "Solang Valley",
            destination = "Manali",
            state = "Himachal Pradesh",
            category = "Snow & Paragliding",
            rating = 4.7,
            entryFee = 0,
            entryPrice = "Free entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Solang Valley"),
            description = "Premier adventure valley for paragliding, zorbing, skiing and snow sports."
        ),
        Attraction(
            id = "abbey_falls",
            name = "Abbey Falls",
            destination = "Coorg",
            state = "Karnataka",
            category = "Waterfall",
            rating = 4.5,
            entryFee = 15,
            entryPrice = "₹15 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Abbey Falls"),
            description = "Cascading waterfall nestled in private coffee and spice plantations."
        )
    )

    private val mockHotels = listOf(
        Hotel(
            id = "1", name = "Savoy IHCL SeleQtions", destination = "Ooty", location = "Ooty", 
            description = "A heritage stay.", rating = 4.6, reviewCount = 120, pricePerNight = 4500, 
            imageRes = null, vectorIcon = Icons.Default.Hotel, distanceFromCenter = 1.2, 
            facilities = listOf("Free Wi-Fi", "Breakfast"), roomType = "Double", 
            cancellationPolicy = "Free cancellation", breakfastIncluded = true, isFavorite = false
        )
    )

    override suspend fun getCurrentUser(): Result<User?> {
        delay(100)
        return Result.success(User("1", "Richard", "demo@tripdirect.com"))
    }

    override suspend fun getPopularDestinations(): Result<List<Destination>> {
        delay(100)
        return Result.success(destinationRepository.getAllDestinations().take(8))
    }

    override suspend fun getPopularAttractions(): Result<List<Attraction>> {
        delay(100)
        return Result.success(popularAttractionsList)
    }

    override suspend fun getRecommendedHotels(): Result<List<Hotel>> {
        delay(100)
        return Result.success(mockHotels.filter { it.rating >= 4.5 })
    }

    override suspend fun getActiveTrip(): Result<Trip?> {
        delay(100)
        return Result.success(
            Trip("t1", "Ooty", 3, 2, 10000, 70)
        )
    }

    override suspend fun search(query: String): Result<SearchResults> {
        delay(100)
        val lowerQuery = query.lowercase().trim()
        return Result.success(
            SearchResults(
                destinations = destinationRepository.searchDestinations(query),
                hotels = mockHotels.filter { it.name.lowercase().contains(lowerQuery) },
                attractions = popularAttractionsList.filter {
                    it.name.lowercase().contains(lowerQuery) ||
                    it.destination.lowercase().contains(lowerQuery)
                }
            )
        )
    }
}
