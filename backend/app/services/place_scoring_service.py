from typing import Dict, Any, List, Tuple

# Configurable Weights
DEFAULT_WEIGHTS = {
    "interest_match": 0.30,
    "rating_score": 0.20,
    "distance_score": 0.20,
    "budget_score": 0.15,
    "suitability_score": 0.15
}


def score_place(
    place: Dict[str, Any],
    user_interests: List[str],
    daily_budget_per_person: float,
    travel_style: str = "balanced",
    weights: Dict[str, float] = None
) -> Tuple[float, List[str]]:
    """
    Calculate transparent recommendation score (0.0 to 1.0) and human-readable recommendation reasons.
    Formula:
      score = w_interest * interest_match + w_rating * rating_score + w_dist * distance_score + w_budget * budget_score + w_suitability * suitability_score
    """
    if weights is None:
        weights = DEFAULT_WEIGHTS

    reasons: List[str] = []

    # 1. Interest Match (Normalized 0.0 - 1.0)
    user_interests_lower = [i.strip().lower() for i in user_interests if i.strip()]
    place_text = f"{place.get('name', '')} {place.get('category', '')} {' '.join(place.get('tags', {}).values())}".lower()

    matched_interests = []
    for interest in user_interests_lower:
        if interest in place_text:
            matched_interests.append(interest.capitalize())

    if user_interests_lower:
        interest_match = min(1.0, len(matched_interests) / len(user_interests_lower))
    else:
        interest_match = 0.5  # Neutral if no interests provided

    for mi in matched_interests:
        reasons.append(f"Matches interest: {mi}")

    # 2. Rating Score (Normalized 0.0 - 1.0)
    rating = place.get("rating") or 4.5
    rating_score = min(1.0, rating / 5.0)
    if rating >= 4.5:
        reasons.append(f"Highly rated attraction ({rating}/5.0)")

    # 3. Distance Score (Normalized 0.0 - 1.0 based on proximity to center)
    distance_km = place.get("distance_km", 5.0)
    distance_score = max(0.1, 1.0 - (distance_km / 35.0))
    if distance_km <= 5.0:
        reasons.append(f"Conveniently located ({distance_km:.1f} km from city center)")
    elif distance_km <= 15.0:
        reasons.append(f"Within easy reach ({distance_km:.1f} km from city center)")

    # 4. Budget Score (Normalized 0.0 - 1.0)
    category = place.get("category", "attraction").lower()
    if category in ["park", "nature", "temple", "viewpoint"]:
        est_cost = 50.0
    elif category in ["museum", "historic"]:
        est_cost = 150.0
    elif category in ["restaurant", "cafe"]:
        est_cost = 350.0
    else:
        est_cost = 200.0

    if daily_budget_per_person >= est_cost:
        budget_score = 1.0
        reasons.append("Fits comfortable daily budget")
    else:
        budget_score = max(0.2, daily_budget_per_person / max(1.0, est_cost))
        reasons.append("Economical entry/visit option")

    # 5. Suitability Score (Normalized 0.0 - 1.0 based on travel style & category)
    style = travel_style.lower()
    suitability_score = 0.8  # Default baseline

    if style == "relaxed" and category in ["park", "cafe", "nature", "viewpoint"]:
        suitability_score = 1.0
        reasons.append("Ideal for a relaxed travel pace")
    elif style == "fast-paced" or style == "active":
        suitability_score = 0.9
        reasons.append("Great for active exploration")
    elif style == "budget" and est_cost <= 100:
        suitability_score = 1.0
        reasons.append("Great low-cost pick")

    # Final Weighted Calculation
    total_score = (
        weights["interest_match"] * interest_match +
        weights["rating_score"] * rating_score +
        weights["distance_score"] * distance_score +
        weights["budget_score"] * budget_score +
        weights["suitability_score"] * suitability_score
    )

    final_score = round(total_score, 4)
    return final_score, reasons[:3]
