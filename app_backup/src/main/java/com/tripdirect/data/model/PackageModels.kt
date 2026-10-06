package com.tripdirect.data.model

enum class FeatureAdvantage {
    SELF_PLAN,
    PACKAGE,
    NEUTRAL
}

data class TouristPackage(
    val id: String,
    val title: String,
    val providerName: String,
    val pricePerPerson: Int,
    val totalPrice: Int,
    val durationDays: Int,
    val hotelCategory: String,
    val transportType: String,
    val includedAttractionsCount: Int,
    val guideIncluded: Boolean,
    val mealsIncluded: String,
    val flexibilityScore: String, // "Low", "Medium", "High"
    val cancellationPolicy: String,
    val hiddenBrokerFee: Int,
    val description: String
)

data class SelfPlan(
    val id: String,
    val destination: String,
    val durationDays: Int,
    val travelersCount: Int,
    val hotelType: String,
    val transportType: String,
    val stayCost: Int,
    val transportCost: Int,
    val foodCost: Int,
    val attractionsCost: Int,
    val activitiesCost: Int,
    val totalCost: Int,
    val flexibilityScore: String, // "High"
    val cancellationPolicy: String
)

data class ComparisonFeature(
    val category: String, // "Cost", "Stay", "Transport", "Attractions", "Guide", "Flexibility", "Cancellation", "Hidden Costs"
    val featureName: String,
    val packageValue: String,
    val selfPlanValue: String,
    val advantage: FeatureAdvantage,
    val notes: String = ""
)

data class ItemCostBreakdown(
    val categoryName: String,
    val packageCost: Int,
    val selfPlanCost: Int
)

data class PackageComparison(
    val destination: String,
    val durationDays: Int,
    val travelersCount: Int,
    val touristPackage: TouristPackage,
    val selfPlan: SelfPlan,
    val features: List<ComparisonFeature>,
    val costBreakdown: List<ItemCostBreakdown>
) {
    val savingsAmount: Int get() = (touristPackage.totalPrice - selfPlan.totalCost).coerceAtLeast(0)
    val savingsPercentage: Int get() = if (touristPackage.totalPrice > 0) ((savingsAmount.toDouble() / touristPackage.totalPrice) * 100).toInt() else 0
}
