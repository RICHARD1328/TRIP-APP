from typing import List
from fastapi import APIRouter, Depends, Query, Path, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.schemas.destination import DestinationSearchItem, DestinationDetailResponse
from app.services import destination_service

router = APIRouter()


@router.get("/search", response_model=List[DestinationSearchItem], status_code=status.HTTP_200_OK)
def search_destinations(
    q: str = Query(..., min_length=2, description="Search query term (minimum 2 characters)"),
    limit: int = Query(10, ge=1, le=50, description="Maximum results to return")
):
    """
    Search destinations using OpenStreetMap Nominatim API.
    GET /api/v1/destinations/search?q={query}
    """
    return destination_service.search_destinations_nominatim(query=q, limit=limit)


@router.get("/{destination_id}", response_model=DestinationDetailResponse, status_code=status.HTTP_200_OK)
def get_destination_by_id(
    destination_id: str = Path(..., description="Destination ID or slug (e.g. Munnar, Ooty, Coorg)"),
    db: Session = Depends(get_db)
):
    """
    Get detailed destination information including verified social media Reels ('social_content').
    GET /api/v1/destinations/{destination_id}
    """
    return destination_service.get_destination_details(db=db, destination_id=destination_id)
