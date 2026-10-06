import json
from typing import List, Optional, Dict, Any
from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.package import Package
from app.repositories import package_repository
from app.schemas.package import (
    PackageResponse,
    PackageListResponse,
    ComparePackagesRequest,
    PackageComparisonResponse,
    ComparedPackage,
    ScoreBreakdown,
    AttributeComparisonItem,
    ComparisonSummary,
    UserPreferences,
)


def parse_json_list(raw_value: str) -> List[str]:
    """
    Parse a JSON formatted string into a Python list of strings.
    Fallback to comma-separated split if plain text.
    """
    if not raw_value:
        return []
    try:
        parsed = json.loads(raw_value)
        if isinstance(parsed, list):
            return [str(item) for item in parsed]
    except Exception:
        pass
    return [item.strip() for item in raw_value.split(",") if item.strip()]


def package_to_response(pkg: Package) -> PackageResponse:
    """
    Convert a SQLAlchemy Package model instance into a PackageResponse schema.
    """
    return PackageResponse(
        id=pkg.id,
        title=pkg.title,
        destination=pkg.destination,
        total_price=pkg.total_price,
        currency=pkg.currency or "INR",
        duration_days=pkg.duration_days,
        duration_nights=pkg.duration_nights,
        accommodation=pkg.accommodation,
        transport=pkg.transport,
        included_attractions=parse_json_list(pkg.included_attractions),
        meals=pkg.meals,
        rating=pkg.rating,
        review_count=pkg.review_count,
        exclusions=parse_json_list(pkg.exclusions),
        provider_name=pkg.provider_name,
        is_demo_data=pkg.is_demo_data,
        data_source=pkg.data_source,
        image_url=pkg.image_url,
    )


def list_packages(
    db: Session,
    destination: Optional[str] = None,
    max_price: Optional[float] = None,
    max_duration: Optional[int] = None,
    min_rating: Optional[float] = None,
    search: Optional[str] = None,
    limit: int = 20,
    offset: int = 0,
) -> PackageListResponse:
    """
    Retrieve paginated package catalog.
    """
    packages, total = package_repository.get_all_packages(
        db=db,
        destination=destination,
        max_price=max_price,
        max_duration=max_duration,
        min_rating=min_rating,
        search=search,
        limit=limit,
        offset=offset,
    )
    return PackageListResponse(
        total=total,
        packages=[package_to_response(pkg) for pkg in packages],
        is_demo_data=True,
        disclaimer="Package prices and inclusions shown are demo estimates and not connected to a live real-time booking provider."
    )


def get_package_detail(db: Session, package_id: int) -> PackageResponse:
    """
    Retrieve single package by ID or raise 404.
    """
    pkg = package_repository.get_package_by_id(db, package_id)
    if not pkg:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Travel package with ID {package_id} not found."
        )
    return package_to_response(pkg)


def compare_packages(db: Session, request: ComparePackagesRequest) -> PackageComparisonResponse:
    """
    Structured comparison engine for selected travel packages.
    """
    if len(request.package_ids) < 2:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="At least 2 package IDs must be provided for comparison."
        )

    # Remove duplicates while preserving order
    unique_ids = list(dict.fromkeys(request.package_ids))
    packages = package_repository.get_packages_by_ids(db, unique_ids)

    found_ids = {pkg.id for pkg in packages}
    missing_ids = [pid for pid in unique_ids if pid not in found_ids]
    if missing_ids:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Packages not found for IDs: {missing_ids}"
        )

    # Map package details & parsed lists
    parsed_packages = [package_to_response(pkg) for pkg in packages]

    prices = [p.total_price for p in parsed_packages]
    min_price, max_price = min(prices), max(prices)

    ratings = [p.rating for p in parsed_packages]
    min_rating, max_rating = min(ratings), max(ratings)

    attraction_counts = [len(p.included_attractions) for p in parsed_packages]
    min_attraction_cnt, max_attraction_cnt = min(attraction_counts), max(attraction_counts)

    durations = [p.duration_days for p in parsed_packages]
    min_duration, max_duration = min(durations), max(durations)

    prefs = request.user_preferences or UserPreferences()
    weights = prefs.importance_weights

    # Normalize weightings to ensure sum = 1.0
    weight_total = weights.price + weights.rating + weights.inclusions + weights.duration
    w_price = weights.price / weight_total if weight_total > 0 else 0.30
    w_rating = weights.rating / weight_total if weight_total > 0 else 0.25
    w_inclusions = weights.inclusions / weight_total if weight_total > 0 else 0.25
    w_duration = weights.duration / weight_total if weight_total > 0 else 0.20

    compared_packages: List[ComparedPackage] = []
    scores_dict: Dict[int, float] = {}

    for pkg in parsed_packages:
        # 1. Price Score
        if max_price == min_price:
            price_score = 100.0
        else:
            price_score = 100.0 * (max_price - pkg.total_price) / (max_price - min_price)

        if prefs.max_budget is not None and prefs.max_budget > 0:
            if pkg.total_price <= prefs.max_budget:
                savings_ratio = (prefs.max_budget - pkg.total_price) / prefs.max_budget
                price_score = min(100.0, price_score * 0.7 + (1.0 + savings_ratio) * 30.0)
            else:
                overage_ratio = (pkg.total_price - prefs.max_budget) / prefs.max_budget
                price_score = max(0.0, price_score - (overage_ratio * 50.0))

        # 2. Rating Score
        rating_score = (pkg.rating / 5.0) * 100.0
        if prefs.min_rating is not None:
            if pkg.rating >= prefs.min_rating:
                rating_score = min(100.0, rating_score + 5.0)
            else:
                rating_score = max(0.0, rating_score - 20.0)

        # 3. Inclusions Score
        base_inclusions_score = 60.0 + min(40.0, len(pkg.included_attractions) * 8.0)
        if prefs.required_inclusions:
            pkg_text = (
                " ".join(pkg.included_attractions) + " " + pkg.meals + " " + pkg.accommodation + " " + pkg.transport
            ).lower()
            matched = sum(1 for req in prefs.required_inclusions if req.lower() in pkg_text)
            match_ratio = matched / len(prefs.required_inclusions)
            inclusions_score = (base_inclusions_score * 0.5) + (match_ratio * 50.0)
        else:
            inclusions_score = base_inclusions_score
        inclusions_score = min(100.0, max(0.0, inclusions_score))

        # 4. Duration Score
        if prefs.preferred_duration_days is not None:
            diff = abs(pkg.duration_days - prefs.preferred_duration_days)
            duration_score = max(0.0, 100.0 - (diff * 25.0))
        else:
            if max_duration == min_duration:
                duration_score = 85.0
            else:
                duration_score = 70.0 + 30.0 * (pkg.duration_days - min_duration) / (max_duration - min_duration)

        # 5. Preference Match Score
        pref_match_elements = []
        if prefs.max_budget is not None:
            pref_match_elements.append(100.0 if pkg.total_price <= prefs.max_budget else 40.0)
        if prefs.preferred_duration_days is not None:
            pref_match_elements.append(max(0.0, 100.0 - abs(pkg.duration_days - prefs.preferred_duration_days) * 25.0))
        if prefs.min_rating is not None:
            pref_match_elements.append(100.0 if pkg.rating >= prefs.min_rating else 30.0)
        if prefs.required_inclusions:
            pkg_text = (" ".join(pkg.included_attractions) + " " + pkg.meals).lower()
            matched = sum(1 for req in prefs.required_inclusions if req.lower() in pkg_text)
            pref_match_elements.append((matched / len(prefs.required_inclusions)) * 100.0)

        pref_match_score = (
            sum(pref_match_elements) / len(pref_match_elements)
            if pref_match_elements
            else ((price_score + rating_score + inclusions_score) / 3.0)
        )

        overall_score = round(
            (price_score * w_price) +
            (rating_score * w_rating) +
            (inclusions_score * w_inclusions) +
            (duration_score * w_duration),
            1
        )
        scores_dict[pkg.id] = overall_score

        # Highlights & Drawbacks
        highlights = []
        drawbacks = []

        if pkg.total_price == min_price:
            highlights.append(f"Lowest price in comparison cohort (₹{pkg.total_price:,.0f})")
        elif pkg.total_price == max_price and len(parsed_packages) > 2:
            drawbacks.append(f"Highest price among compared packages (₹{pkg.total_price:,.0f})")

        if pkg.rating == max_rating:
            highlights.append(f"Top guest rating ({pkg.rating} ⭐)")
        elif pkg.rating == min_rating and len(parsed_packages) > 2:
            drawbacks.append(f"Lowest rating in cohort ({pkg.rating} ⭐)")

        if len(pkg.included_attractions) == max_attraction_cnt:
            highlights.append(f"Includes maximum attractions ({len(pkg.included_attractions)} sightseeing spots)")

        if "Breakfast" in pkg.meals or "Dinner" in pkg.meals:
            highlights.append(f"Meals included ({pkg.meals})")

        if pkg.duration_days == max_duration and max_duration > min_duration:
            highlights.append(f"Longest duration ({pkg.duration_days} Days / {pkg.duration_nights} Nights)")

        if pkg.exclusions:
            first_exclusion = pkg.exclusions[0]
            drawbacks.append(f"Excludes: {first_exclusion}")

        score_breakdown = ScoreBreakdown(
            price_score=round(price_score, 1),
            rating_score=round(rating_score, 1),
            inclusions_score=round(inclusions_score, 1),
            duration_score=round(duration_score, 1),
            preference_match_score=round(pref_match_score, 1),
        )

        compared_packages.append(
            ComparedPackage(
                package=pkg,
                overall_score=overall_score,
                score_breakdown=score_breakdown,
                badge=None,
                highlights=highlights,
                drawbacks=drawbacks,
            )
        )

    # Assign summary winners
    best_overall_pkg = max(parsed_packages, key=lambda p: scores_dict[p.id])
    cheapest_pkg = min(parsed_packages, key=lambda p: p.total_price)
    highest_rated_pkg = max(parsed_packages, key=lambda p: (p.rating, p.review_count))
    most_inclusive_pkg = max(parsed_packages, key=lambda p: len(p.included_attractions))

    # Assign badges
    for cp in compared_packages:
        pid = cp.package.id
        if pid == best_overall_pkg.id:
            cp.badge = "Best Overall"
        elif pid == cheapest_pkg.id:
            cp.badge = "Cheapest"
        elif pid == highest_rated_pkg.id:
            cp.badge = "Top Rated"
        elif pid == most_inclusive_pkg.id:
            cp.badge = "Most Inclusive"

    # Build attribute comparison matrix
    comparison_matrix: List[AttributeComparisonItem] = [
        AttributeComparisonItem(
            attribute_key="total_price",
            attribute_label="Total Price",
            values={str(p.id): f"₹{p.total_price:,.0f} {p.currency}" for p in parsed_packages},
            winner_package_id=cheapest_pkg.id,
            note=f"Package ID {cheapest_pkg.id} offers the lowest total price."
        ),
        AttributeComparisonItem(
            attribute_key="duration",
            attribute_label="Duration",
            values={str(p.id): f"{p.duration_days} Days / {p.duration_nights} Nights" for p in parsed_packages},
            winner_package_id=max(parsed_packages, key=lambda p: p.duration_days).id,
            note="Comparison of total trip length."
        ),
        AttributeComparisonItem(
            attribute_key="accommodation",
            attribute_label="Accommodation",
            values={str(p.id): p.accommodation for p in parsed_packages},
            winner_package_id=None,
            note="Hotel category, room type, and included amenities."
        ),
        AttributeComparisonItem(
            attribute_key="transport",
            attribute_label="Transport",
            values={str(p.id): p.transport for p in parsed_packages},
            winner_package_id=None,
            note="Transfer vehicle type and local sightseeing arrangements."
        ),
        AttributeComparisonItem(
            attribute_key="included_attractions",
            attribute_label="Included Attractions",
            values={str(p.id): p.included_attractions for p in parsed_packages},
            winner_package_id=most_inclusive_pkg.id,
            note=f"Package ID {most_inclusive_pkg.id} includes the highest number of attractions ({len(most_inclusive_pkg.included_attractions)})."
        ),
        AttributeComparisonItem(
            attribute_key="meals",
            attribute_label="Meals",
            values={str(p.id): p.meals for p in parsed_packages},
            winner_package_id=None,
            note="Included meal plan (e.g. Breakfast, Lunch, Dinner)."
        ),
        AttributeComparisonItem(
            attribute_key="rating",
            attribute_label="Rating",
            values={str(p.id): f"{p.rating} / 5.0 ({p.review_count} reviews)" for p in parsed_packages},
            winner_package_id=highest_rated_pkg.id,
            note=f"Package ID {highest_rated_pkg.id} has the highest rating ({highest_rated_pkg.rating} ⭐)."
        ),
        AttributeComparisonItem(
            attribute_key="exclusions",
            attribute_label="Exclusions",
            values={str(p.id): p.exclusions for p in parsed_packages},
            winner_package_id=None,
            note="Out-of-pocket costs and items not covered by package rate."
        ),
    ]

    summary = ComparisonSummary(
        best_overall_package_id=best_overall_pkg.id,
        cheapest_package_id=cheapest_pkg.id,
        highest_rated_package_id=highest_rated_pkg.id,
        most_inclusive_package_id=most_inclusive_pkg.id,
    )

    return PackageComparisonResponse(
        compared_packages=compared_packages,
        summary=summary,
        comparison_matrix=comparison_matrix,
        is_demo_data=True,
        disclaimer="Package prices, inclusions, and availability shown are demo data and not connected to a real-time live provider."
    )
