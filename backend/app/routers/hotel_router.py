from typing import List, Optional
from fastapi import APIRouter, Query, status
from app.schemas.hotel import HotelSearchItem
from app.services import hotel_service

router = APIRouter()


@router.get("/search", response_model=List[HotelSearchItem], status_code=status.HTTP_200_OK)
def search_hotels(
    destination: Optional[str] = Query(None, description="Destination city or location name"),
    latitude: Optional[float] = Query(None, ge=-90.0, le=90.0, description="Latitude coordinate"),
    longitude: Optional[float] = Query(None, ge=-180.0, le=180.0, description="Longitude coordinate"),
    check_in: Optional[str] = Query(None, description="Check-in date (YYYY-MM-DD)"),
    check_out: Optional[str] = Query(None, description="Check-out date (YYYY-MM-DD)"),
    guests: int = Query(1, ge=1, le=20, description="Number of guests"),
    rooms: int = Query(1, ge=1, le=10, description="Number of rooms"),
    min_price: Optional[float] = Query(None, ge=0.0, description="Minimum price per night"),
    max_price: Optional[float] = Query(None, ge=0.0, description="Maximum price per night"),
    min_rating: Optional[float] = Query(None, ge=0.0, le=5.0, description="Minimum guest rating out of 5.0"),
    amenities: Optional[str] = Query(None, description="Comma-separated amenities (e.g. Free Wi-Fi,Pool,Spa)"),
    max_distance: Optional[float] = Query(None, ge=0.0, description="Maximum distance in km from coordinates"),
    sort_by: str = Query("price_low_to_high", description="Sort option: price_low_to_high, price_high_to_low, rating, distance")
):
    """
    Search hotels with filtering, sorting, and source_type demo flag.
    GET /api/v1/hotels/search
    """
    amenities_list = [a.strip() for a in amenities.split(",")] if amenities else None
    provider = hotel_service.get_hotel_provider()

    return provider.search_hotels(
        destination=destination,
        latitude=latitude,
        longitude=longitude,
        check_in=check_in,
        check_out=check_out,
        guests=guests,
        rooms=rooms,
        min_price=min_price,
        max_price=max_price,
        min_rating=min_rating,
        amenities=amenities_list,
        max_distance=max_distance,
        sort_by=sort_by
    )
