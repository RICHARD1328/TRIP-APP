from typing import Optional
from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.schemas.package import (
    PackageResponse,
    PackageListResponse,
    ComparePackagesRequest,
    PackageComparisonResponse,
)
from app.services import package_service

router = APIRouter()


@router.get("", response_model=PackageListResponse, status_code=status.HTTP_200_OK)
def get_packages(
    destination: Optional[str] = Query(None, description="Filter packages by destination name"),
    max_price: Optional[float] = Query(None, ge=0.0, description="Filter packages by maximum price"),
    max_duration: Optional[int] = Query(None, ge=1, description="Filter packages by maximum duration in days"),
    min_rating: Optional[float] = Query(None, ge=0.0, le=5.0, description="Filter packages by minimum rating"),
    search: Optional[str] = Query(None, description="Search keyword in package title, destination, attractions"),
    limit: int = Query(20, ge=1, le=100, description="Page limit"),
    offset: int = Query(0, ge=0, description="Page offset"),
    db: Session = Depends(get_db),
):
    """
    Get travel packages catalog with optional search and filtering.
    GET /api/v1/packages
    """
    return package_service.list_packages(
        db=db,
        destination=destination,
        max_price=max_price,
        max_duration=max_duration,
        min_rating=min_rating,
        search=search,
        limit=limit,
        offset=offset,
    )


@router.get("/{package_id}", response_model=PackageResponse, status_code=status.HTTP_200_OK)
def get_package(
    package_id: int,
    db: Session = Depends(get_db),
):
    """
    Get single travel package by ID.
    GET /api/v1/packages/{id}
    """
    return package_service.get_package_detail(db=db, package_id=package_id)


@router.post("/compare", response_model=PackageComparisonResponse, status_code=status.HTTP_200_OK)
def compare_packages(
    request: ComparePackagesRequest,
    db: Session = Depends(get_db),
):
    """
    Compare selected travel packages based on price, duration, accommodation, transport,
    included attractions, meals, rating, exclusions, and user preferences.
    POST /api/v1/packages/compare
    """
    return package_service.compare_packages(db=db, request=request)
