from typing import List, Optional
from fastapi import APIRouter, Query, status
from app.schemas.transport import TransportSearchItem
from app.services import transport_service

router = APIRouter()


@router.get("/search", response_model=List[TransportSearchItem], status_code=status.HTTP_200_OK)
def search_transport(
    origin: Optional[str] = Query(None, description="Origin city or location"),
    destination: Optional[str] = Query(None, description="Destination city or location"),
    travel_date: Optional[str] = Query(None, description="Travel date (YYYY-MM-DD)"),
    passengers: int = Query(1, ge=1, le=50, description="Number of passengers"),
    transport_type: Optional[str] = Query(None, description="Filter by transport type: bus, train, flight, taxi, car (comma-separated for multiple)"),
    sort_by: str = Query("price", description="Sort option: price, duration")
):
    """
    Search transit options (bus, train, flight, taxi, car) with filtering, sorting, and source_type demo flag.
    GET /api/v1/transport/search
    """
    provider = transport_service.get_transport_provider()

    return provider.search_transport(
        origin=origin,
        destination=destination,
        travel_date=travel_date,
        passengers=passengers,
        transport_type=transport_type,
        sort_by=sort_by
    )
