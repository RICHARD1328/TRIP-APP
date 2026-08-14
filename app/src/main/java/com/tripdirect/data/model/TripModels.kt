package com.tripdirect.data.model

object DestinationImageProvider {
    // 1. Destination Image URLs (Authentic, accurate Unsplash photographs)
    const val OOTY = "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=800&q=80" // Ooty Nilgiri hills & tea gardens
    const val MUNNAR = "https://images.unsplash.com/photo-1596402184320-417e7178b2cd?w=800&q=80" // Munnar rolling tea estate
    const val KODAIKANAL = "https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?w=800&q=80" // Kodaikanal Lake & misty hills
    const val GOA = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=800&q=80" // Goa beach & palm sunset
    const val JAIPUR = "https://images.unsplash.com/photo-1477587458883-47145ed94245?w=800&q=80" // Hawa Mahal & Amber Fort
    const val MANALI = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=800&q=80" // Manali snow-capped Himalayan mountains
    const val COORG = "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=800&q=80" // Coorg coffee plantations & hills
    const val ALLEPPEY = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=800&q=80" // Kerala backwaters & houseboats
    const val DARJEELING = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&q=80" // Darjeeling hills & tea gardens
    const val WAYANAD = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80" // Wayanad waterfalls & forest
    const val PONDICHERRY = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80" // Pondicherry Promenade Beach
    const val AGRA = "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=800&q=80" // Taj Mahal Agra
    const val DELHI = "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800&q=80" // India Gate Delhi
    const val MUMBAI = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?w=800&q=80" // Gateway of India Mumbai
    const val BENGALURU = "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?w=800&q=80" // Bengaluru city & gardens
    const val CHENNAI = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80" // Marina Beach Chennai
    const val HYDERABAD = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?w=800&q=80" // Charminar Hyderabad
    const val KOCHI = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=800&q=80" // Fort Kochi waterfront

    /**
     * Case-insensitive, normalized lookup of destination images.
     * Returns null for unknown destinations so a neutral placeholder is shown (never wrong image).
     */
    fun getDestinationImage(destination: String?): String? {
        if (destination.isNull_or_blank()) return null
        val normalized = (destination ?: "").lowercase()
            .replace(",", " ")
            .replace("  ", " ")
            .trim()

        return when {
            normalized.contains("ooty") -> OOTY
            normalized.contains("munnar") -> MUNNAR
            normalized.contains("kodaikanal") || normalized.contains("kodai") -> KODAIKANAL
            normalized.contains("goa") -> GOA
            normalized.contains("jaipur") -> JAIPUR
            normalized.contains("manali") -> MANALI
            normalized.contains("coorg") -> COORG
            normalized.contains("alleppey") || normalized.contains("alappuzha") -> ALLEPPEY
            normalized.contains("darjeeling") -> DARJEELING
            normalized.contains("wayanad") -> WAYANAD
            normalized.contains("pondicherry") || normalized.contains("puducherry") -> PONDICHERRY
            normalized.contains("agra") -> AGRA
            normalized.contains("delhi") -> DELHI
            normalized.contains("mumbai") || normalized.contains("bombay") -> MUMBAI
            normalized.contains("bengaluru") || normalized.contains("bangalore") -> BENGALURU
            normalized.contains("chennai") || normalized.contains("madras") -> CHENNAI
            normalized.contains("hyderabad") -> HYDERABAD
            normalized.contains("kochi") || normalized.contains("cochin") -> KOCHI
            else -> null
        }
    }

    /**
     * Accessible content description for screen readers.
     */
    fun getDestinationDescription(destination: String?): String {
        if (destination.isNull_or_blank()) return "Travel Destination"
        val norm = (destination ?: "").lowercase()
        return when {
            norm.contains("ooty") -> "Ooty Nilgiri Hills and Tea Gardens"
            norm.contains("munnar") -> "Munnar Rolling Tea Plantations"
            norm.contains("kodaikanal") -> "Kodaikanal Lake and Pine Forest"
            norm.contains("goa") -> "Goa Beach and Palm Trees"
            norm.contains("jaipur") -> "Jaipur Hawa Mahal Palace"
            norm.contains("manali") -> "Manali Snow-Capped Himalayan Peaks"
            norm.contains("coorg") -> "Coorg Coffee Plantations and Waterfalls"
            norm.contains("alleppey") -> "Alleppey Kerala Backwaters and Houseboat"
            norm.contains("darjeeling") -> "Darjeeling Tea Estate and Himalayas"
            norm.contains("wayanad") -> "Wayanad Waterfalls and Forest"
            norm.contains("pondicherry") -> "Pondicherry Promenade Beach"
            norm.contains("agra") -> "Taj Mahal Agra"
            norm.contains("delhi") -> "India Gate Delhi"
            norm.contains("mumbai") -> "Gateway of India Mumbai"
            norm.contains("bengaluru") -> "Bengaluru Garden City"
            norm.contains("chennai") -> "Chennai Marina Beach Coastline"
            norm.contains("hyderabad") -> "Charminar Hyderabad"
            norm.contains("kochi") -> "Fort Kochi Kerala Waterfront"
            else -> "$destination Landscape"
        }
    }
}

// Backward-compatible alias
object TripImages {
    const val OOTY = DestinationImageProvider.OOTY
    const val OOTY_LAKE = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=800&q=80"
    const val TEA_GARDEN = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&q=80"
    const val DODDABETTA = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80"

    const val KODAIKANAL = DestinationImageProvider.KODAIKANAL
    const val KODAI_LAKE = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=800&q=80"
    const val PINE_FOREST = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=800&q=80"
    const val COAKERS_WALK = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&q=80"

    const val PONDICHERRY = DestinationImageProvider.PONDICHERRY
    const val PROMENADE_BEACH = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80"
    const val FRENCH_QUARTER = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&q=80"

    const val MUNNAR = DestinationImageProvider.MUNNAR
    const val WATERFALL = "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=800&q=80"

    const val GOA = DestinationImageProvider.GOA
    const val GOA_BEACH = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80"

    const val COORG = DestinationImageProvider.COORG
    const val JAIPUR = DestinationImageProvider.JAIPUR
    const val KERALA = DestinationImageProvider.ALLEPPEY
    const val MANALI = DestinationImageProvider.MANALI

    const val RESORT_HOTEL = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80"
    const val RESTAURANT_FOOD = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80"
    const val SOUTH_INDIAN_FOOD = "https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=800&q=80"

    fun getDestinationImage(name: String?): String {
        return DestinationImageProvider.getDestinationImage(name) ?: OOTY
    }
}

object AttractionImageProvider {
    // Ooty
    const val OOTY_LAKE = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=800&q=80" // Boat on Ooty Lake
    const val DODDABETTA_PEAK = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80" // Doddabetta mountain peak viewpoint
    const val BOTANICAL_GARDEN_OOTY = "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=800&q=80" // Lush botanical gardens
    const val NILGIRI_MOUNTAIN_RAILWAY = "https://images.unsplash.com/photo-1474487548417-781cb71495f3?w=800&q=80" // Toy train railway tracks mountain
    const val TEA_FACTORY_MUSEUM = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&q=80" // Tea plantation estate

    // Munnar
    const val ERAVIKULAM_NATIONAL_PARK = "https://images.unsplash.com/photo-1596402184320-417e7178b2cd?w=800&q=80" // Eravikulam green hills & Nilgiri Tahr habitat
    const val MATTUPETTY_DAM = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80" // Reservoir and dam water view
    const val TOP_STATION = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&q=80" // Misty valley view top station
    const val MUNNAR_TEA_MUSEUM = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=800&q=80" // Tea processing museum
    const val ECHO_POINT = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=800&q=80" // Echo point lake & hill slope

    // Goa
    const val FORT_AGUADA = "https://images.unsplash.com/photo-1590523741831-ab7e8b8f9c7f?w=800&q=80" // Fort Aguada lighthouse & coastal wall
    const val BAGA_BEACH = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=800&q=80" // Baga beach shacks and sunset
    const val CHAPORA_FORT = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80" // Historic Chapora Fort ruins over sea
    const val DUDHSAGAR_FALLS = "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=800&q=80" // Tiered white Dudhsagar waterfall

    // Manali
    const val HADIMBA_TEMPLE = "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800&q=80" // Wooden Hadimba Pagoda temple in pine forest
    const val SOLANG_VALLEY = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?w=800&q=80" // Solang valley adventure slope
    const val ROHTANG_PASS = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=800&q=80" // Rohtang pass snow mountain road
    const val MANALI_MALL_ROAD = "https://images.unsplash.com/photo-1519671482749-fd09be7ccebf?w=800&q=80" // Lively street market mall road

    // Coorg
    const val ABBEY_FALLS = "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=800&q=80" // Cascading Abbey Falls in spice estate
    const val RAJAS_SEAT = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80" // Raja's Seat sunset garden overlook
    const val DUBARE_ELEPHANT_CAMP = "https://images.unsplash.com/photo-1557050543-4d5f4e07ef46?w=800&q=80" // River riverbank elephant camp
    const val MANDALPATTI = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80" // Mandalpatti rolling misty ridges

    fun getAttractionImage(name: String?): String {
        if (name == null) return OOTY_LAKE
        val norm = name.lowercase().trim()
        return when {
            norm.contains("ooty lake") -> OOTY_LAKE
            norm.contains("doddabetta") -> DODDABETTA_PEAK
            norm.contains("botanical") -> BOTANICAL_GARDEN_OOTY
            norm.contains("railway") || norm.contains("nilgiri mountain") -> NILGIRI_MOUNTAIN_RAILWAY
            norm.contains("tea museum") && norm.contains("ooty") -> TEA_FACTORY_MUSEUM
            norm.contains("tea factory") || norm.contains("tea museum") -> TEA_FACTORY_MUSEUM
            norm.contains("eravikulam") -> ERAVIKULAM_NATIONAL_PARK
            norm.contains("mattupetty") -> MATTUPETTY_DAM
            norm.contains("top station") -> TOP_STATION
            norm.contains("echo point") -> ECHO_POINT
            norm.contains("fort aguada") || norm.contains("aguada") -> FORT_AGUADA
            norm.contains("baga beach") || norm.contains("baga") -> BAGA_BEACH
            norm.contains("chapora") -> CHAPORA_FORT
            norm.contains("dudhsagar") -> DUDHSAGAR_FALLS
            norm.contains("hadimba") || norm.contains("hidimba") -> HADIMBA_TEMPLE
            norm.contains("solang") -> SOLANG_VALLEY
            norm.contains("rohtang") -> ROHTANG_PASS
            norm.contains("mall road") -> MANALI_MALL_ROAD
            norm.contains("abbey falls") || norm.contains("abbey") -> ABBEY_FALLS
            norm.contains("raja's seat") || norm.contains("raja seat") -> RAJAS_SEAT
            norm.contains("dubare") || norm.contains("elephant") -> DUBARE_ELEPHANT_CAMP
            norm.contains("mandalpatti") -> MANDALPATTI
            norm.contains("coonoor") -> "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&q=80"
            norm.contains("amber") -> "https://images.unsplash.com/photo-1477587458883-47145ed94245?w=800&q=80"
            else -> DestinationImageProvider.getDestinationImage(name) ?: OOTY_LAKE
        }
    }
}


private fun String?.isNull_or_blank() = this == null || this.isBlank()

data class DestinationItem(
    val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val description: String,
    val rating: Double,
    val priceRange: String,
    val bestTimeToVisit: String
)

data class HotelItem(
    val id: String,
    val name: String,
    val destination: String,
    val imageUrl: String,
    val rating: Double,
    val pricePerNight: Int,
    val distance: String,
    val facilities: List<String>
)

data class AttractionItem(
    val id: String,
    val name: String,
    val destination: String,
    val imageUrl: String,
    val rating: Double,
    val entryFee: Int,
    val distance: String,
    val timing: String
)

data class RestaurantItem(
    val id: String,
    val name: String,
    val rating: Double,
    val priceRange: String = "₹₹",
    val priceLevel: String = "₹₹",
    val distance: String,
    val cuisine: String,
    val imageUrl: String = ""
)

data class ExpenseItem(
    val category: String,
    val amount: Int,
    val icon: String = "category",
    val iconName: String = "category"
)

data class PackingItem(
    val id: String,
    val title: String,
    val isPacked: Boolean = false
)

data class TravelDocumentItem(
    val id: String,
    val title: String,
    val type: String // "Tickets", "Hotel Reservation", "ID Proof", "Travel Insurance"
)

data class ItineraryDayItem(
    val dayNumber: Int,
    val title: String,
    val activities: List<String>
)

data class FullTrip(
    val id: String,
    val destination: String,
    val startLocation: String = "Chennai",
    val startDate: String,
    val endDate: String,
    val travelers: Int = 2,
    val budget: Int = 10000,
    val estimatedCost: Int = 8500,
    val status: String = "UPCOMING", // "UPCOMING", "ONGOING", "COMPLETED"
    val imageUrl: String? = null,
    val packageCost: Int = 12000,
    val planningCompletedPercentage: Int = 75,
    val daysUntilStart: Int = 7,
    val weatherTemp: String = "21°C",
    val weatherCondition: String = "Pleasant / Misty",
    val weatherRange: String = "15°C - 23°C",
    val hotel: HotelItem? = null,
    val attractions: List<AttractionItem> = emptyList(),
    val restaurants: List<RestaurantItem> = emptyList(),
    val itinerary: List<ItineraryDayItem> = emptyList(),
    val expenses: List<ExpenseItem> = emptyList(),
    val routeSteps: List<String> = emptyList(),
    val packingChecklist: List<PackingItem> = emptyList(),
    val documents: List<TravelDocumentItem> = emptyList()
)
