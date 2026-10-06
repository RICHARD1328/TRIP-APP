from typing import Dict, Any, List


def calculate_trip_budget(
    user_budget: float,
    duration_days: int,
    travelers: int,
    transport_preference: str = "car",
    accommodation_preference: str = "budget"
) -> Dict[str, Any]:
    """
    Calculate detailed budget allocation across 7 categories:
    transportation, accommodation, food, activities, local_travel, other, emergency_reserve.
    """
    duration = max(1, duration_days)
    people = max(1, travelers)
    nights = max(1, duration - 1)
    rooms_needed = max(1, (people + 1) // 2)

    # 1. Transportation cost estimation based on preference
    trans_pref = transport_preference.lower()
    if "flight" in trans_pref or "air" in trans_pref:
        base_trans_per_person = 3500.0
    elif "train" in trans_pref:
        base_trans_per_person = 600.0
    elif "bus" in trans_pref:
        base_trans_per_person = 800.0
    else:  # Car / cab / default
        base_trans_per_person = 1500.0

    transportation_cost = round(base_trans_per_person * people, 2)

    # 2. Accommodation cost per night based on preference
    acc_pref = accommodation_preference.lower()
    if "luxury" in acc_pref or "5-star" in acc_pref:
        nightly_rate = 7500.0
    elif "mid-range" in acc_pref or "3-star" in acc_pref or "standard" in acc_pref:
        nightly_rate = 3500.0
    else:  # Budget / hostel
        nightly_rate = 1800.0

    accommodation_cost = round(nightly_rate * nights * rooms_needed, 2)

    # 3. Food cost (3 meals/day per person)
    daily_food_per_person = 500.0
    food_cost = round(daily_food_per_person * duration * people, 2)

    # 4. Activities / Sightseeing entry fees
    daily_activities_per_person = 250.0
    activities_cost = round(daily_activities_per_person * duration * people, 2)

    # 5. Local Travel (auto/cabs/taxis within destination)
    daily_local_travel = 400.0
    local_travel_cost = round(daily_local_travel * duration, 2)

    # 6. Miscellaneous / Other
    other_cost = round(200.0 * duration, 2)

    # 7. Emergency Reserve (5% of calculated subtotal)
    subtotal = transportation_cost + accommodation_cost + food_cost + activities_cost + local_travel_cost + other_cost
    emergency_reserve = round(subtotal * 0.05, 2)

    total_estimated_cost = round(subtotal + emergency_reserve, 2)
    cost_per_traveler = round(total_estimated_cost / people, 2)
    cost_per_day = round(total_estimated_cost / duration, 2)
    remaining_budget = round(user_budget - total_estimated_cost, 2)

    return {
        "categories": {
            "transportation": transportation_cost,
            "accommodation": accommodation_cost,
            "food": food_cost,
            "activities": activities_cost,
            "local_travel": local_travel_cost,
            "other": other_cost,
            "emergency_reserve": emergency_reserve
        },
        "total_estimated_cost": total_estimated_cost,
        "cost_per_traveler": cost_per_traveler,
        "cost_per_day": cost_per_day,
        "user_budget": user_budget,
        "remaining_budget": remaining_budget,
        "is_within_budget": total_estimated_cost <= user_budget,
        "is_live_pricing": False,
        "price_data_source": "Estimated based on user preferences and regional reference averages"
    }
