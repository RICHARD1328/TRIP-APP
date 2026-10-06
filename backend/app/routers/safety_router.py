from typing import Optional
from fastapi import APIRouter, Query, status

from app.schemas.safety import (
    NearbyServicesResponse,
    SafetyTipsResponse,
    SafetyChecklistResponse,
)
from app.services import safety_service

router = APIRouter()


@router.get("/services", response_model=NearbyServicesResponse, status_code=status.HTTP_200_OK)
def get_nearby_safety_services(
    latitude: Optional[float] = Query(None, description="Geographic latitude coordinate"),
    longitude: Optional[float] = Query(None, description="Geographic longitude coordinate"),
    radius: float = Query(5000.0, ge=100.0, le=50000.0, description="Search radius in meters (default 5000m)"),
    country: Optional[str] = Query(None, description="Target country name or ISO code (e.g. India, US, UK)"),
):
    """
    Retrieve nearby safety services (hospitals, police stations, pharmacies, emergency medical facilities).
    Uses OpenStreetMap / Overpass API for location discovery with fallback support.
    GET /api/v1/safety/services
    """
    return safety_service.get_nearby_safety_services(
        latitude=latitude,
        longitude=longitude,
        radius=radius,
        country=country,
    )


@router.get("/tips", response_model=SafetyTipsResponse, status_code=status.HTTP_200_OK)
def get_safety_tips():
    """
    Retrieve comprehensive travel safety information and tips categorized by topic.
    GET /api/v1/safety/tips
    """
    return safety_service.get_safety_tips()


@router.get("/checklist", response_model=SafetyChecklistResponse, status_code=status.HTTP_200_OK)
def get_safety_checklist(
    country: Optional[str] = Query(None, description="Target country name or ISO code (e.g. India, US, UK)")
):
    """
    Retrieve travel safety checklist including verified emergency contact numbers,
    essential documents, medical prep, travel precautions, and local recommendations.
    GET /api/v1/safety/checklist
    """
    return safety_service.get_safety_checklist(country=country)
