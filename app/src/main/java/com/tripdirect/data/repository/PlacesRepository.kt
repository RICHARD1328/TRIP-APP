package com.tripdirect.data.repository

import com.tripdirect.BuildConfig
import com.tripdirect.data.model.AttractionImageProvider
import com.tripdirect.data.model.Place
import com.tripdirect.data.network.GooglePlacesApi
import com.tripdirect.data.network.GooglePlacesPhotoHelper
import com.tripdirect.data.network.TextSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface PlacesRepository {
    suspend fun getPlaces(): List<Place>
    suspend fun getPlaceById(id: String): Place?
    suspend fun searchPlaces(query: String): List<Place>
}

class MockPlacesRepository(
    private val destinationRepository: DestinationRepository = DestinationRepository()
) : PlacesRepository {

    private val api: GooglePlacesApi? by lazy {
        try {
            Retrofit.Builder()
                .baseUrl("https://places.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GooglePlacesApi::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private val placesList = listOf(
        // OOTY
        Place(
            id = "ooty_lake",
            name = "Ooty Lake",
            location = "Ooty, Tamil Nadu",
            description = "Ooty Lake is a scenic artificial lake constructed in 1824 by John Sullivan. Surrounded by groves of Eucalyptus trees, it offers boating facilities and a mini train.",
            category = "Nature",
            rating = 4.4,
            entryFee = 30,
            entryPrice = "₹30 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Ooty Lake"),
            openingTime = "8:00 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "October – June",
            estimatedVisitDuration = "2 hours",
            latitude = 11.4011,
            longitude = 76.6853,
            isFeatured = true
        ),
        Place(
            id = "doddabetta_peak",
            name = "Doddabetta Peak",
            location = "Ooty, Tamil Nadu",
            description = "Doddabetta Peak is the highest mountain in the Nilgiri Hills at 2,637 metres. It features a telescope house at the top offering panoramic 360-degree views of the valley.",
            category = "Adventure",
            rating = 4.6,
            entryFee = 10,
            entryPrice = "₹10 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Doddabetta Peak"),
            openingTime = "7:00 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "October – June",
            estimatedVisitDuration = "2 hours",
            latitude = 11.4038,
            longitude = 76.7358,
            isFeatured = true,
            isFavorite = true
        ),
        Place(
            id = "botanical_garden_ooty",
            name = "Government Botanical Garden",
            location = "Ooty, Tamil Nadu",
            description = "Established in 1848, this 55-hectare botanical garden features thousands of indigenous and exotic species, terraced flower beds, and a fossilized tree trunk.",
            category = "Nature",
            rating = 4.5,
            entryFee = 50,
            entryPrice = "₹50 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Government Botanical Garden"),
            openingTime = "7:00 AM",
            closingTime = "6:30 PM",
            bestTimeToVisit = "April – May",
            estimatedVisitDuration = "2–3 hours",
            isFeatured = false
        ),
        Place(
            id = "nilgiri_mountain_railway",
            name = "Nilgiri Mountain Railway",
            location = "Ooty, Tamil Nadu",
            description = "A UNESCO World Heritage steam railway built by the British in 1908, winding through dramatic tunnels, bridges, and tea estates from Mettupalayam to Ooty.",
            category = "Historical",
            rating = 4.8,
            entryFee = 200,
            entryPrice = "₹200 ticket",
            photoUrl = AttractionImageProvider.getAttractionImage("Nilgiri Mountain Railway"),
            openingTime = "7:10 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "All year",
            estimatedVisitDuration = "3 hours",
            isFeatured = true
        ),
        Place(
            id = "tea_museum_ooty",
            name = "Tea Factory & Tea Museum",
            location = "Ooty, Tamil Nadu",
            description = "Learn the fascinating history of Nilgiri tea, tour the full cut-tear-curl manufacturing process, and enjoy fresh cardamom hot tea with hill views.",
            category = "Historical",
            rating = 4.3,
            entryFee = 20,
            entryPrice = "₹20 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Tea Factory / Tea Museum"),
            openingTime = "9:00 AM",
            closingTime = "5:30 PM",
            bestTimeToVisit = "All year",
            estimatedVisitDuration = "1 hour",
            isFeatured = false
        ),

        // MUNNAR
        Place(
            id = "eravikulam_national_park",
            name = "Eravikulam National Park",
            location = "Munnar, Kerala",
            description = "Home to the endangered Nilgiri Tahr mountain goat and the famous Neelakurinji flower that blooms once every 12 years. Features mist-shrouded green hills.",
            category = "Nature",
            rating = 4.7,
            entryFee = 200,
            entryPrice = "₹200 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Eravikulam National Park"),
            openingTime = "7:30 AM",
            closingTime = "4:00 PM",
            bestTimeToVisit = "September – May",
            estimatedVisitDuration = "3 hours",
            isFeatured = true
        ),
        Place(
            id = "mattupetty_dam",
            name = "Mattupetty Dam",
            location = "Munnar, Kerala",
            description = "A storage concrete gravity dam surrounded by tea plantations and thick pine forests. Popular for speed boating and spotting wild elephants near the lake.",
            category = "Nature",
            rating = 4.4,
            entryFee = 10,
            entryPrice = "₹10 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Mattupetty Dam"),
            openingTime = "9:30 AM",
            closingTime = "5:00 PM",
            bestTimeToVisit = "September – March",
            estimatedVisitDuration = "2 hours",
            isFeatured = false
        ),
        Place(
            id = "top_station",
            name = "Top Station",
            location = "Munnar, Kerala",
            description = "The highest point on the Munnar-Kodaikanal road at 1,700m elevation. Offers breathtaking valley views overlooking the neighbouring state of Tamil Nadu.",
            category = "Adventure",
            rating = 4.6,
            entryFee = 20,
            entryPrice = "₹20 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Top Station"),
            openingTime = "6:00 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "October – March",
            estimatedVisitDuration = "2 hours",
            isFeatured = true
        ),
        Place(
            id = "munnar_tea_museum",
            name = "Munnar Tea Museum",
            location = "Munnar, Kerala",
            description = "KDHP's Tata Tea Museum showcases century-old machinery, vintage tea rollers, and tea tasting experiences detailing Munnar's plantation history.",
            category = "Historical",
            rating = 4.5,
            entryFee = 125,
            entryPrice = "₹125 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Munnar Tea Museum"),
            openingTime = "9:00 AM",
            closingTime = "5:00 PM",
            bestTimeToVisit = "All year",
            estimatedVisitDuration = "1.5 hours",
            isFeatured = false
        ),
        Place(
            id = "echo_point",
            name = "Echo Point",
            location = "Munnar, Kerala",
            description = "Confluence of three mountain streams located by a scenic lake where your shout echoes back through the misty valleys. Surrounded by green slope trails.",
            category = "Nature",
            rating = 4.3,
            entryFee = 10,
            entryPrice = "₹10 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Echo Point"),
            openingTime = "6:00 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "September – May",
            estimatedVisitDuration = "1 hour",
            isFeatured = false
        ),

        // GOA
        Place(
            id = "fort_aguada",
            name = "Fort Aguada",
            location = "Goa, Goa",
            description = "A 17th-century Portuguese fortress and lighthouse overlooking the Arabian Sea, built to prevent Dutch and Maratha ships from entering Goa.",
            category = "Historical",
            rating = 4.6,
            entryFee = 50,
            entryPrice = "₹50 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Fort Aguada"),
            openingTime = "8:30 AM",
            closingTime = "5:30 PM",
            bestTimeToVisit = "November – February",
            estimatedVisitDuration = "2 hours",
            isFeatured = true
        ),
        Place(
            id = "baga_beach",
            name = "Baga Beach",
            location = "Goa, Goa",
            description = "One of North Goa's most famous vibrant beaches, renowned for thrilling water sports, beach shacks, night markets, and golden sunsets.",
            category = "Beach",
            rating = 4.5,
            entryFee = 0,
            entryPrice = "Free entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Baga Beach"),
            openingTime = "Open 24 hrs",
            closingTime = "Open 24 hrs",
            bestTimeToVisit = "October – March",
            estimatedVisitDuration = "3 hours",
            isFeatured = true
        ),
        Place(
            id = "chapora_fort",
            name = "Chapora Fort",
            location = "Goa, Goa",
            description = "Famous cliffside red laterite fort offering sweeping sunset views over Chapora River and Vagator Beach. Popularized by Indian cinema.",
            category = "Historical",
            rating = 4.5,
            entryFee = 0,
            entryPrice = "Free entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Chapora Fort"),
            openingTime = "9:30 AM",
            closingTime = "5:30 PM",
            bestTimeToVisit = "November – March",
            estimatedVisitDuration = "1.5 hours",
            isFeatured = false
        ),
        Place(
            id = "dudhsagar_falls",
            name = "Dudhsagar Falls",
            location = "Goa, Goa",
            description = "Four-tiered waterfall on the Mandovi River, cascading like a sea of white milk down steep green cliffs in Bhagwan Mahaveer Sanctuary.",
            category = "Adventure",
            rating = 4.7,
            entryFee = 100,
            entryPrice = "₹100 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Dudhsagar Falls"),
            openingTime = "6:00 AM",
            closingTime = "5:00 PM",
            bestTimeToVisit = "October – April",
            estimatedVisitDuration = "Half Day",
            isFeatured = true
        ),

        // MANALI
        Place(
            id = "hadimba_temple",
            name = "Hadimba Devi Temple",
            location = "Manali, Himachal Pradesh",
            description = "Built in 1553, an ancient wooden pagoda-style cave temple dedicated to Hadimba Devi, set amidst dense deodar pine forests of Dhungri.",
            category = "Historical",
            rating = 4.6,
            entryFee = 0,
            entryPrice = "Free entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Hadimba Devi Temple"),
            openingTime = "8:00 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "All year",
            estimatedVisitDuration = "1.5 hours",
            isFeatured = true
        ),
        Place(
            id = "solang_valley",
            name = "Solang Valley",
            location = "Manali, Himachal Pradesh",
            description = "A side valley at the top of the Kullu Valley in Himachal Pradesh, world-famous for paragliding, zorbing, skiing, and snow sports.",
            category = "Adventure",
            rating = 4.7,
            entryFee = 0,
            entryPrice = "Free entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Solang Valley"),
            openingTime = "9:00 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "October – June",
            estimatedVisitDuration = "Half Day",
            isFeatured = true
        ),
        Place(
            id = "rohtang_pass",
            name = "Rohtang Pass",
            location = "Manali, Himachal Pradesh",
            description = "A high mountain pass at 3,978m elevation on the Pir Panjal Range. Offers snow-capped Himalayan glaciers and gateway to Lahaul & Spiti.",
            category = "Adventure",
            rating = 4.8,
            entryFee = 550,
            entryPrice = "₹550 permit",
            photoUrl = AttractionImageProvider.getAttractionImage("Rohtang Pass"),
            openingTime = "6:00 AM",
            closingTime = "4:00 PM",
            bestTimeToVisit = "May – October",
            estimatedVisitDuration = "Full Day",
            isFeatured = true
        ),
        Place(
            id = "manali_mall_road",
            name = "Manali Mall Road",
            location = "Manali, Himachal Pradesh",
            description = "The bustling heart of Manali lined with Himachali woolen shops, wooden handicrafts, cafes, local momos, and street food stalls.",
            category = "Shopping",
            rating = 4.5,
            entryFee = 0,
            entryPrice = "Free entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Manali Mall Road"),
            openingTime = "9:00 AM",
            closingTime = "10:00 PM",
            bestTimeToVisit = "All year",
            estimatedVisitDuration = "2–3 hours",
            isFeatured = false
        ),

        // COORG
        Place(
            id = "abbey_falls",
            name = "Abbey Falls",
            location = "Coorg, Karnataka",
            description = "Cascading waterfall amidst private coffee plantations and spice estates, viewed from a hanging bridge with roaring mist streams.",
            category = "Nature",
            rating = 4.5,
            entryFee = 15,
            entryPrice = "₹15 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Abbey Falls"),
            openingTime = "9:00 AM",
            closingTime = "5:00 PM",
            bestTimeToVisit = "September – January",
            estimatedVisitDuration = "1.5 hours",
            isFeatured = true
        ),
        Place(
            id = "rajas_seat",
            name = "Raja's Seat",
            location = "Coorg, Karnataka",
            description = "A scenic sunset viewpoint in Madikeri featuring manicured flower gardens and fountains, where kings of Coorg watched sunsets over green hills.",
            category = "Nature",
            rating = 4.5,
            entryFee = 10,
            entryPrice = "₹10 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Raja's Seat"),
            openingTime = "5:30 AM",
            closingTime = "8:00 PM",
            bestTimeToVisit = "October – March",
            estimatedVisitDuration = "1.5 hours",
            isFeatured = false
        ),
        Place(
            id = "dubare_elephant_camp",
            name = "Dubare Elephant Camp",
            location = "Coorg, Karnataka",
            description = "Elephant camp on the banks of River Kaveri where visitors can watch, bathe, and feed trained elephants under forest department guidance.",
            category = "Adventure",
            rating = 4.4,
            entryFee = 100,
            entryPrice = "₹100 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Dubare Elephant Camp"),
            openingTime = "9:00 AM",
            closingTime = "5:30 PM",
            bestTimeToVisit = "September – March",
            estimatedVisitDuration = "3 hours",
            isFeatured = true
        ),
        Place(
            id = "mandalpatti",
            name = "Mandalpatti",
            location = "Coorg, Karnataka",
            description = "Misty hilltop viewpoint reached via rugged 4x4 Jeep safari through Pushpagiri forests, offering dramatic views over endless rolling ridges.",
            category = "Adventure",
            rating = 4.7,
            entryFee = 25,
            entryPrice = "₹25 entry",
            photoUrl = AttractionImageProvider.getAttractionImage("Mandalpatti"),
            openingTime = "6:00 AM",
            closingTime = "6:00 PM",
            bestTimeToVisit = "November – June",
            estimatedVisitDuration = "3 hours",
            isFeatured = true
        )
    )

    override suspend fun getPlaces(): List<Place> {
        delay(100)
        return placesList
    }

    override suspend fun getPlaceById(id: String): Place? {
        if (id.isBlank()) return null
        val norm = id.trim().lowercase()
        val aliasId = when (norm) {
            "ooty_botanical_garden" -> "botanical_garden_ooty"
            "ooty_tea_museum" -> "tea_museum_ooty"
            "tea_museum_ooty" -> "ooty_tea_museum"
            "botanical_garden_ooty" -> "ooty_botanical_garden"
            else -> norm
        }
        return placesList.find { 
            it.id.equals(norm, ignoreCase = true) || 
            it.id.equals(aliasId, ignoreCase = true) ||
            it.name.lowercase().contains(norm) ||
            norm.contains(it.id.lowercase()) ||
            it.id.lowercase().contains(norm)
        } ?: destinationRepository.getDestinationById(id)?.let { dest ->
            Place(
                id = dest.id,
                name = dest.name,
                location = "${dest.name}, ${dest.state}",
                description = dest.description,
                category = dest.category,
                rating = dest.rating.toDouble(),
                entryFee = 0,
                entryPrice = "Free Entry",
                photoUrl = dest.image,
                openingTime = "Open 24 Hours",
                closingTime = "Open 24 Hours",
                bestTimeToVisit = "All Year",
                estimatedVisitDuration = "1 Day",
                latitude = dest.latitude,
                longitude = dest.longitude,
                isFeatured = true
            )
        } ?: fetchSinglePlaceFromGoogle(id)
    }

    override suspend fun searchPlaces(query: String): List<Place> {
        val lowerQuery = query.lowercase().trim()
        if (lowerQuery.isEmpty()) return placesList

        val localMatches = placesList.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.location.lowercase().contains(lowerQuery) ||
            it.category.lowercase().contains(lowerQuery)
        }

        if (localMatches.isNotEmpty()) {
            return localMatches
        }

        // Try Google Places API (New) for unknown search queries
        return fetchPlacesFromGoogle(query)
    }

    private suspend fun fetchSinglePlaceFromGoogle(query: String): Place? {
        return fetchPlacesFromGoogle(query).firstOrNull()
    }

    private suspend fun fetchPlacesFromGoogle(query: String): List<Place> {
        val apiKey = BuildConfig.PLACES_API_KEY.ifBlank { BuildConfig.MAPS_API_KEY }
        if (apiKey.isBlank() || api == null) return emptyList()

        return withContext(Dispatchers.IO) {
            try {
                val response = api!!.searchPlaces(
                    apiKey = apiKey,
                    fieldMask = "places.id,places.displayName,places.formattedAddress,places.rating,places.userRatingCount,places.photos,places.googleMapsUri",
                    request = TextSearchRequest(textQuery = query)
                )

                if (response.isSuccessful) {
                    val placesDto = response.body()?.places ?: emptyList()
                    return@withContext placesDto.mapIndexed { idx, dto ->
                        val photo = dto.photos?.firstOrNull()
                        val photoUrl = photo?.name?.let {
                            GooglePlacesPhotoHelper.buildPhotoUrl(it, apiKey, 1000)
                        } ?: AttractionImageProvider.getAttractionImage(dto.displayName?.text ?: query)
                        val attr = GooglePlacesPhotoHelper.formatAttribution(photo?.authorAttributions)

                        Place(
                            id = dto.id ?: "google_place_$idx",
                            name = dto.displayName?.text ?: query,
                            location = dto.formattedAddress ?: "India",
                            description = "Discovered via Google Places. Rated ${dto.rating ?: 4.5} ⭐ by ${dto.userRatingCount ?: 50}+ visitors.",
                            category = "Exploration",
                            rating = dto.rating ?: 4.5,
                            entryFee = 0,
                            entryPrice = "Check entry price",
                            placeId = dto.id,
                            photoUrl = photoUrl,
                            photoAttribution = attr,
                            isFeatured = true
                        )
                    }
                }
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
