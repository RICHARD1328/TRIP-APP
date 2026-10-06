package com.tripdirect.domain

import com.tripdirect.data.model.BudgetBreakdown
import com.tripdirect.data.model.BudgetInput
import kotlin.math.max

object BudgetCalculator {

    fun calculate(input: BudgetInput): BudgetBreakdown {
        val nights = max(input.days - 1, 0)
        val totalTravelers = input.adults + input.children

        // Calculate individual categories
        val transportCost = input.transportCost
        val accommodationCost = input.accommodationPerNight * nights
        val foodCost = input.foodPerPersonPerDay * totalTravelers * input.days
        val attractionCost = input.attractionCost
        val activityCost = input.activityCost
        val localTransportCost = input.localTransportPerDay * input.days
        val miscellaneousCost = input.miscellaneousCost

        // Calculate total
        val total = transportCost + accommodationCost + foodCost + attractionCost + activityCost + localTransportCost + miscellaneousCost

        // Calculate averages
        val perPerson = if (totalTravelers > 0) total / totalTravelers else 0
        val perDay = if (input.days > 0) total / input.days else 0

        // Calculate remaining
        val remaining = input.totalBudget - total

        return BudgetBreakdown(
            transport = transportCost,
            accommodation = accommodationCost,
            food = foodCost,
            attractions = attractionCost,
            activities = activityCost,
            localTransport = localTransportCost,
            miscellaneous = miscellaneousCost,
            total = total,
            perPerson = perPerson,
            perDay = perDay,
            budget = input.totalBudget,
            remaining = remaining
        )
    }
}
