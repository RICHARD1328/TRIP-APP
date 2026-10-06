from typing import List, Optional, Tuple
from sqlalchemy.orm import Session
from app.models.itinerary import Trip, TripDay, ItineraryItem
from app.schemas.itinerary import TripCreate, ItineraryItemCreate


def create_trip(db: Session, user_id: int, trip_in: TripCreate, days_data: List[dict]) -> Trip:
    """
    Create a new trip with automatically generated day records.
    """
    db_trip = Trip(
        user_id=user_id,
        title=trip_in.title,
        destination=trip_in.destination,
        start_date=trip_in.start_date,
        end_date=trip_in.end_date,
        budget=trip_in.budget,
        status="planned"
    )
    db.add(db_trip)
    db.flush()  # Obtain db_trip.id

    for day_info in days_data:
        db_day = TripDay(
            trip_id=db_trip.id,
            day_number=day_info["day_number"],
            date=day_info["date"],
            title=day_info["title"]
        )
        db.add(db_day)

    db.commit()
    db.refresh(db_trip)
    return db_trip


def get_user_trips(db: Session, user_id: int) -> List[Trip]:
    """
    Fetch all trips belonging to the authenticated user.
    """
    return db.query(Trip).filter(Trip.user_id == user_id).order_by(Trip.created_at.desc()).all()


def get_trip_by_id_for_user(db: Session, trip_id: int, user_id: int) -> Optional[Trip]:
    """
    Fetch a specific trip belonging to the authenticated user.
    """
    return db.query(Trip).filter(Trip.id == trip_id, Trip.user_id == user_id).first()


def update_trip_for_user(db: Session, trip_id: int, user_id: int, update_data: dict) -> Optional[Trip]:
    """
    Update trip metadata for the authenticated user.
    """
    trip = get_trip_by_id_for_user(db, trip_id=trip_id, user_id=user_id)
    if not trip:
        return None

    for key, value in update_data.items():
        if value is not None and hasattr(trip, key):
            setattr(trip, key, value)

    db.commit()
    db.refresh(trip)
    return trip


def delete_trip_for_user(db: Session, trip_id: int, user_id: int) -> bool:
    """
    Delete a trip and all associated days & itinerary items for the authenticated user.
    """
    trip = get_trip_by_id_for_user(db, trip_id=trip_id, user_id=user_id)
    if not trip:
        return False

    db.delete(trip)
    db.commit()
    return True


def add_item_to_day_for_user(
    db: Session,
    trip_id: int,
    day_id: int,
    user_id: int,
    item_in: ItineraryItemCreate
) -> Optional[ItineraryItem]:
    """
    Add a new itinerary item to a specific trip day, verifying user ownership.
    """
    trip = get_trip_by_id_for_user(db, trip_id=trip_id, user_id=user_id)
    if not trip:
        return None

    day = db.query(TripDay).filter(TripDay.id == day_id, TripDay.trip_id == trip_id).first()
    if not day:
        return None

    db_item = ItineraryItem(
        trip_day_id=day.id,
        place_id=item_in.place_id,
        title=item_in.title,
        description=item_in.description,
        start_time=item_in.start_time,
        end_time=item_in.end_time,
        estimated_cost=item_in.estimated_cost,
        latitude=item_in.latitude,
        longitude=item_in.longitude,
        notes=item_in.notes
    )
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item


def update_item_for_user(
    db: Session,
    trip_id: int,
    item_id: int,
    user_id: int,
    update_data: dict
) -> Optional[ItineraryItem]:
    """
    Update an existing itinerary item, verifying user ownership.
    """
    trip = get_trip_by_id_for_user(db, trip_id=trip_id, user_id=user_id)
    if not trip:
        return None

    item = (
        db.query(ItineraryItem)
        .join(TripDay, ItineraryItem.trip_day_id == TripDay.id)
        .filter(ItineraryItem.id == item_id, TripDay.trip_id == trip_id)
        .first()
    )
    if not item:
        return None

    for key, value in update_data.items():
        if value is not None and hasattr(item, key):
            setattr(item, key, value)

    db.commit()
    db.refresh(item)
    return item


def delete_item_for_user(db: Session, trip_id: int, item_id: int, user_id: int) -> bool:
    """
    Delete an itinerary item, verifying user ownership.
    """
    trip = get_trip_by_id_for_user(db, trip_id=trip_id, user_id=user_id)
    if not trip:
        return False

    item = (
        db.query(ItineraryItem)
        .join(TripDay, ItineraryItem.trip_day_id == TripDay.id)
        .filter(ItineraryItem.id == item_id, TripDay.trip_id == trip_id)
        .first()
    )
    if not item:
        return False

    db.delete(item)
    db.commit()
    return True
