from typing import Dict, List, Optional, Any
from pydantic import BaseModel, Field


class LocationPoint(BaseModel):
    latitude: float = Field(..., description="Latitude coordinate")
    longitude: float = Field(..., description="Longitude coordinate")


class RouteMapResponse(BaseModel):
    distance: float = Field(..., description="Total route distance in meters")
    distance_km: float = Field(..., description="Total route distance in kilometers")
    duration: float = Field(..., description="Total estimated travel duration in seconds")
    duration_formatted: str = Field(..., description="Human-readable duration string (e.g. 25 mins or 2h 15m)")
    origin: LocationPoint = Field(..., description="Origin location coordinates")
    destination: LocationPoint = Field(..., description="Destination location coordinates")
    travel_mode: str = Field(..., description="Travel mode (walking, cycling, driving)")
    route_geometry: Optional[Dict[str, Any]] = Field(None, description="GeoJSON LineString geometry payload")
    provider_info: str = Field("OSRM (Open Source Routing Machine)", description="Routing infrastructure provider name")
