import json
from typing import Optional, List
from sqlalchemy.orm import Session
from app.models.trip import SavedTrip


def save_trip_plan(
    db: Session,
    trip_id: str,
    title: str,
    destination: str,
    start_date: str,
    end_date: str,
    duration_days: int,
    number_of_travelers: int,
    budget: float,
    estimated_total_cost: float,
    plan_dict: dict,
    user_id: Optional[int] = None
) -> SavedTrip:
    """Save generated trip plan to database."""
    existing = db.query(SavedTrip).filter(SavedTrip.trip_id == trip_id).first()
    if existing:
        if user_id is not None and existing.user_id is not None and existing.user_id != user_id:
            raise PermissionError("Access denied: You do not own this saved trip.")
        existing.title = title
        existing.destination = destination
        existing.start_date = start_date
        existing.end_date = end_date
        existing.duration_days = duration_days
        existing.number_of_travelers = number_of_travelers
        existing.budget = budget
        existing.estimated_total_cost = estimated_total_cost
        existing.itinerary_json = json.dumps(plan_dict)
        if user_id is not None:
            existing.user_id = user_id
        db.commit()
        db.refresh(existing)
        return existing

    saved_trip = SavedTrip(
        trip_id=trip_id,
        user_id=user_id,
        title=title,
        destination=destination,
        start_date=start_date,
        end_date=end_date,
        duration_days=duration_days,
        number_of_travelers=number_of_travelers,
        budget=budget,
        estimated_total_cost=estimated_total_cost,
        itinerary_json=json.dumps(plan_dict)
    )
    db.add(saved_trip)
    db.commit()
    db.refresh(saved_trip)
    return saved_trip


def list_user_trips(db: Session, user_id: Optional[int] = None) -> List[dict]:
    """Retrieve all saved trips belonging to a user (or all trips if unauthenticated)."""
    query = db.query(SavedTrip)
    if user_id is not None:
        query = query.filter(SavedTrip.user_id == user_id)
    records = query.order_by(SavedTrip.created_at.desc()).all()
    results = []
    for rec in records:
        try:
            data = json.loads(rec.itinerary_json)
            results.append(data)
        except Exception:
            results.append({
                "trip_id": rec.trip_id,
                "title": rec.title,
                "destination": rec.destination,
                "start_date": rec.start_date,
                "end_date": rec.end_date,
                "budget": rec.budget
            })
    return results


def get_saved_trip(db: Session, trip_id: str, user_id: Optional[int] = None) -> Optional[dict]:
    """Fetch saved trip plan by trip_id, optionally ensuring user ownership."""
    query = db.query(SavedTrip).filter(SavedTrip.trip_id == trip_id)
    saved_trip = query.first()
    if not saved_trip:
        return None

    if user_id is not None and saved_trip.user_id is not None and saved_trip.user_id != user_id:
        raise PermissionError("Access denied: You do not own this saved trip.")

    return json.loads(saved_trip.itinerary_json)


def update_saved_trip(db: Session, trip_id: str, plan_dict: dict, user_id: Optional[int] = None) -> Optional[dict]:
    """Update a saved trip's itinerary payload."""
    saved_trip = db.query(SavedTrip).filter(SavedTrip.trip_id == trip_id).first()
    if not saved_trip:
        return None

    if user_id is not None and saved_trip.user_id is not None and saved_trip.user_id != user_id:
        raise PermissionError("Access denied: You do not own this saved trip.")

    saved_trip.itinerary_json = json.dumps(plan_dict)
    db.commit()
    db.refresh(saved_trip)
    return json.loads(saved_trip.itinerary_json)


def delete_saved_trip(db: Session, trip_id: str, user_id: Optional[int] = None) -> bool:
    """Delete a saved trip by trip_id."""
    saved_trip = db.query(SavedTrip).filter(SavedTrip.trip_id == trip_id).first()
    if not saved_trip:
        return False

    if user_id is not None and saved_trip.user_id is not None and saved_trip.user_id != user_id:
        raise PermissionError("Access denied: You do not own this saved trip.")

    db.delete(saved_trip)
    db.commit()
    return True
