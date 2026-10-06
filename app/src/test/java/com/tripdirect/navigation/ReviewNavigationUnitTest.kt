package com.tripdirect.navigation

import com.tripdirect.data.model.ReviewCategory
import com.tripdirect.data.repository.MockPlacesRepository
import com.tripdirect.data.repository.MockReviewRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewNavigationUnitTest {

    private val reviewRepository = MockReviewRepository()
    private val placesRepository = MockPlacesRepository()

    @Test
    fun testReviewRoute_generatesDynamicRouteWithEntityId() {
        val ootyRoute = Screen.Reviews.createRoute("ooty_botanical_garden")
        assertEquals("reviews?entityId=ooty_botanical_garden", ootyRoute)

        val munnarRoute = Screen.Reviews.createRoute("munnar_tea_museum")
        assertEquals("reviews?entityId=munnar_tea_museum", munnarRoute)

        val defaultRoute = Screen.Reviews.createRoute(null)
        assertEquals("reviews", defaultRoute)
    }

    @Test
    fun testPlaceDetailRoute_generatesDynamicPlaceRoute() {
        val route1 = Screen.PlaceDetail.createRoute("ooty_botanical_garden")
        assertEquals("place/ooty_botanical_garden", route1)

        val route2 = Screen.PlaceDetail.createRoute("munnar_tea_museum")
        assertEquals("place/munnar_tea_museum", route2)

        val route3 = Screen.PlaceDetail.createRoute("fort_aguada")
        assertEquals("place/fort_aguada", route3)
    }

    @Test
    fun testReviewRepository_hasDiverseEntityIdsAndPlaces() = runBlocking {
        val allReviews = reviewRepository.getReviews("all")
        assertTrue(allReviews.isNotEmpty())

        val ootyReview = allReviews.find { it.entityId == "ooty_botanical_garden" }
        assertNotNull("Expected review for Ooty Botanical Garden", ootyReview)
        assertEquals("ooty_botanical_garden", ootyReview?.entityId)

        val munnarReview = allReviews.find { it.entityId == "munnar_tea_museum" }
        assertNotNull("Expected review for Munnar Tea Museum", munnarReview)
        assertEquals("munnar_tea_museum", munnarReview?.entityId)

        val goaReview = allReviews.find { it.entityId == "fort_aguada" }
        assertNotNull("Expected review for Fort Aguada", goaReview)
        assertEquals("fort_aguada", goaReview?.entityId)
    }

    @Test
    fun testReviewSummary_reflectsTargetEntity() = runBlocking {
        val munnarSummary = reviewRepository.getReviewSummary("munnar_tea_museum")
        assertEquals("munnar_tea_museum", munnarSummary.entityId)
        assertEquals("Munnar Tea Museum", munnarSummary.entityName)
        assertEquals("Munnar, Kerala", munnarSummary.entityLocation)

        val ootySummary = reviewRepository.getReviewSummary("ooty_botanical_garden")
        assertEquals("ooty_botanical_garden", ootySummary.entityId)
        assertEquals("Ooty Botanical Garden", ootySummary.entityName)
        assertEquals("Ooty, Tamil Nadu", ootySummary.entityLocation)
    }

    @Test
    fun testPlacesRepository_resolvesEntityIdsCorrectly() = runBlocking {
        // Test Ooty review entity ID alias resolution
        val ootyPlace = placesRepository.getPlaceById("ooty_botanical_garden")
        assertNotNull("Should resolve ooty_botanical_garden", ootyPlace)
        assertEquals("Government Botanical Garden", ootyPlace?.name)

        // Test Munnar review entity ID
        val munnarPlace = placesRepository.getPlaceById("munnar_tea_museum")
        assertNotNull("Should resolve munnar_tea_museum", munnarPlace)
        assertEquals("Munnar Tea Museum", munnarPlace?.name)

        // Test Goa review entity ID
        val goaPlace = placesRepository.getPlaceById("fort_aguada")
        assertNotNull("Should resolve fort_aguada", goaPlace)
        assertEquals("Fort Aguada", goaPlace?.name)

        // Test Ooty Lake review entity ID
        val lakePlace = placesRepository.getPlaceById("ooty_lake")
        assertNotNull("Should resolve ooty_lake", lakePlace)
        assertEquals("Ooty Lake", lakePlace?.name)
    }

    @Test
    fun testPlacesRepository_handlesInvalidAndEmptyIdsGracefully() = runBlocking {
        val emptyPlace = placesRepository.getPlaceById("")
        assertNull("Empty ID should return null safely without throwing", emptyPlace)

        val blankPlace = placesRepository.getPlaceById("   ")
        assertNull("Blank ID should return null safely without throwing", blankPlace)

        val invalidPlace = placesRepository.getPlaceById("non_existent_place_xyz_999")
        assertNull("Non-existent ID should return null without throwing", invalidPlace)
    }
}
