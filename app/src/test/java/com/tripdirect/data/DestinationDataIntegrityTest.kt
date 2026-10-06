package com.tripdirect.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tripdirect.data.model.Destination
import com.tripdirect.data.repository.DestinationRepository
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class DestinationDataIntegrityTest {

    private val expectedIds = listOf(
        "ooty", "munnar", "kodaikanal", "goa", "jaipur", "manali",
        "coorg", "alleppey", "darjeeling", "wayanad", "pondicherry",
        "agra", "delhi", "mumbai", "chennai", "hyderabad", "bengaluru", "kochi"
    )

    private fun loadJsonDestinations(): List<Destination> {
        val jsonFile = File("src/main/assets/destinations.json")
        assertTrue("destinations.json file must exist in assets", jsonFile.exists())
        val jsonString = jsonFile.readText()
        val type = object : TypeToken<List<Destination>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    @Test
    fun testDestinationsJsonParsesSuccessfully() {
        val destinations = loadJsonDestinations()
        
        assertNotNull("Deserialized list must not be null", destinations)
        assertEquals("There should be exactly 18 destinations", 18, destinations.size)
        
        val actualIds = destinations.map { it.id }
        assertEquals("All 18 destination IDs must match expected IDs in order", expectedIds, actualIds)
        assertEquals("Destination IDs must be unique", 18, actualIds.toSet().size)
        
        // Check core fields for each destination
        destinations.forEach { dest ->
            assertFalse("ID must not be blank for ${dest.id}", dest.id.isBlank())
            assertFalse("Name must not be blank for ${dest.id}", dest.name.isBlank())
            assertFalse("State must not be blank for ${dest.id}", dest.state.isBlank())
            assertEquals("Country must be India for ${dest.id}", "India", dest.country)
            assertFalse("Region must not be blank for ${dest.id}", dest.region.isBlank())
            assertFalse("Category must not be blank for ${dest.id}", dest.category.isBlank())
            assertFalse("Description must not be blank for ${dest.id}", dest.description.isBlank())
            assertTrue("Rating must be > 0 for ${dest.id}", dest.rating > 0.0)
            assertTrue("Latitude must be valid for ${dest.id}", dest.latitude != 0.0)
            assertTrue("Longitude must be valid for ${dest.id}", dest.longitude != 0.0)
            assertFalse("Estimated daily budget must not be blank for ${dest.id}", dest.estimatedDailyBudget.isBlank())
            assertFalse("Weather must not be blank for ${dest.id}", dest.weather.isBlank())
            assertFalse("Google search query must not be blank for ${dest.id}", dest.googleSearchQuery.isBlank())
            assertFalse("Fallback image URL must not be blank for ${dest.id}", dest.fallbackImageUrl.isBlank())
            assertFalse("Safety information must not be blank for ${dest.id}", dest.safetyInformation.isBlank())
            
            assertNotNull("TransportInfo must not be null for ${dest.id}", dest.transportInfo)
            assertFalse("TransportInfo railway must not be blank for ${dest.id}", dest.transportInfo.railway.isBlank())
            assertFalse("TransportInfo airport must not be blank for ${dest.id}", dest.transportInfo.airport.isBlank())
            assertFalse("TransportInfo bus must not be blank for ${dest.id}", dest.transportInfo.bus.isBlank())
            assertFalse("TransportInfo road must not be blank for ${dest.id}", dest.transportInfo.road.isBlank())

            assertNotNull("Cost breakdown should be safely accessible for ${dest.id}", dest.costBreakdown)
            assertNotNull("Popular attractions should be non-null for ${dest.id}", dest.popularAttractions)
            assertNotNull("Hotels should be non-null for ${dest.id}", dest.hotels)
            assertNotNull("Local transport should be non-null for ${dest.id}", dest.localTransport)
            assertNotNull("Best time should be non-null for ${dest.id}", dest.bestTime)
            assertNotNull("Things to do should be non-null for ${dest.id}", dest.thingsToDo)
            assertNotNull("Local food should be non-null for ${dest.id}", dest.localFood)
            assertNotNull("Reviews should be non-null for ${dest.id}", dest.reviews)
            assertNotNull("Social content should be non-null for ${dest.id}", dest.socialContent)
        }

        // Specific checks for key destinations
        val jaipur = destinations.first { it.id == "jaipur" }
        assertEquals("Jaipur must have 4 attractions", 4, jaipur.popularAttractions.size)
        assertTrue("Jaipur must include City Palace", jaipur.popularAttractions.any { it.id == "att_jaipur_3" })
        assertTrue("Jaipur must include Jantar Mantar", jaipur.popularAttractions.any { it.id == "att_jaipur_4" })

        val manali = destinations.first { it.id == "manali" }
        assertEquals("Manali must have 3 attractions", 3, manali.popularAttractions.size)
        assertTrue("Manali must include Hadimba Devi Temple", manali.popularAttractions.any { it.id == "att_manali_1" })

        val coorg = destinations.first { it.id == "coorg" }
        assertEquals("Coorg must have 3 attractions", 3, coorg.popularAttractions.size)
        assertTrue("Coorg must include Abbey Falls", coorg.popularAttractions.any { it.id == "att_coorg_1" })
    }

    @Test
    fun testRepositoryAndJsonSynchronized() {
        val jsonDestinations = loadJsonDestinations()
        val repo = DestinationRepository()
        val repoDestinations = repo.getAllDestinations()

        assertEquals("Destination count must match between JSON and Repository", jsonDestinations.size, repoDestinations.size)

        for (i in expectedIds.indices) {
            val jsonD = jsonDestinations[i]
            val repoD = repoDestinations[i]

            assertEquals("Destination ID mismatch at index $i", jsonD.id, repoD.id)
            assertEquals("Name mismatch for ${jsonD.id}", jsonD.name, repoD.name)
            assertEquals("OfficialName mismatch for ${jsonD.id}", jsonD.officialName, repoD.officialName)
            assertEquals("Tagline mismatch for ${jsonD.id}", jsonD.tagline, repoD.tagline)
            assertEquals("State mismatch for ${jsonD.id}", jsonD.state, repoD.state)
            assertEquals("Country mismatch for ${jsonD.id}", jsonD.country, repoD.country)
            assertEquals("Region mismatch for ${jsonD.id}", jsonD.region, repoD.region)
            assertEquals("Rating mismatch for ${jsonD.id}", jsonD.rating, repoD.rating, 0.001)
            assertEquals("Category mismatch for ${jsonD.id}", jsonD.category, repoD.category)
            assertEquals("Description mismatch for ${jsonD.id}", jsonD.description, repoD.description)
            assertEquals("EstimatedDailyBudget mismatch for ${jsonD.id}", jsonD.estimatedDailyBudget, repoD.estimatedDailyBudget)
            assertEquals("Weather mismatch for ${jsonD.id}", jsonD.weather, repoD.weather)
            assertEquals("Latitude mismatch for ${jsonD.id}", jsonD.latitude, repoD.latitude, 0.0001)
            assertEquals("Longitude mismatch for ${jsonD.id}", jsonD.longitude, repoD.longitude, 0.0001)
            assertEquals("GoogleSearchQuery mismatch for ${jsonD.id}", jsonD.googleSearchQuery, repoD.googleSearchQuery)
            assertEquals("FallbackImageUrl mismatch for ${jsonD.id}", jsonD.fallbackImageUrl, repoD.fallbackImageUrl)
            assertEquals("SafetyInformation mismatch for ${jsonD.id}", jsonD.safetyInformation, repoD.safetyInformation)

            assertEquals("Transport railway mismatch for ${jsonD.id}", jsonD.transportInfo.railway, repoD.transportInfo.railway)
            assertEquals("Transport airport mismatch for ${jsonD.id}", jsonD.transportInfo.airport, repoD.transportInfo.airport)
            assertEquals("Transport bus mismatch for ${jsonD.id}", jsonD.transportInfo.bus, repoD.transportInfo.bus)
            assertEquals("Transport road mismatch for ${jsonD.id}", jsonD.transportInfo.road, repoD.transportInfo.road)

            assertEquals("Attraction count mismatch for ${jsonD.id}", jsonD.popularAttractions.size, repoD.popularAttractions.size)
            for (j in jsonD.popularAttractions.indices) {
                assertEquals("Attraction ID mismatch for ${jsonD.id}[$j]", jsonD.popularAttractions[j].id, repoD.popularAttractions[j].id)
                assertEquals("Attraction name mismatch for ${jsonD.id}[$j]", jsonD.popularAttractions[j].name, repoD.popularAttractions[j].name)
            }
        }
    }

    @Test
    fun testDestinationLookupsAndAliases() {
        val repo = DestinationRepository()

        // 1. Direct ID lookups for all 18 destinations
        expectedIds.forEach { id ->
            val dest = repo.getDestinationById(id)
            assertNotNull("Lookup by ID '$id' must not be null", dest)
            assertEquals("Retrieved destination ID must match", id, dest?.id)
        }

        // 2. Numeric aliases 1 through 18
        assertEquals("ooty", repo.getDestinationById("1")?.id)
        assertEquals("munnar", repo.getDestinationById("2")?.id)
        assertEquals("kodaikanal", repo.getDestinationById("3")?.id)
        assertEquals("goa", repo.getDestinationById("4")?.id)
        assertEquals("jaipur", repo.getDestinationById("5")?.id)
        assertEquals("manali", repo.getDestinationById("6")?.id)
        assertEquals("coorg", repo.getDestinationById("7")?.id)
        assertEquals("alleppey", repo.getDestinationById("8")?.id)
        assertEquals("darjeeling", repo.getDestinationById("9")?.id)
        assertEquals("wayanad", repo.getDestinationById("10")?.id)
        assertEquals("pondicherry", repo.getDestinationById("11")?.id)
        assertEquals("agra", repo.getDestinationById("12")?.id)
        assertEquals("delhi", repo.getDestinationById("13")?.id)
        assertEquals("mumbai", repo.getDestinationById("14")?.id)
        assertEquals("chennai", repo.getDestinationById("15")?.id)
        assertEquals("hyderabad", repo.getDestinationById("16")?.id)
        assertEquals("bengaluru", repo.getDestinationById("17")?.id)
        assertEquals("kochi", repo.getDestinationById("18")?.id)

        // 3. Name and Official Name lookups
        assertEquals("jaipur", repo.getDestinationById("Pink City")?.id)
        assertEquals("ooty", repo.getDestinationById("Udhagamandalam")?.id)
        assertEquals("coorg", repo.getDestinationById("Kodagu")?.id)
        assertEquals("mumbai", repo.getDestinationById("Bombay")?.id)
        assertEquals("bengaluru", repo.getDestinationById("Bangalore")?.id)
        assertEquals("chennai", repo.getDestinationById("Madras")?.id)
        assertEquals("kochi", repo.getDestinationById("Cochin")?.id)
        assertEquals("agra", repo.getDestinationById("City of Taj")?.id)
        assertEquals("hyderabad", repo.getDestinationById("City of Pearls")?.id)
    }

    @Test
    fun testDestinationSearch() {
        val repo = DestinationRepository()

        assertTrue(repo.searchDestinations("Ooty").any { it.id == "ooty" })
        assertTrue(repo.searchDestinations("Munnar").any { it.id == "munnar" })
        assertTrue(repo.searchDestinations("Kodaikanal").any { it.id == "kodaikanal" })
        assertTrue(repo.searchDestinations("Jaipur").any { it.id == "jaipur" })
        assertTrue(repo.searchDestinations("Delhi").any { it.id == "delhi" })
        assertTrue(repo.searchDestinations("Agra").any { it.id == "agra" })
        assertTrue(repo.searchDestinations("Tamil Nadu").size >= 3) // Ooty, Kodaikanal, Chennai
        assertTrue(repo.searchDestinations("Kerala").size >= 4) // Munnar, Alleppey, Wayanad, Kochi
    }
}
