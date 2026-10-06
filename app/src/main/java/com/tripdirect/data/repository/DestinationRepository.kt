package com.tripdirect.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tripdirect.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.tripdirect.data.network.AiPlannerApi
import com.tripdirect.data.network.ApiClient

class DestinationRepository {

    private val api: AiPlannerApi
        get() = ApiClient.getAiPlannerApi()

    private val savedDestinationIds = MutableStateFlow<Set<String>>(setOf("ooty", "1"))
    val savedIds: StateFlow<Set<String>> = savedDestinationIds.asStateFlow()

    suspend fun getSocialContentForDestination(destinationId: String, destName: String): List<SocialContent> {
        return try {
            val resp = api.getDestinationDetail(destinationId)
            if (resp.isSuccessful && resp.body() != null) {
                val d = resp.body()!!
                val list = d.social_content.map { sDto ->
                    SocialContent(
                        id = sDto.id,
                        destinationId = sDto.destination_id,
                        platform = sDto.platform,
                        title = sDto.title,
                        reelUrl = sDto.reel_url,
                        thumbnailUrl = sDto.thumbnail_url,
                        creatorName = sDto.creator_name,
                        description = sDto.description,
                        verified = sDto.verified,
                        active = sDto.active
                    )
                }
                if (list.isNotEmpty()) return list
            }
            getFallbackSocialContent(destName)
        } catch (e: Exception) {
            getFallbackSocialContent(destName)
        }
    }

    private fun getFallbackSocialContent(destName: String): List<SocialContent> {
        val key = destName.lowercase()
        return when {
            "munnar" in key -> listOf(
                SocialContent(
                    id = "social_munnar_1",
                    destinationId = "munnar",
                    platform = "instagram",
                    title = "Munnar Tea Gardens & Misty Peaks",
                    reelUrl = "https://www.instagram.com/reel/C3MunnarReel1/",
                    thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Munnar_-_Tea_Plantations.jpg/1280px-Munnar_-_Tea_Plantations.jpg",
                    creatorName = "KeralaTravelVibes",
                    description = "Breathtaking aerial view of Munnar's rolling tea plantations and cloud-capped hills.",
                    verified = true,
                    active = true
                ),
                SocialContent(
                    id = "social_munnar_2",
                    destinationId = "munnar",
                    platform = "instagram",
                    title = "Top Station Sunset Point",
                    reelUrl = "https://www.instagram.com/reel/C3MunnarReel2/",
                    thumbnailUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
                    creatorName = "WanderlustIndia",
                    description = "Watching golden hour above the clouds at Top Station Munnar.",
                    verified = true,
                    active = true
                )
            )
            "ooty" in key -> listOf(
                SocialContent(
                    id = "social_ooty_1",
                    destinationId = "ooty",
                    platform = "instagram",
                    title = "Nilgiri Mountain Toy Train Ride",
                    reelUrl = "https://www.instagram.com/reel/C4OotyReel99/",
                    thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/5/55/Ooty_Emerald_Lake.jpg",
                    creatorName = "TamilNaduExplorers",
                    description = "Riding the iconic UNESCO heritage steam train from Coonoor to Ooty.",
                    verified = true,
                    active = true
                ),
                SocialContent(
                    id = "social_ooty_2",
                    destinationId = "ooty",
                    platform = "instagram",
                    title = "Ooty Lake Boating & Rose Garden",
                    reelUrl = "https://www.instagram.com/reel/C4OotyReel88/",
                    thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
                    creatorName = "SouthIndiaStories",
                    description = "Boating on serene Ooty Lake and exploring thousands of roses in bloom.",
                    verified = true,
                    active = true
                )
            )
            "coorg" in key -> listOf(
                SocialContent(
                    id = "social_coorg_1",
                    destinationId = "coorg",
                    platform = "instagram",
                    title = "Coorg Coffee Plantation Walk & Abbey Falls",
                    reelUrl = "https://www.instagram.com/reel/C5CoorgReel12/",
                    thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/ABBEY_FALLS_%2CCOORG_%2CKARNATAKA.jpg/1280px-ABBEY_FALLS_%2CCOORG_%2CKARNATAKA.jpg",
                    creatorName = "KarnatakaDiaries",
                    description = "Smelling fresh coffee beans and visiting roaring Abbey Falls in Madikeri.",
                    verified = true,
                    active = true
                )
            )
            "goa" in key -> listOf(
                SocialContent(
                    id = "social_goa_1",
                    destinationId = "goa",
                    platform = "instagram",
                    title = "Goa Golden Hour Sunset at Palolem Beach",
                    reelUrl = "https://www.instagram.com/reel/C6GoaReel77/",
                    thumbnailUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=800",
                    creatorName = "GoaTravelReels",
                    description = "Relaxing beach vibes, palm trees, and ocean sunsets in South Goa.",
                    verified = true,
                    active = true
                )
            )
            else -> emptyList()
        }
    }

    fun toggleSave(destinationId: String) {
        val current = savedDestinationIds.value.toMutableSet()
        if (current.contains(destinationId)) {
            current.remove(destinationId)
        } else {
            current.add(destinationId)
        }
        savedDestinationIds.value = current
    }

    /**
     * Loads destinations from local assets JSON file if context is provided.
     * Falls back to built-in local destinations dataset.
     */
    fun loadDestinationsFromAssets(context: Context): List<Destination> {
        return try {
            val jsonString = context.assets.open("destinations.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Destination>>() {}.type
            Gson().fromJson<List<Destination>>(jsonString, type) ?: destinationsList
        } catch (e: Exception) {
            destinationsList
        }
    }


    fun getAllDestinations(): List<Destination> = destinationsList

    fun getDestinationById(id: String?): Destination? {
        if (id.isNullOrBlank()) return null
        val normalized = id.trim().lowercase()

        // 1. Direct ID, Name or Official Name match
        destinationsList.firstOrNull { 
            it.id.equals(normalized, ignoreCase = true) || 
            it.name.equals(normalized, ignoreCase = true) ||
            (it.officialName != null && it.officialName.lowercase().contains(normalized))
        }?.let { return it }

        // 2. Numeric / Regional Aliases
        val aliasId = when (normalized) {
            "1" -> "ooty"
            "2" -> "munnar"
            "3" -> "kodaikanal"
            "4" -> "goa"
            "5" -> "jaipur"
            "6" -> "manali"
            "7" -> "coorg"
            "8" -> "alleppey"
            "9" -> "darjeeling"
            "10" -> "wayanad"
            "11" -> "pondicherry"
            "12" -> "agra"
            "13" -> "delhi"
            "14" -> "mumbai"
            "15" -> "chennai"
            "16" -> "hyderabad"
            "17" -> "bengaluru"
            "18" -> "kochi"
            "kerala" -> "munnar"
            else -> null
        }
        if (aliasId != null) {
            destinationsList.firstOrNull { it.id == aliasId }?.let { return it }
        }

        // 3. Partial substring match
        return destinationsList.firstOrNull { 
            it.name.lowercase().contains(normalized) || 
            normalized.contains(it.id.lowercase()) ||
            it.state.lowercase().contains(normalized)
        }
    }

    fun searchDestinations(query: String): List<Destination> {
        if (query.isBlank()) return destinationsList
        val lower = query.trim().lowercase()
        return destinationsList.filter { 
            it.name.lowercase().contains(lower) || 
            it.state.lowercase().contains(lower) || 
            it.region.lowercase().contains(lower) 
        }
    }

    private val destinationsList = listOf(
        // 1. OOTY
        Destination(
            id = "ooty",
            name = "Ooty",
            officialName = "Udhagamandalam / Ootacamund",
            tagline = "Queen of the Nilgiris",
            state = "Tamil Nadu",
            country = "India",
            region = "Nilgiris",
            rating = 4.5,
            category = "Hill Station",
            description = "Ooty is a scenic hill destination in the Nilgiris of Tamil Nadu, known for its cool climate, tea estates, gardens, lakes and mountain scenery.",
            estimatedDailyBudget = "₹2,000–₹4,000/day",
            weather = "18°C • Mild & Pleasant",
            latitude = 11.4102,
            longitude = 76.6950,
            googleSearchQuery = "Ooty Tamil Nadu India",
            fallbackImageUrl = "https://upload.wikimedia.org/wikipedia/commons/5/55/Ooty_Emerald_Lake.jpg",
            popularAttractions = listOf(
                AttractionInfo("att_ooty_1", "Ooty Lake", "Boating", 4.4, "Artificial lake built in 1824 offering paddle and motor boat rides.", "Check current price", "https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=600&auto=format&fit=crop", 11.4086, 76.6896),
                AttractionInfo("att_ooty_2", "Government Botanical Garden", "Nature", 4.5, "Lush 55-acre garden featuring rare trees, exotic plants and a fossilized tree trunk.", "Check current price", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?q=80&w=600&auto=format&fit=crop", 11.4162, 76.7118),
                AttractionInfo("att_ooty_3", "Doddabetta Peak", "Viewpoint", 4.6, "Highest peak in the Nilgiri Hills at 2,637 meters offering panoramic valley views.", "Check current price", "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=600&auto=format&fit=crop", 11.4011, 76.7360),
                AttractionInfo("att_ooty_4", "Rose Garden", "Flora", 4.5, "Landscaped garden boasting thousands of varieties of roses across terraced hillsides.", "Check current price", "https://images.unsplash.com/photo-1496062031456-07b8f162a322?q=80&w=600&auto=format&fit=crop", 11.4072, 76.7153),
                AttractionInfo("att_ooty_5", "Nilgiri Mountain Railway", "Heritage Train", 4.8, "UNESCO World Heritage toy train running through tunnels and wooden bridges.", "Check current price", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=600&auto=format&fit=crop", 11.4070, 76.7020),
                AttractionInfo("att_ooty_6", "Tea Factory and Museum", "Culture", 4.4, "Learn the step-by-step process of tea leaves processing with fresh tea tasting.", "Check current price", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?q=80&w=600&auto=format&fit=crop", 11.4230, 76.7280)
            ),
            hotels = listOf(
                HotelInfo("h1", "Savoy - IHCL SeleQtions", 4.6, "Prices vary — check current availability", "1.5 km from Town Center", listOf("Free Wi-Fi", "Heritage Bar", "Spa", "Garden View"), "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600&auto=format&fit=crop"),
                HotelInfo("h2", "Sterling Ooty Fern Hill", 4.4, "Prices vary — check current availability", "2.8 km from Ooty Lake", listOf("Mountain View", "Restaurant", "Games Room"), "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600&auto=format&fit=crop"),
                HotelInfo("h3", "Sinclairs Retreat Ooty", 4.3, "Prices vary — check current availability", "3.1 km from Botanical Garden", listOf("Valley View", "Free Parking", "Buffet"), "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600&auto=format&fit=crop")
            ),
            transportInfo = TransportInfo(
                railway = "Nearest broad gauge railway station is Mettupalayam (40 km). Connects via Nilgiri Mountain Railway toy train.",
                airport = "Coimbatore International Airport (CJB) is approximately 88 km away.",
                bus = "Frequent government and private buses run from Bengaluru, Mysuru, Coimbatore, and Chennai.",
                road = "Well connected via NH181. Picturesque drive through 36 hairpin bends via Kallatty route or Coonoor route."
            ),
            localTransport = listOf(
                LocalTransportOption("Auto Rickshaw", "Metered or negotiated rate. Typical short trips ₹100–₹250."),
                LocalTransportOption("Taxi / Cab", "Daily sightseeing cabs typical ₹1,800–₹3,000 per day."),
                LocalTransportOption("Rental Scooter", "Available in town from ₹500–₹800/day.")
            ),
            bestTime = listOf(
                BestTimeInfo("🌤 Pleasant Season", "October to March", "Cool and comfortable weather ideal for sightseeing."),
                BestTimeInfo("🌸 Summer Season", "April to June", "Popular flower show season and escape from summer heat."),
                BestTimeInfo("🌧 Monsoon Season", "July to September", "Heavy misty rainfall across tea hills; quiet season.")
            ),
            thingsToDo = listOf(
                ThingToDoInfo("🌿 Nature", "Tea Garden Walk", "Stroll through rolling green tea estates in Coonoor and Ooty."),
                ThingToDoInfo("🏞 Sightseeing", "Nilgiri Toy Train Ride", "Ride the historic steam-operated toy train through tunnels."),
                ThingToDoInfo("🍴 Food", "Handmade Chocolate Tasting", "Sample freshly made homemade dark and milk chocolates."),
                ThingToDoInfo("📸 Photography", "Doddabetta Sunset", "Capture the golden hour over misty Blue Mountains.")
            ),
            localFood = listOf(
                LocalFoodInfo("Ooty Homemade Chocolates", "Rich artisanal chocolates flavored with nuts, rum, and berries."),
                LocalFoodInfo("Nilgiri Fresh Tea", "Fragrant high-altitude hill tea direct from local tea factories."),
                LocalFoodInfo("Varkey (Ooty Bakery Snack)", "Crispy, flaky traditional hill baked pastry snack.")
            ),
            safetyInformation = "Ooty is generally safe for tourists. Exercise caution on winding hill roads, especially in foggy condition. Keep warm clothing handy during night hours.",
            reviews = listOf(
                ReviewInfo("Anita S.", 4.5, "The view from Doddabetta was incredible! The weather in November was crisp and fresh.", "2 weeks ago"),
                ReviewInfo("Rahul M.", 5.0, "Riding the Nilgiri toy train was a highlight for our family. Highly recommend booking in advance.", "1 month ago")
            )
        ),

        // 2. MUNNAR
        Destination(
            id = "munnar",
            name = "Munnar",
            officialName = "Munnar Hill Station",
            tagline = "Green Heaven of Western Ghats",
            state = "Kerala",
            country = "India",
            region = "Idukki",
            rating = 4.7,
            category = "Hill Station",
            description = "Munnar is a tranquil hill town in Idukki district of Kerala, celebrated for its sprawling tea plantations, misty valleys, waterfalls, and rare Nilgiri Tahr wildlife.",
            estimatedDailyBudget = "₹2,500–₹4,500/day",
            weather = "19°C • Cool & Misty",
            latitude = 10.0889,
            longitude = 77.0595,
            googleSearchQuery = "Munnar Kerala India",
            fallbackImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Munnar_-_Tea_Plantations.jpg/1280px-Munnar_-_Tea_Plantations.jpg",
            popularAttractions = listOf(
                AttractionInfo("att_munnar_1", "Eravikulam National Park", "Wildlife & Nature", 4.7, "Home to endangered Nilgiri Tahr wild goats and blooming Neelakurinji flowers.", "Check current price", "https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_munnar_2", "Mattupetty Dam", "Scenic Waterway", 4.5, "Masonry dam nestled amidst tea gardens with speed boat rides and elephant sightings.", "Check current price", "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_munnar_3", "Tea Museum (KDHP)", "Heritage", 4.6, "Historical tea museum depicting the growth of tea plantations in Munnar.", "Check current price", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_munnar_4", "Top Station", "Viewpoint", 4.8, "Panoramic mountain view on the Kerala-Tamil Nadu border looking down on Theni valley.", "Check current price", "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?q=80&w=600&auto=format&fit=crop")
            ),
            hotels = listOf(
                HotelInfo("hm1", "Blanket Hotel & Spa", 4.8, "Prices vary — check current availability", "Attukad Waterfall view", listOf("Spa", "Infinity Pool", "Waterfall View"), "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600&auto=format&fit=crop"),
                HotelInfo("hm2", "Tea County Munnar", 4.5, "Prices vary — check current availability", "Munnar Town center", listOf("Free Wi-Fi", "Ayurvedic Massage", "Garden"), "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600&auto=format&fit=crop")
            ),
            transportInfo = TransportInfo(
                railway = "Nearest railway stations are Ernakulam (120 km) and Aluva (110 km).",
                airport = "Cochin International Airport (COK) is 110 km away (approx. 3.5 hour scenic hill drive).",
                bus = "KSRTC buses connect Munnar directly from Kochi, Trivandrum, Madurai and Bengaluru.",
                road = "Scenic mountain highways (NH85) with tea garden vistas on all approaches."
            ),
            localTransport = listOf(
                LocalTransportOption("Auto", "Short city rides ₹80–₹200."),
                LocalTransportOption("Cab / Jeep", "Full day Munnar sightseeing cab ₹2,200–₹3,500.")
            ),
            bestTime = listOf(
                BestTimeInfo("🌤 Peak Season", "September to May", "Chilly morning mist and clear sunny afternoons.")
            ),
            thingsToDo = listOf(
                ThingToDoInfo("🌿 Nature", "Tea Plantation Trekking", "Walk along winding tea slopes guided by local naturalists."),
                ThingToDoInfo("🍴 Food", "Kerala Sadya Meal", "Traditional Kerala banana leaf feast with fresh cardamom tea.")
            ),
            localFood = listOf(
                LocalFoodInfo("Appam with Stew", "Fluffy rice pancakes served with aromatic coconut milk vegetable/chicken stew."),
                LocalFoodInfo("Kerala Spiced Cardamom Tea", "Freshly brewed tea infused with local green cardamom.")
            ),
            safetyInformation = "Drive carefully during monsoon landslides risk. Wildlife warnings apply near national park boundaries.",
            reviews = listOf(
                ReviewInfo("David P.", 5.0, "Stunning green hill views! The tea museum and Eravikulam park are must visits.", "3 weeks ago")
            )
        ),

        // 3. KODAIKANAL
        Destination(
            id = "kodaikanal",
            name = "Kodaikanal",
            officialName = "Kodaikanal Hill Station",
            tagline = "Princess of Hill Stations",
            state = "Tamil Nadu",
            country = "India",
            region = "Dindigul",
            rating = 4.4,
            category = "Hill Station",
            description = "Kodaikanal is a serene hill station centered around a star-shaped lake, thick pine forests, cascading waterfalls and granite rock formations.",
            estimatedDailyBudget = "₹1,800–₹3,500/day",
            weather = "17°C • Cool Breeze",
            latitude = 10.2381,
            longitude = 77.4892,
            googleSearchQuery = "Kodaikanal Tamil Nadu India",
            fallbackImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Kodaikanal_Lake_2.jpg/1280px-Kodaikanal_Lake_2.jpg",
            popularAttractions = listOf(
                AttractionInfo("att_kodai_1", "Kodaikanal Lake", "Boating", 4.5, "Star-shaped man-made lake located right at the heart of town.", "Check current price", "https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_kodai_2", "Coaker's Walk", "Walkway", 4.6, "1-km mountain pedestrian path with valley views and haze effects.", "Check current price", "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_kodai_3", "Pine Forest", "Nature Trail", 4.7, "Dense towering pine forest preserved since 1906, popular film shooting location.", "Check current price", "https://images.unsplash.com/photo-1448375240586-882707db888b?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_kodai_4", "Pillar Rocks", "Viewpoint", 4.4, "Three vertical granite rock pillars standing 122 meters high.", "Check current price", "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?q=80&w=600&auto=format&fit=crop")
            ),
            hotels = listOf(
                HotelInfo("hk1", "The Carlton Kodaikanal", 4.7, "Prices vary — check current availability", "Overlooking Kodai Lake", listOf("Lakefront", "5 Star", "Boating Access"), "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600&auto=format&fit=crop")
            ),
            transportInfo = TransportInfo(
                railway = "Nearest station is Kodai Road (80 km).",
                airport = "Madurai Airport (IXM) is 120 km away.",
                bus = "Direct buses from Chennai, Bengaluru, Madurai and Coimbatore.",
                road = "Scenic mountain ghat road from Batlagundu or Palani."
            ),
            localTransport = listOf(
                LocalTransportOption("Bicycle rental", "Popular around Kodai lake loop line for ₹100/hr."),
                LocalTransportOption("Cab", "Local sightseeing tourist taxi package ₹1,800/day.")
            ),
            bestTime = listOf(BestTimeInfo("🌤 Best Season", "September to May", "Pleasant weather with clear lake reflections.")),
            thingsToDo = listOf(
                ThingToDoInfo("🌿 Nature", "Cycling around Lake", "Rent a bicycle and ride around the 5 km lake circuit.")
            ),
            localFood = listOf(LocalFoodInfo("Kodai Cheese & Butter", "Handcrafted artisanal hill cheese produced by local dairy farms.")),
            safetyInformation = "Watch step around steep cliff viewpoints. Beware of monkeys around public parks.",
            reviews = listOf(ReviewInfo("Priya K.", 4.5, "The pine forest walk felt surreal in early morning fog!", "1 month ago"))
        ),

        // 4. GOA
        Destination(
            id = "goa",
            name = "Goa",
            officialName = "State of Goa",
            tagline = "Sun, Sand & Portuguese Heritage",
            state = "Goa",
            country = "India",
            region = "Konkan Coast",
            rating = 4.8,
            category = "Beach & Coastline",
            description = "Goa is India's coastal paradise, famous for pristine sandy beaches, Portuguese architecture, vibrant nightlife, spice plantations, and seafood cuisine.",
            estimatedDailyBudget = "₹3,000–₹7,000/day",
            weather = "28°C • Warm & Tropical",
            latitude = 15.2993,
            longitude = 74.1240,
            googleSearchQuery = "Goa India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?q=80&w=1200&auto=format&fit=crop",
            popularAttractions = listOf(
                AttractionInfo("att_goa_1", "Baga Beach", "Beach & Water Sports", 4.7, "Vibrant beach famous for water sports, beach shacks, and night parties.", "Check current price", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_goa_2", "Fort Aguada", "Heritage Monument", 4.6, "17th-century Portuguese fort and lighthouse with views over Sinquerim beach.", "Check current price", "https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_goa_3", "Basilica of Bom Jesus", "UNESCO Site", 4.8, "World Heritage Church housing the sacred relics of St. Francis Xavier.", "Check current price", "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_goa_4", "Dudhsagar Waterfalls", "Nature Adventure", 4.7, "Four-tiered waterfall on the Mandovi river resembling milk pouring down cliffs.", "Check current price", "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=600&auto=format&fit=crop")
            ),
            hotels = listOf(
                HotelInfo("hg1", "Taj Exotica Resort & Spa Goa", 4.8, "Prices vary — check current availability", "Benaulim Beach Front", listOf("Private Beach", "Pool", "Spa"), "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600&auto=format&fit=crop")
            ),
            transportInfo = TransportInfo(
                railway = "Major railway stations are Madgaon (MAO) and Thivim (THVM).",
                airport = "Dabolim Airport (GOI) and Manohar International Airport Mopa (GOX).",
                bus = "KSRTC, Kadamba and private sleeper buses run from Mumbai, Pune, Bengaluru.",
                road = "Coastal highway scenic routes (NH66)."
            ),
            localTransport = listOf(
                LocalTransportOption("Scooter / Bike Rental", "Most popular choice, ₹350–₹600 per day."),
                LocalTransportOption("Goa Miles Taxi", "App-based and prepaid taxi counters at airport/stations.")
            ),
            bestTime = listOf(BestTimeInfo("☀️ Peak Beach Season", "November to February", "Clear blue skies, dry weather and sea festivities.")),
            thingsToDo = listOf(
                ThingToDoInfo("🏄 Adventure", "Water Sports", "Try parasailing, jet skiing, and banana boat rides at Calangute."),
                ThingToDoInfo("🍴 Food", "Sunset Beach Shack Dining", "Enjoy fresh grilled red snapper with local Goan feni.")
            ),
            localFood = listOf(
                LocalFoodInfo("Goan Fish Curry Rice", "Tangy and spicy coconut curry dish infused with Kokum and fresh catch."),
                LocalFoodInfo("Bebinca", "Traditional multi-layered Goan coconut milk dessert cake.")
            ),
            safetyInformation = "Follow beach safety flags and lifesaver guidelines before swimming in ocean currents.",
            reviews = listOf(ReviewInfo("Carlos R.", 5.0, "Unmatched sunset views at Anjuna and historic charm in Panjim Fontainhas!", "2 weeks ago"))
        ),

        // 5. JAIPUR
        Destination(
            id = "jaipur",
            name = "Jaipur",
            officialName = "Pink City",
            tagline = "The Royal Pink City",
            state = "Rajasthan",
            country = "India",
            region = "Marwar",
            rating = 4.6,
            category = "Heritage Architecture",
            description = "Jaipur is the royal capital of Rajasthan, famous for its terracotta-pink fortresses, grand palaces like Hawa Mahal and Amber Fort, vibrant bazaars, and rich Rajput legacy.",
            estimatedDailyBudget = "₹2,500–₹5,500/day",
            weather = "26°C • Sunny",
            latitude = 26.9124,
            longitude = 75.7873,
            googleSearchQuery = "Jaipur Rajasthan India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1477587458883-47145ed94245?q=80&w=1200&auto=format&fit=crop",
            popularAttractions = listOf(
                AttractionInfo("att_jaipur_1", "Hawa Mahal", "Palace of Winds", 4.7, "Iconic five-story pink sandstone palace with 953 intricate honeycomb windows.", "Check current price", "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_jaipur_2", "Amber Fort", "Hill Fortress", 4.8, "Majestic hilltop fort featuring Sheesh Mahal mirror palace overlooking Maota Lake.", "Check current price", "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_jaipur_3", "City Palace", "Royal Residence", 4.6, "Grand palace complex combining Rajput and Mughal court architecture.", "Check current price", "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600&auto=format&fit=crop"),
                AttractionInfo("att_jaipur_4", "Jantar Mantar", "UNESCO Observatory", 4.7, "Historic astronomical observatory featuring the world's largest stone sundial.", "Check current price", "https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=600&auto=format&fit=crop")
            ),
            hotels = listOf(
                HotelInfo("hj1", "Rambagh Palace Jaipur", 4.9, "Prices vary — check current availability", "Former Royal Residence", listOf("5 Star Heritage", "Pool", "Royal Gardens"), "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600&auto=format&fit=crop")
            ),
            transportInfo = TransportInfo(
                railway = "Jaipur Junction (JP) connects directly to Delhi, Mumbai, Kolkata, Ahmedabad.",
                airport = "Jaipur International Airport (JAI) located in Sanganer (13 km).",
                bus = "RSRTC buses run frequently from Delhi ISBT Kashmere Gate.",
                road = "Delhi-Jaipur Expressway (NH48) approx 4-5 hours."
            ),
            localTransport = listOf(
                LocalTransportOption("E-Rickshaw / Auto", "Convenient for narrow Pink City market streets."),
                LocalTransportOption("Jaipur Metro", "Connects Mansarovar to Badi Chaupar near Hawa Mahal.")
            ),
            bestTime = listOf(BestTimeInfo("🏰 Royal Season", "October to March", "Pleasant weather ideal for exploring forts without summer heat.")),
            thingsToDo = listOf(
                ThingToDoInfo("🛍 Shopping", "Bapu Bazaar Handicrafts", "Shop for traditional Jaipuri quilts, Mojari shoes, and silver jewelry.")
            ),
            localFood = listOf(
                LocalFoodInfo("Dal Baati Churma", "Authentic Rajasthani baked wheat ball served with lentils and pure ghee churma."),
                LocalFoodInfo("Pyaaz Kachori", "Crispy deep-fried pastry stuffed with spicy onion filling.")
            ),
            safetyInformation = "Keep belongings safe in crowded bazaar corridors. Confirm auto rates before starting rides.",
            reviews = listOf(ReviewInfo("Vikram S.", 5.0, "Amber fort light show at dusk is unforgettable!", "2 weeks ago"))
        ),

        // 6. MANALI
        Destination(
            id = "manali",
            name = "Manali",
            officialName = "Manali Himachal",
            tagline = "Valley of the Gods",
            state = "Himachal Pradesh",
            country = "India",
            region = "Kullu Valley",
            rating = 4.6,
            category = "Mountains & Snow",
            description = "Manali is a high-altitude Himalayan resort town known for snow sports in Solang Valley, Rohtang Pass views, old cedar wood temples, and pine valley treks.",
            estimatedDailyBudget = "₹2,500–₹5,000/day",
            weather = "12°C • Cool & Refreshing",
            latitude = 32.2432,
            longitude = 77.1892,
            googleSearchQuery = "Manali Himachal Pradesh India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?q=80&w=1200&auto=format&fit=crop",
            popularAttractions = listOf(
                AttractionInfo("att_manali_1", "Hadimba Devi Temple", "Historical", 4.6, "16th-century wooden pagoda cave temple in dense pine forests.", "Free entry", "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800&q=80"),
                AttractionInfo("att_manali_2", "Solang Valley", "Adventure", 4.7, "World-famous adventure valley for paragliding, zorbing, and winter skiing.", "Free entry", "https://images.unsplash.com/photo-1605649487212-47bdab064df7?w=800&q=80"),
                AttractionInfo("att_manali_3", "Rohtang Pass", "Adventure", 4.8, "High mountain pass at 3,978m elevation on Pir Panjal Range.", "Check current price", "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=800&q=80")
            ),
            transportInfo = TransportInfo("Chandigarh (310 km)", "Kullu Bhuntar Airport (50 km)", "Frequent Volvo buses from Delhi & Chandigarh", "NH21 scenic Beas river drive"),
            safetyInformation = "Check Rohtang pass permits in advance during peak snow season."
        ),

        // 7. COORG
        Destination(
            id = "coorg",
            name = "Coorg",
            officialName = "Kodagu",
            tagline = "Scotland of India",
            state = "Karnataka",
            country = "India",
            region = "Western Ghats",
            rating = 4.4,
            category = "Coffee Hills",
            description = "Coorg is famous for lush coffee plantations, misty green hills, Abbey Falls, elephant camps, and authentic Kodava culture.",
            estimatedDailyBudget = "₹2,000–₹4,000/day",
            weather = "20°C • Mild",
            latitude = 12.4244,
            longitude = 75.7382,
            googleSearchQuery = "Coorg Karnataka India",
            fallbackImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/ABBEY_FALLS_%2CCOORG_%2CKARNATAKA.jpg/1280px-ABBEY_FALLS_%2CCOORG_%2CKARNATAKA.jpg",
            popularAttractions = listOf(
                AttractionInfo("att_coorg_1", "Abbey Falls", "Nature", 4.5, "Cascading waterfall amidst coffee plantations viewed from hanging bridge.", "Check current price", "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=800&q=80"),
                AttractionInfo("att_coorg_2", "Raja's Seat", "Nature", 4.5, "Sunset viewpoint in Madikeri with manicured flower gardens.", "Check current price", "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80"),
                AttractionInfo("att_coorg_3", "Dubare Elephant Camp", "Adventure", 4.4, "Elephant camp on Kaveri riverbank offering bathing and feeding experiences.", "Check current price", "https://images.unsplash.com/photo-1557050543-4d5f4e07ef46?w=800&q=80")
            ),
            transportInfo = TransportInfo("Mysuru (120 km)", "Kannur Airport (90 km) / Mysuru (120 km)", "Buses from Bengaluru & Mysuru", "SH88 highway drive"),
            safetyInformation = "Drive cautiously on forest curves, especially during heavy monsoon rains."
        ),

        // 8. ALLEPPEY
        Destination(
            id = "alleppey",
            name = "Alleppey",
            officialName = "Alappuzha",
            tagline = "Venice of the East",
            state = "Kerala",
            country = "India",
            region = "Backwaters",
            rating = 4.7,
            category = "Backwaters",
            description = "Alleppey is famous for serene palm-fringed backwater canals, traditional houseboat cruises, Punnamada Lake, and paddy field vistas.",
            estimatedDailyBudget = "₹2,500–₹6,000/day",
            weather = "27°C • Humid Tropical",
            latitude = 9.4981,
            longitude = 76.3388,
            googleSearchQuery = "Alleppey Kerala India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo("Alappuzha Railway Station (ALLP)", "Cochin International Airport (85 km)", "Direct buses from Kochi and Trivandrum", "AC Canal roads"),
            safetyInformation = "Only hire licensed houseboat operators with valid safety jackets."
        ),

        // 9. DARJEELING
        Destination(
            id = "darjeeling",
            name = "Darjeeling",
            officialName = "Queen of the Hills",
            tagline = "Queen of the Hills",
            state = "West Bengal",
            country = "India",
            region = "Himalayas",
            rating = 4.5,
            category = "Hill Station",
            description = "Darjeeling is world-renowned for its aromatic tea gardens, views of Kanchenjunga peak, Toy Train heritage railway, and vibrant Buddhist monasteries.",
            estimatedDailyBudget = "₹2,000–₹4,500/day",
            weather = "14°C • Cool",
            latitude = 27.0410,
            longitude = 88.2663,
            googleSearchQuery = "Darjeeling West Bengal India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo("New Jalpaiguri (NJP 70 km)", "Bagdogra Airport (IXB 75 km)", "Shared Jeeps and buses from Siliguri", "Hill Cart Road"),
            safetyInformation = "Early morning trip to Tiger Hill requires warm layers."
        ),

        // 10. WAYANAD
        Destination(
            id = "wayanad",
            name = "Wayanad",
            officialName = "Green Paradise",
            tagline = "Green Paradise of Kerala",
            state = "Kerala",
            country = "India",
            region = "Western Ghats",
            rating = 4.5,
            category = "Nature & Wildlife",
            description = "Wayanad features prehistoric Edakkal Caves, spice plantations, Banasura Sagar Dam, and wild elephant sanctuaries.",
            estimatedDailyBudget = "₹2,000–₹4,000/day",
            weather = "21°C • Refreshing",
            latitude = 11.6854,
            longitude = 76.1320,
            googleSearchQuery = "Wayanad Kerala India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1593693397690-362cb9666fc2?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo("Kozhikode (110 km)", "Calicut International Airport (100 km)", "Buses from Kozhikode, Mysuru & Bengaluru", "Thamarassery Ghat Pass"),
            safetyInformation = "Avoid night driving through forest corridors."
        ),

        // 11. PONDICHERRY
        Destination(
            id = "pondicherry",
            name = "Pondicherry",
            officialName = "Puducherry",
            tagline = "French Riviera of the East",
            state = "Puducherry",
            country = "India",
            region = "Coromandel Coast",
            rating = 4.5,
            category = "Coastal & Heritage",
            description = "Pondicherry is famous for mustard-yellow French Quarter villas, promenade beach walks, Sri Aurobindo Ashram, and experimental city Auroville.",
            estimatedDailyBudget = "₹2,000–₹4,500/day",
            weather = "28°C • Coastal Breeze",
            latitude = 11.9416,
            longitude = 79.8083,
            googleSearchQuery = "Pondicherry Puducherry India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "Puducherry Station (PDY)",
                airport = "Chennai Airport (135 km)",
                bus = "Frequent buses from Chennai ECR",
                road = "East Coast Road drive"
            ),
            safetyInformation = "Rent bicycles or scooters to easily navigate White Town lanes."
        ),

        // 12. AGRA
        Destination(
            id = "agra",
            name = "Agra",
            officialName = "City of Taj",
            tagline = "City of the Taj Mahal",
            state = "Uttar Pradesh",
            country = "India",
            region = "Braj",
            rating = 4.8,
            category = "World Wonder Heritage",
            description = "Agra is home to the world-famous Taj Mahal, Agra Fort, and Fatehpur Sikri, standing as testament to Mughal architectural magnificence.",
            estimatedDailyBudget = "₹2,000–₹5,000/day",
            weather = "25°C • Sunny",
            latitude = 27.1767,
            longitude = 78.0081,
            googleSearchQuery = "Agra Uttar Pradesh India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "Agra Cantt (AGC)",
                airport = "Delhi IGI Airport (200 km)",
                bus = "Superfast Express trains & Yamuna Expressway buses",
                road = "Yamuna Expressway (2.5 hrs from Delhi)"
            ),
            safetyInformation = "Purchase Taj Mahal tickets online to skip monument queues."
        ),

        // 13. DELHI
        Destination(
            id = "delhi",
            name = "Delhi",
            officialName = "National Capital Territory of Delhi",
            tagline = "Heart of India",
            state = "Delhi",
            country = "India",
            region = "North India",
            rating = 4.6,
            category = "Metropolis & Heritage",
            description = "Delhi seamlessly blends millennia of history with modern sprawl, featuring the Red Fort, Qutub Minar, India Gate, and iconic street food bazaars.",
            estimatedDailyBudget = "₹2,500–₹6,000/day",
            weather = "24°C • Pleasant",
            latitude = 28.6139,
            longitude = 77.2090,
            googleSearchQuery = "Delhi India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "New Delhi (NDLS), Old Delhi (DLI), Hazrat Nizamuddin",
                airport = "Indira Gandhi International Airport (DEL)",
                bus = "Interstate Bus Terminals (ISBT)",
                road = "Delhi Metro world-class network"
            ),
            safetyInformation = "Use Delhi Metro for fast transit across city hubs."
        ),

        // 14. MUMBAI
        Destination(
            id = "mumbai",
            name = "Mumbai",
            officialName = "Bombay",
            tagline = "City of Dreams",
            state = "Maharashtra",
            country = "India",
            region = "Konkan Coast",
            rating = 4.7,
            category = "Metropolis & Seafront",
            description = "Mumbai is India's financial and entertainment capital, famous for Gateway of India, Marine Drive Queen's Necklace, Bollywood studios, and street food.",
            estimatedDailyBudget = "₹3,000–₹8,000/day",
            weather = "29°C • Tropical",
            latitude = 19.0760,
            longitude = 72.8777,
            googleSearchQuery = "Mumbai Maharashtra India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1567157577867-05ccb1388e66?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "CSMT & Mumbai Central",
                airport = "Chhatrapati Shivaji Maharaj Airport (BOM)",
                bus = "MSRTC & Volvo buses",
                road = "Mumbai Suburban Rail & Coastal Road"
            ),
            safetyInformation = "Plan local train commutes outside peak rush hours."
        ),

        // 15. CHENNAI
        Destination(
            id = "chennai",
            name = "Chennai",
            officialName = "Madras",
            tagline = "Cultural Gateway of South India",
            state = "Tamil Nadu",
            country = "India",
            region = "Coromandel Coast",
            rating = 4.4,
            category = "Culture & Coast",
            description = "Chennai is known for Marina Beach, ancient Dravidian temples in Mylapore, classical Carnatic music, and South Indian filter coffee.",
            estimatedDailyBudget = "₹2,000–₹5,000/day",
            weather = "30°C • Warm",
            latitude = 13.0827,
            longitude = 80.2707,
            googleSearchQuery = "Chennai Tamil Nadu India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "Chennai Central (MAS) & Egmore",
                airport = "Chennai International Airport (MAA)",
                bus = "CMBT Bus Station Koyambedu",
                road = "Metro & Suburban Train"
            ),
            safetyInformation = "Stay hydrated when visiting coastal temples during daytime."
        ),

        // 16. HYDERABAD
        Destination(
            id = "hyderabad",
            name = "Hyderabad",
            officialName = "City of Pearls",
            tagline = "City of Pearls & Biryani",
            state = "Telangana",
            country = "India",
            region = "Deccan",
            rating = 4.6,
            category = "Heritage & IT Hub",
            description = "Hyderabad is famous for Charminar, Golconda Fort, world-renowned Hyderabadi Dum Biryani, pearl markets, and HITEC City technology center.",
            estimatedDailyBudget = "₹2,000–₹5,000/day",
            weather = "27°C • Clear",
            latitude = 17.3850,
            longitude = 78.4867,
            googleSearchQuery = "Hyderabad Telangana India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1572445271230-a78b5944a659?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "Secunderabad (SC) & Hyderabad Deccan (HYB)",
                airport = "Rajiv Gandhi International Airport (HYD)",
                bus = "TSRTC buses",
                road = "Hyderabad Metro Rail"
            ),
            safetyInformation = "Explore Old City bazaars near Charminar with a local guide."
        ),

        // 17. BENGALURU
        Destination(
            id = "bengaluru",
            name = "Bengaluru",
            officialName = "Bangalore",
            tagline = "Garden City & Silicon Valley of India",
            state = "Karnataka",
            country = "India",
            region = "Deccan Plateau",
            rating = 4.5,
            category = "Metropolis & Parks",
            description = "Bengaluru is famed for pleasant climate, Cubbon Park, Lalbagh Botanical Garden, historic palaces, craft breweries, and tech innovation.",
            estimatedDailyBudget = "₹2,500–₹6,000/day",
            weather = "23°C • Moderate & Pleasant",
            latitude = 12.9716,
            longitude = 77.5946,
            googleSearchQuery = "Bengaluru Karnataka India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "KSR Bengaluru (SBC) & Yesvantpur",
                airport = "Kempegowda International Airport (BLR)",
                bus = "BMTC Vayu Vajra airport buses",
                road = "Namma Metro network"
            ),
            safetyInformation = "Account for peak hour traffic when heading to the airport."
        ),

        // 18. KOCHI
        Destination(
            id = "kochi",
            name = "Kochi",
            officialName = "Cochin",
            tagline = "Queen of the Arabian Sea",
            state = "Kerala",
            country = "India",
            region = "Malabar Coast",
            rating = 4.6,
            category = "Coastal Port & Heritage",
            description = "Kochi is a historic port city celebrated for Chinese fishing nets, Fort Kochi heritage, Mattancherry Spice Market, and Water Metro boats.",
            estimatedDailyBudget = "₹2,000–₹5,000/day",
            weather = "28°C • Coastal Breeze",
            latitude = 9.9312,
            longitude = 76.2673,
            googleSearchQuery = "Kochi Kerala India",
            fallbackImageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?q=80&w=1200&auto=format&fit=crop",
            transportInfo = TransportInfo(
                railway = "Ernakulam Junction (ERS) & Town (ERN)",
                airport = "Cochin International Airport (COK)",
                bus = "KSRTC buses",
                road = "Kochi Metro & Kochi Water Metro"
            ),
            safetyInformation = "Take the scenic Water Metro boat ride connecting islands."
        )
    )
}
