from typing import List, Optional
from fastapi import APIRouter, Query, status
from app.schemas.place import NearbyPlaceItem
from app.services import place_service

router = APIRouter()


@router.get("/nearby", response_model=List[NearbyPlaceItem], status_code=status.HTTP_200_OK)
def get_nearby_places(
    latitude: float = Query(..., ge=-90.0, le=90.0, description="Latitude coordinate"),
    longitude: float = Query(..., ge=-180.0, le=180.0, description="Longitude coordinate"),
    radius: int = Query(5000, ge=100, le=50000, description="Search radius in meters"),
    category: Optional[str] = Query(None, description="Category filter (e.g. attraction, hotel, restaurant, museum, park)")
):
    """
    Get nearby places from OpenStreetMap via Overpass API.
    GET /api/v1/places/nearby?latitude={lat}&longitude={lon}&radius={radius}&category={category}
    """
    return place_service.get_nearby_places_overpass(
        latitude=latitude,
        longitude=longitude,
        radius=radius,
        category=category
    )
