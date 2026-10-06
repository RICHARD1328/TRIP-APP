from datetime import datetime, timedelta
from typing import List, Optional
from sqlalchemy.orm import Session
from fastapi import HTTPException, status

from app.models.itinerary import Trip, ItineraryItem
from app.schemas.itinerary import (
    TripCreate,
    TripUpdate,
    ItineraryItemCreate,
    ItineraryItemUpdate,
)
from app.repositories import itinerary_repository


def create_new_trip(db: Session, user_id: int, trip_in: TripCreate) -> Trip:
    """
    Validate date inputs, generate trip days, and persist new trip in database.
    """
    try:
        start_dt = datetime.strptime(trip_in.start_date.strip(), "%Y-%m-%d")
        end_dt = datetime.strptime(trip_in.end_date.strip(), "%Y-%m-%d")
    except ValueError:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Dates must be in YYYY-MM-DD format."
        )

    if end_dt < start_dt:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="end_date must be on or after start_date."
        )

    duration_days = (end_dt - start_dt).days + 1
    days_data = []
    for day in range(1, duration_days + 1):
        cur_date = (start_dt + timedelta(days=day - 1)).strftime("%Y-%m-%d")
        days_data.append({
            "day_number": day,
            "date": cur_date,
            "title": f"Day {day}: {trip_in.destination}"
        })

    return itinerary_repository.create_trip(db=db, user_id=user_id, trip_in=trip_in, days_data=days_data)


def list_user_trips(db: Session, user_id: int) -> List[Trip]:
    """
    Retrieve all trips created by the authenticated user.
    """
    return itinerary_repository.get_user_trips(db=db, user_id=user_id)


def get_user_trip_by_id(db: Session, trip_id: int, user_id: int) -> Trip:
    """
    Retrieve a specific trip by ID for the authenticated user. Raises 404 if not found or unauthorized.
    """
    trip = itinerary_repository.get_trip_by_id_for_user(db=db, trip_id=trip_id, user_id=user_id)
    if not trip:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Itinerary with ID {trip_id} not found."
        )
    return trip


def update_user_trip(db: Session, trip_id: int, user_id: int, trip_update: TripUpdate) -> Trip:
    """
    Update trip parameters for the authenticated user.
    """
    update_data = trip_update.model_dump(exclude_unset=True)
    trip = itinerary_repository.update_trip_for_user(db=db, trip_id=trip_id, user_id=user_id, update_data=update_data)
    if not trip:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Itinerary with ID {trip_id} not found."
        )
    return trip


def delete_user_trip(db: Session, trip_id: int, user_id: int):
    """
    Delete a trip by ID for the authenticated user.
    """
    success = itinerary_repository.delete_trip_for_user(db=db, trip_id=trip_id, user_id=user_id)
    if not success:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Itinerary with ID {trip_id} not found."
        )


def add_item_to_day(
    db: Session,
    trip_id: int,
    day_id: int,
    user_id: int,
    item_in: ItineraryItemCreate
) -> ItineraryItem:
    """
    Add a new activity/item to a specific day in the user's trip.
    """
    item = itinerary_repository.add_item_to_day_for_user(
        db=db,
        trip_id=trip_id,
        day_id=day_id,
        user_id=user_id,
        item_in=item_in
    )
    if not item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Itinerary or Day ID not found for current user."
        )
    return item


def update_item(
    db: Session,
    trip_id: int,
    item_id: int,
    user_id: int,
    item_update: ItineraryItemUpdate
) -> ItineraryItem:
    """
    Update an itinerary item in the user's trip.
    """
    update_data = item_update.model_dump(exclude_unset=True)
    item = itinerary_repository.update_item_for_user(
        db=db,
        trip_id=trip_id,
        item_id=item_id,
        user_id=user_id,
        update_data=update_data
    )
    if not item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Itinerary item with ID {item_id} not found."
        )
    return item


def delete_item(db: Session, trip_id: int, item_id: int, user_id: int):
    """
    Delete an itinerary item from the user's trip.
    """
    success = itinerary_repository.delete_item_for_user(
        db=db,
        trip_id=trip_id,
        item_id=item_id,
        user_id=user_id
    )
    if not success:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Itinerary item with ID {item_id} not found."
        )
