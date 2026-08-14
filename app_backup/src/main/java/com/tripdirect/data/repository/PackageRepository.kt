package com.tripdirect.data.repository

import com.tripdirect.data.model.ComparisonFeature
import com.tripdirect.data.model.FeatureAdvantage
import com.tripdirect.data.model.ItemCostBreakdown
import com.tripdirect.data.model.PackageComparison
import com.tripdirect.data.model.SelfPlan
import com.tripdirect.data.model.TouristPackage
import kotlinx.coroutines.delay

interface PackageRepository {
    suspend fun getComparison(destination: String, durationDays: Int, travelers: Int): PackageComparison
    suspend fun customizeSelfPlan(baseComparison: PackageComparison, customStayCost: Int, customTransportCost: Int): PackageComparison
}

class MockPackageRepository : PackageRepository {

    override suspend fun getComparison(destination: String, durationDays: Int, travelers: Int): PackageComparison {
        delay(300)

        val pkg = TouristPackage(
            id = "pkg_ooty_std",
            title = "Ooty 3D/2N Complete Tourist Package",
            providerName = "Sunshine Holidays & Travels",
            pricePerPerson = 6000,
            totalPrice = 12000,
            durationDays = 3,
            hotelCategory = "3★ Standard Hotel (Assigned)",
            transportType = "Shared Tourist AC Bus",
            includedAttractionsCount = 5,
            guideIncluded = true,
            mealsIncluded = "Breakfast Only",
            flexibilityScore = "Low",
            cancellationPolicy = "Strict (50% fee < 48h)",
            hiddenBrokerFee = 2500,
            description = "Standard group travel package with fixed itinerary, mandatory shopping stops, and shared bus transport."
        )

        val self = SelfPlan(
            id = "self_ooty_opt",
            destination = "Ooty",
            durationDays = 3,
            travelersCount = 2,
            hotelType = "3★ Selected Heritage Hotel",
            transportType = "Direct Bus + Local Taxi",
            stayCost = 3000,
            transportCost = 2500,
            foodCost = 1800,
            attractionsCost = 400,
            activitiesCost = 800,
            totalCost = 8500,
            flexibilityScore = "High (100% Flexible)",
            cancellationPolicy = "Flexible (Free cancellation up to 24h)"
        )

        val features = listOf(
            ComparisonFeature(
                category = "Cost",
                featureName = "Total Estimated Cost",
                packageValue = "₹12,000 (₹6,000/person)",
                selfPlanValue = "₹8,500 (₹4,250/person)",
                advantage = FeatureAdvantage.SELF_PLAN,
                notes = "Self-plan saves ₹3,500 by eliminating travel agency markups and broker commissions."
            ),
            ComparisonFeature(
                category = "Stay",
                featureName = "Hotel Accommodation",
                packageValue = "3★ Fixed Hotel (Pre-assigned)",
                selfPlanValue = "3★ Choice Heritage Resort",
                advantage = FeatureAdvantage.SELF_PLAN,
                notes = "Self-planning lets you pick your exact room and location based on user ratings."
            ),
            ComparisonFeature(
                category = "Transport",
                featureName = "Transportation Mode",
                packageValue = "Shared Group Tourist Bus",
                selfPlanValue = "Direct Express Bus + Local Taxi",
                advantage = FeatureAdvantage.SELF_PLAN,
                notes = "No waiting for group members; custom departure timing."
            ),
            ComparisonFeature(
                category = "Attractions",
                featureName = "Included Sights",
                packageValue = "5 Fixed Sights (Fixed order)",
                selfPlanValue = "6 Selected Sights (Custom order)",
                advantage = FeatureAdvantage.SELF_PLAN,
                notes = "Choose spots based on your interest; skip crowded commercial stops."
            ),
            ComparisonFeature(
                category = "Guide",
                featureName = "Tour Guide & Escort",
                packageValue = "Group Tour Guide Included",
                selfPlanValue = "AI Audio Guide / Self Guided",
                advantage = FeatureAdvantage.PACKAGE,
                notes = "Packages include a physical group guide, whereas self-planning uses AI digital guides."
            ),
            ComparisonFeature(
                category = "Flexibility",
                featureName = "Schedule Flexibility",
                packageValue = "Low (Strict timetable)",
                selfPlanValue = "High (Change anytime)",
                advantage = FeatureAdvantage.SELF_PLAN,
                notes = "Spend extra time where you like without group rush."
            ),
            ComparisonFeature(
                category = "Cancellation",
                featureName = "Cancellation Policy",
                packageValue = "Strict (50% penalty)",
                selfPlanValue = "Flexible (Free up to 24h)",
                advantage = FeatureAdvantage.SELF_PLAN,
                notes = "Direct hotel/transport bookings offer easier refunds."
            ),
            ComparisonFeature(
                category = "Hidden Costs",
                featureName = "Agency Commission & Hidden Fees",
                packageValue = "₹2,500 Broker Markup",
                selfPlanValue = "₹0 (Direct booking)",
                advantage = FeatureAdvantage.SELF_PLAN,
                notes = "Every rupee goes directly to hotels, transport, and attractions."
            )
        )

        val breakdown = listOf(
            ItemCostBreakdown("Accommodation", 4500, 3000),
            ItemCostBreakdown("Transportation", 3200, 2500),
            ItemCostBreakdown("Food & Dining", 1800, 1800),
            ItemCostBreakdown("Attractions & Entry", 800, 400),
            ItemCostBreakdown("Guide / Misc", 1700, 800)
        )

        return PackageComparison(
            destination = destination.ifEmpty { "Ooty" },
            durationDays = durationDays,
            travelersCount = travelers,
            touristPackage = pkg,
            selfPlan = self,
            features = features,
            costBreakdown = breakdown
        )
    }

    override suspend fun customizeSelfPlan(
        baseComparison: PackageComparison,
        customStayCost: Int,
        customTransportCost: Int
    ): PackageComparison {
        delay(150)
        val updatedSelfCost = customStayCost + customTransportCost + baseComparison.selfPlan.foodCost + baseComparison.selfPlan.attractionsCost + baseComparison.selfPlan.activitiesCost
        val updatedSelfPlan = baseComparison.selfPlan.copy(
            stayCost = customStayCost,
            transportCost = customTransportCost,
            totalCost = updatedSelfCost
        )
        return baseComparison.copy(selfPlan = updatedSelfPlan)
    }
}
