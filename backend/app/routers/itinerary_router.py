from typing import List
from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.security import get_current_user
from app.models.user import User
from app.schemas.itinerary import (
    TripCreate,
    TripUpdate,
    TripResponse,
    ItineraryItemCreate,
    ItineraryItemUpdate,
    ItineraryItemResponse,
)
from app.services import itinerary_service

router = APIRouter()


@router.post("", response_model=TripResponse, status_code=status.HTTP_201_CREATED)
def create_itinerary(
    trip_in: TripCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Create a new itinerary for the authenticated user.
    POST /api/v1/itineraries
    """
    return itinerary_service.create_new_trip(db=db, user_id=current_user.id, trip_in=trip_in)


@router.get("", response_model=List[TripResponse], status_code=status.HTTP_200_OK)
def list_itineraries(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Get all itineraries belonging to the authenticated user.
    GET /api/v1/itineraries
    """
    return itinerary_service.list_user_trips(db=db, user_id=current_user.id)


@router.get("/{id}", response_model=TripResponse, status_code=status.HTTP_200_OK)
def get_itinerary(
    id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Get a specific itinerary by ID for the authenticated user.
    GET /api/v1/itineraries/{id}
    """
    return itinerary_service.get_user_trip_by_id(db=db, trip_id=id, user_id=current_user.id)


@router.put("/{id}", response_model=TripResponse, status_code=status.HTTP_200_OK)
def update_itinerary(
    id: int,
    trip_update: TripUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Update itinerary parameters for the authenticated user.
    PUT /api/v1/itineraries/{id}
    """
    return itinerary_service.update_user_trip(db=db, trip_id=id, user_id=current_user.id, trip_update=trip_update)


@router.delete("/{id}", status_code=status.HTTP_200_OK)
def delete_itinerary(
    id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Delete a specific itinerary for the authenticated user.
    DELETE /api/v1/itineraries/{id}
    """
    itinerary_service.delete_user_trip(db=db, trip_id=id, user_id=current_user.id)
    return {"message": "Itinerary deleted successfully."}


@router.post("/{id}/days/{day_id}/items", response_model=ItineraryItemResponse, status_code=status.HTTP_201_CREATED)
def add_itinerary_item(
    id: int,
    day_id: int,
    item_in: ItineraryItemCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Add a new activity item to a specific day in the authenticated user's itinerary.
    POST /api/v1/itineraries/{id}/days/{day_id}/items
    """
    return itinerary_service.add_item_to_day(
        db=db,
        trip_id=id,
        day_id=day_id,
        user_id=current_user.id,
        item_in=item_in
    )


@router.put("/{id}/items/{item_id}", response_model=ItineraryItemResponse, status_code=status.HTTP_200_OK)
def update_itinerary_item(
    id: int,
    item_id: int,
    item_update: ItineraryItemUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Update an existing activity item in the authenticated user's itinerary.
    PUT /api/v1/itineraries/{id}/items/{item_id}
    """
    return itinerary_service.update_item(
        db=db,
        trip_id=id,
        item_id=item_id,
        user_id=current_user.id,
        item_update=item_update
    )


@router.delete("/{id}/items/{item_id}", status_code=status.HTTP_200_OK)
def delete_itinerary_item(
    id: int,
    item_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Delete an activity item from the authenticated user's itinerary.
    DELETE /api/v1/itineraries/{id}/items/{item_id}
    """
    itinerary_service.delete_item(
        db=db,
        trip_id=id,
        item_id=item_id,
        user_id=current_user.id
    )
    return {"message": "Itinerary item deleted successfully."}
