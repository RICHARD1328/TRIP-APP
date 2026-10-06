from decimal import Decimal, ROUND_HALF_UP
from datetime import datetime, timezone
import logging
from typing import Dict, Optional
from sqlalchemy.orm import Session
from fastapi import HTTPException, status

from app.schemas.budget import (
    BudgetCalculateRequest,
    BudgetCalculateResponse,
    ExpenseCategoryBreakdown,
    BudgetSummary,
)
from app.repositories import budget_repository

logger = logging.getLogger(__name__)

TWOPLACES = Decimal("0.01")


def _to_decimal(val: float | int | str) -> Decimal:
    """
    Convert numeric input to Decimal quantized to 2 decimal places using ROUND_HALF_UP.
    Prevents floating point precision bugs in money arithmetic.
    """
    try:
        dec = Decimal(str(val))
        return dec.quantize(TWOPLACES, rounding=ROUND_HALF_UP)
    except Exception as exc:
        logger.error(f"Error converting value '{val}' to Decimal: {exc}")
        return Decimal("0.00")


def calculate_budget(db: Session, request: BudgetCalculateRequest) -> BudgetCalculateResponse:
    """
    Perform exact, decimal-safe budget calculation for travel expenses.
    Calculates subtotal, emergency reserve, total budget, per-person cost,
    per-day cost, and category percentage breakdown.
    """
    if request.number_of_travelers < 1:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="number_of_travelers must be at least 1."
        )

    if request.number_of_days < 1:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="number_of_days must be at least 1."
        )

    # 1. Decimal-Safe Conversion
    accom = _to_decimal(request.accommodation_cost)
    trans = _to_decimal(request.transport_cost)
    food = _to_decimal(request.food_cost)
    attr = _to_decimal(request.attraction_cost)
    shop = _to_decimal(request.shopping_cost)
    other = _to_decimal(request.other_cost)

    # 2. Subtotal & Reserve Arithmetic
    subtotal = accom + trans + food + attr + shop + other
    reserve = _to_decimal(request.emergency_reserve)
    total_budget = subtotal + reserve

    # 3. Per Person and Per Day Costs
    travelers_dec = Decimal(str(request.number_of_travelers))
    days_dec = Decimal(str(request.number_of_days))

    per_person = (total_budget / travelers_dec).quantize(TWOPLACES, rounding=ROUND_HALF_UP)
    per_day = (total_budget / days_dec).quantize(TWOPLACES, rounding=ROUND_HALF_UP)

    # 4. Category Percentages
    pct_dict: Dict[str, float] = {}
    if total_budget > Decimal("0.00"):
        pct_dict["accommodation"] = float((accom / total_budget * Decimal("100")).quantize(TWOPLACES, rounding=ROUND_HALF_UP))
        pct_dict["transport"] = float((trans / total_budget * Decimal("100")).quantize(TWOPLACES, rounding=ROUND_HALF_UP))
        pct_dict["food"] = float((food / total_budget * Decimal("100")).quantize(TWOPLACES, rounding=ROUND_HALF_UP))
        pct_dict["attraction"] = float((attr / total_budget * Decimal("100")).quantize(TWOPLACES, rounding=ROUND_HALF_UP))
        pct_dict["shopping"] = float((shop / total_budget * Decimal("100")).quantize(TWOPLACES, rounding=ROUND_HALF_UP))
        pct_dict["other"] = float((other / total_budget * Decimal("100")).quantize(TWOPLACES, rounding=ROUND_HALF_UP))
        pct_dict["emergency_reserve"] = float((reserve / total_budget * Decimal("100")).quantize(TWOPLACES, rounding=ROUND_HALF_UP))
    else:
        pct_dict = {
            "accommodation": 0.0,
            "transport": 0.0,
            "food": 0.0,
            "attraction": 0.0,
            "shopping": 0.0,
            "other": 0.0,
            "emergency_reserve": 0.0
        }

    breakdown = ExpenseCategoryBreakdown(
        accommodation_cost=float(accom),
        transport_cost=float(trans),
        food_cost=float(food),
        attraction_cost=float(attr),
        shopping_cost=float(shop),
        other_cost=float(other),
        subtotal=float(subtotal),
        emergency_reserve=float(reserve)
    )

    summary = BudgetSummary(
        subtotal=float(subtotal),
        emergency_reserve=float(reserve),
        total_budget=float(total_budget),
        per_person_cost=float(per_person),
        per_day_cost=float(per_day)
    )

    response = BudgetCalculateResponse(
        trip_id=request.trip_id,
        number_of_travelers=request.number_of_travelers,
        number_of_days=request.number_of_days,
        breakdown=breakdown,
        summary=summary,
        category_percentages=pct_dict,
        created_at=datetime.now(timezone.utc).isoformat()
    )

    # Automatically save if trip_id is present
    if request.trip_id:
        budget_repository.save_budget_record(
            db=db,
            trip_id=request.trip_id,
            number_of_travelers=request.number_of_travelers,
            number_of_days=request.number_of_days,
            subtotal=float(subtotal),
            emergency_reserve=float(reserve),
            total_budget=float(total_budget),
            per_person_cost=float(per_person),
            per_day_cost=float(per_day),
            budget_dict=response.model_dump()
        )

    return response


def get_budget_for_trip(db: Session, trip_id: str) -> Optional[dict]:
    """
    Retrieve saved budget record by trip_id.
    """
    return budget_repository.get_budget_by_trip_id(db=db, trip_id=trip_id)
