from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.schemas.home import (
    HomeDashboardResponse,
    PaginatedPlaceResponse,
)
from app.services import home_service

router = APIRouter()


@router.get("", response_model=HomeDashboardResponse, status_code=status.HTTP_200_OK)
def get_home_dashboard(db: Session = Depends(get_db)):
    """
    Get aggregated Home Dashboard data including popular destinations, attractions,
    recommended hotels, featured places, and quick statistics.
    GET /api/v1/home
    """
    return home_service.fetch_home_dashboard(db=db)


@router.get("/popular-destinations", response_model=PaginatedPlaceResponse, status_code=status.HTTP_200_OK)
def get_popular_destinations(
    page: int = Query(1, ge=1, description="Page number"),
    size: int = Query(10, ge=1, le=100, description="Items per page"),
    db: Session = Depends(get_db)
):
    """
    Get paginated popular destinations.
    GET /api/v1/home/popular-destinations
    """
    return home_service.fetch_popular_destinations(db=db, page=page, size=size)


@router.get("/popular-attractions", response_model=PaginatedPlaceResponse, status_code=status.HTTP_200_OK)
def get_popular_attractions(
    page: int = Query(1, ge=1, description="Page number"),
    size: int = Query(10, ge=1, le=100, description="Items per page"),
    db: Session = Depends(get_db)
):
    """
    Get paginated popular attractions.
    GET /api/v1/home/popular-attractions
    """
    return home_service.fetch_popular_attractions(db=db, page=page, size=size)


@router.get("/recommended-hotels", response_model=PaginatedPlaceResponse, status_code=status.HTTP_200_OK)
def get_recommended_hotels(
    page: int = Query(1, ge=1, description="Page number"),
    size: int = Query(10, ge=1, le=100, description="Items per page"),
    db: Session = Depends(get_db)
):
    """
    Get paginated recommended hotels.
    GET /api/v1/home/recommended-hotels
    """
    return home_service.fetch_recommended_hotels(db=db, page=page, size=size)


@router.get("/featured", response_model=PaginatedPlaceResponse, status_code=status.HTTP_200_OK)
def get_featured_places(
    page: int = Query(1, ge=1, description="Page number"),
    size: int = Query(10, ge=1, le=100, description="Items per page"),
    db: Session = Depends(get_db)
):
    """
    Get paginated featured places.
    GET /api/v1/home/featured
    """
    return home_service.fetch_featured_places(db=db, page=page, size=size)
