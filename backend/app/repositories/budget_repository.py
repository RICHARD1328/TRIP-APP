import json
from typing import Optional
from sqlalchemy.orm import Session
from app.models.budget import SavedBudget


def save_budget_record(db: Session, trip_id: str, number_of_travelers: int, number_of_days: int, subtotal: float, emergency_reserve: float, total_budget: float, per_person_cost: float, per_day_cost: float, budget_dict: dict) -> SavedBudget:
    """
    Save calculated budget record to database.
    """
    existing = db.query(SavedBudget).filter(SavedBudget.trip_id == trip_id).first()
    if existing:
        existing.number_of_travelers = number_of_travelers
        existing.number_of_days = number_of_days
        existing.subtotal = subtotal
        existing.emergency_reserve = emergency_reserve
        existing.total_budget = total_budget
        existing.per_person_cost = per_person_cost
        existing.per_day_cost = per_day_cost
        existing.budget_json = json.dumps(budget_dict)
        db.commit()
        db.refresh(existing)
        return existing

    saved_budget = SavedBudget(
        trip_id=trip_id,
        number_of_travelers=number_of_travelers,
        number_of_days=number_of_days,
        subtotal=subtotal,
        emergency_reserve=emergency_reserve,
        total_budget=total_budget,
        per_person_cost=per_person_cost,
        per_day_cost=per_day_cost,
        budget_json=json.dumps(budget_dict)
    )
    db.add(saved_budget)
    db.commit()
    db.refresh(saved_budget)
    return saved_budget


def get_budget_by_trip_id(db: Session, trip_id: str) -> Optional[dict]:
    """
    Fetch saved budget breakdown by trip_id.
    """
    saved_budget = db.query(SavedBudget).filter(SavedBudget.trip_id == trip_id).first()
    if not saved_budget:
        return None
    return json.loads(saved_budget.budget_json)
