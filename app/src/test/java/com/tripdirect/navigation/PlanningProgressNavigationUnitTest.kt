package com.tripdirect.navigation

import com.tripdirect.data.model.TripPlan
import com.tripdirect.data.model.TripPlanRequest
import com.tripdirect.data.repository.TripPlannerRepository
import com.tripdirect.viewmodel.TripPlannerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PlanningProgressNavigationUnitTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeTestTripPlannerRepository(
        var shouldThrow: Boolean = false
    ) : TripPlannerRepository {
        override suspend fun generateTripPlan(request: TripPlanRequest): TripPlan {
            if (shouldThrow) throw RuntimeException("Simulated API failure")
            return TripPlan(
                id = "plan_test_123",
                from = request.from,
                destination = request.destination,
                startDate = request.startDate,
                endDate = request.endDate,
                travelers = request.travelers,
                budget = request.budget,
                interests = request.interests,
                travelStyle = request.travelStyle,
                hotelPreference = request.hotelPreference,
                transportPreference = request.transportPreference,
                days = emptyList(),
                budgetSummary = com.tripdirect.data.model.TripBudget(0, 0, 0, 0, 0, 0, 0, request.budget),
                summary = "Test Plan Summary"
            )
        }

        override suspend fun optimizeTrip(tripPlan: TripPlan): TripPlan {
            if (shouldThrow) throw RuntimeException("Simulated API failure")
            return tripPlan.copy(id = "plan_optimized_456")
        }

        override suspend fun modifyTrip(tripPlan: TripPlan, instruction: String): TripPlan {
            if (shouldThrow) throw RuntimeException("Simulated API failure")
            return tripPlan.copy(id = "plan_modified_789")
        }
    }

    @Test
    fun testRoutes_definedCorrectly() {
        assertEquals("planningProgress", Screen.PlanningProgress.route)
        assertEquals("generatedTrip", Screen.GeneratedTrip.route)
    }

    @Test
    fun testGenerateTrip_emitsSingleShotNavigationEventAndConsumes() = runTest {
        val fakeRepo = FakeTestTripPlannerRepository()
        val viewModel = TripPlannerViewModel(fakeRepo)

        // Initial State
        assertNull(viewModel.navigationEvent.value)
        assertNull(viewModel.tripPlan.value)
        assertFalse(viewModel.isGenerating.value)
        assertNull(viewModel.errorMessage.value)

        // Set inputs
        viewModel.setFromLocation("Chennai")
        viewModel.setDestination("Ooty")
        viewModel.setDates(LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 23))
        viewModel.toggleInterest("Nature")
        viewModel.setBudget("₹15,000")

        var completionCallbackInvoked = false
        viewModel.generateTrip { completionCallbackInvoked = true }

        // Advance coroutines past UI step delays & generation
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify successful generation
        assertTrue("onComplete callback should be called", completionCallbackInvoked)
        assertNotNull("tripPlan should be populated", viewModel.tripPlan.value)
        assertEquals("plan_test_123", viewModel.tripPlan.value?.id)
        assertFalse("isGenerating should be false after completion", viewModel.isGenerating.value)
        assertNull("errorMessage should be null on success", viewModel.errorMessage.value)

        // Verify single-shot navigation event is set
        assertEquals("plan_test_123", viewModel.navigationEvent.value)

        // Simulate single-shot consumption by PlanningProgressScreen
        viewModel.consumeNavigationEvent()
        assertNull("navigationEvent must be reset to null after consumption", viewModel.navigationEvent.value)

        // Even though tripPlan remains retained, navigationEvent is null (prevents duplicate navigation)
        assertNotNull("tripPlan must remain available for GeneratedTripScreen", viewModel.tripPlan.value)
    }

    @Test
    fun testModifyTrip_emitsNewNavigationEventAndConsumes() = runTest {
        val fakeRepo = FakeTestTripPlannerRepository()
        val viewModel = TripPlannerViewModel(fakeRepo)

        viewModel.setFromLocation("Chennai")
        viewModel.setDestination("Ooty")
        viewModel.setDates(LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 23))
        viewModel.toggleInterest("Nature")
        viewModel.generateTrip()
        testDispatcher.scheduler.advanceUntilIdle()

        // Consume initial event
        viewModel.consumeNavigationEvent()
        assertNull(viewModel.navigationEvent.value)

        // User edits trip
        viewModel.modifyTrip("Make it cheaper")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify new modified event
        assertEquals("plan_modified_789", viewModel.navigationEvent.value)
        assertEquals("plan_modified_789", viewModel.tripPlan.value?.id)

        // Consume modified event
        viewModel.consumeNavigationEvent()
        assertNull(viewModel.navigationEvent.value)
    }

    @Test
    fun testPlanningFailure_doesNotTriggerNavigationEvent() = runTest {
        val fakeRepo = FakeTestTripPlannerRepository(shouldThrow = true)
        val viewModel = TripPlannerViewModel(fakeRepo)

        viewModel.setFromLocation("Chennai")
        viewModel.setDestination("Ooty")
        viewModel.setDates(LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 23))
        viewModel.toggleInterest("Nature")

        var completionCallbackInvoked = false
        viewModel.generateTrip { completionCallbackInvoked = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Verification: Failed generation must NOT emit a navigation event
        assertFalse("onComplete should NOT be invoked on failure", completionCallbackInvoked)
        assertNull("navigationEvent must be null on failure", viewModel.navigationEvent.value)
        assertNull("tripPlan must be null on failure", viewModel.tripPlan.value)
        assertFalse("isGenerating should be reset to false", viewModel.isGenerating.value)
        assertNotNull("errorMessage should contain error description", viewModel.errorMessage.value)
    }

    @Test
    fun testNavigationGuard_preventsDuplicateTriggers() {
        var hasNavigated = false
        var navigationTriggerCount = 0

        fun triggerNavigationIfGuarded(targetEventId: String?, isGenerating: Boolean, errorMessage: String?) {
            if (targetEventId != null && !hasNavigated && !isGenerating && errorMessage == null) {
                hasNavigated = true
                navigationTriggerCount++
            }
        }

        // 1. Initial State: null plan
        triggerNavigationIfGuarded(targetEventId = null, isGenerating = false, errorMessage = null)
        assertEquals(0, navigationTriggerCount)
        assertFalse(hasNavigated)

        // 2. Generating State: plan not ready
        triggerNavigationIfGuarded(targetEventId = "plan_123", isGenerating = true, errorMessage = null)
        assertEquals(0, navigationTriggerCount)
        assertFalse(hasNavigated)

        // 3. Error State: error present
        triggerNavigationIfGuarded(targetEventId = "plan_123", isGenerating = false, errorMessage = "Error occurred")
        assertEquals(0, navigationTriggerCount)
        assertFalse(hasNavigated)

        // 4. Success State: Ready to navigate
        triggerNavigationIfGuarded(targetEventId = "plan_123", isGenerating = false, errorMessage = null)
        assertEquals(1, navigationTriggerCount)
        assertTrue(hasNavigated)

        // 5. Subsequent recompositions or repeated state emissions: BLOCKED
        triggerNavigationIfGuarded(targetEventId = "plan_123", isGenerating = false, errorMessage = null)
        assertEquals(1, navigationTriggerCount) // Count remains 1!

        triggerNavigationIfGuarded(targetEventId = "plan_123_update", isGenerating = false, errorMessage = null)
        assertEquals(1, navigationTriggerCount) // Still blocked by hasNavigated!
    }
}
