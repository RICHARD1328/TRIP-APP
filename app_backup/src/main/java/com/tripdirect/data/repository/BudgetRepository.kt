package com.tripdirect.data.repository

import com.tripdirect.data.model.BudgetInput
import kotlinx.coroutines.delay

interface BudgetRepository {
    suspend fun getSuggestedBudget(
        destination: String,
        days: Int,
        travelers: Int
    ): BudgetInput
}

class MockBudgetRepository : BudgetRepository {

    override suspend fun getSuggestedBudget(destination: String, days: Int, travelers: Int): BudgetInput {
        delay(500) // Simulate network delay
        
        // Mock logic: adjusting baseline based on destination
        val isPremiumDestination = destination.equals("Goa", ignoreCase = true) || destination.equals("Manali", ignoreCase = true)
        
        val accommodationBase = if (isPremiumDestination) 2000 else 1500
        val foodBase = if (isPremiumDestination) 400 else 300
        val localTransportBase = if (isPremiumDestination) 500 else 400
        
        return BudgetInput(
            destination = destination,
            days = days,
            adults = travelers,
            children = 0,
            totalBudget = 10000,
            transportCost = 1500 * travelers, // Assume typical round trip
            accommodationPerNight = accommodationBase,
            foodPerPersonPerDay = foodBase,
            attractionCost = 250 * travelers,
            activityCost = 500 * travelers,
            localTransportPerDay = localTransportBase,
            miscellaneousCost = 500
        )
    }
}
