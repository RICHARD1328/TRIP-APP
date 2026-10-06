from fastapi import APIRouter, Query, status
from app.schemas.route import RouteMapResponse
from app.services import route_service

router = APIRouter()


@router.get("", response_model=RouteMapResponse, status_code=status.HTTP_200_OK)
def get_route(
    origin_latitude: float = Query(..., ge=-90.0, le=90.0, description="Origin latitude coordinate"),
    origin_longitude: float = Query(..., ge=-180.0, le=180.0, description="Origin longitude coordinate"),
    destination_latitude: float = Query(..., ge=-90.0, le=90.0, description="Destination latitude coordinate"),
    destination_longitude: float = Query(..., ge=-180.0, le=180.0, description="Destination longitude coordinate"),
    travel_mode: str = Query("driving", description="Travel mode: walking, cycling, driving")
):
    """
    Get turn-by-turn route, distance, duration, and GeoJSON geometry payload using OpenStreetMap-compatible routing (OSRM).
    GET /api/v1/routes
    """
    provider = route_service.get_route_provider()
    return provider.get_route(
        origin_lat=origin_latitude,
        origin_lon=origin_longitude,
        dest_lat=destination_latitude,
        dest_lon=destination_longitude,
        travel_mode=travel_mode
    )
