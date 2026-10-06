package com.tripdirect.data.model

data class BudgetInput(
    val destination: String,
    val days: Int,
    val adults: Int,
    val children: Int,
    val totalBudget: Int,
    val transportCost: Int,
    val accommodationPerNight: Int,
    val foodPerPersonPerDay: Int,
    val attractionCost: Int,
    val activityCost: Int,
    val localTransportPerDay: Int,
    val miscellaneousCost: Int
)

data class BudgetBreakdown(
    val transport: Int,
    val accommodation: Int,
    val food: Int,
    val attractions: Int,
    val activities: Int,
    val localTransport: Int,
    val miscellaneous: Int,
    val total: Int,
    val perPerson: Int,
    val perDay: Int,
    val budget: Int,
    val remaining: Int
)
