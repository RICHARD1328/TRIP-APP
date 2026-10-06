from abc import ABC, abstractmethod
import math
import time
import logging
from typing import Dict, Tuple, Optional, Any
import httpx
from fastapi import HTTPException, status

from app.schemas.route import LocationPoint, RouteMapResponse

logger = logging.getLogger(__name__)

OSRM_BASE_URL = "https://router.project-osrm.org/route/v1"
OSRM_USER_AGENT = "TripDirectBackend/1.0 (contact@tripdirect.com)"
CACHE_TTL_SECONDS = 900  # 15 minutes TTL

# In-memory query cache: {cache_key: (timestamp, RouteMapResponse)}
_route_cache: Dict[str, Tuple[float, RouteMapResponse]] = {}


def haversine_distance(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """
    Calculate Haversine distance between two coordinates in meters.
    """
    r = 6371000.0  # Earth radius in meters
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lon2 - lon1)

    a = math.sin(delta_phi / 2.0) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2.0) ** 2
    c = 2.0 * math.atan2(math.sqrt(a), math.sqrt(1.0 - a))
    return round(r * c, 1)


def format_duration(duration_seconds: float) -> str:
    """
    Format duration seconds into human-readable string.
    """
    total_minutes = int(round(duration_seconds / 60.0))
    if total_minutes < 1:
        return "< 1 min"
    if total_minutes < 60:
        return f"{total_minutes} mins"
    hours = total_minutes // 60
    mins = total_minutes % 60
    if mins == 0:
        return f"{hours}h"
    return f"{hours}h {mins}m"


def _get_from_cache(cache_key: str) -> Optional[RouteMapResponse]:
    if cache_key in _route_cache:
        timestamp, response = _route_cache[cache_key]
        if time.time() - timestamp < CACHE_TTL_SECONDS:
            return response
        else:
            del _route_cache[cache_key]
    return None


def _set_in_cache(cache_key: str, response: RouteMapResponse):
    _route_cache[cache_key] = (time.time(), response)


class AbstractRouteProvider(ABC):
    """
    Abstract Base Class for Routing Infrastructure Providers (Strategy Pattern).
    Enables swapping between OSRM, GraphHopper, Valhalla, or self-hosted engines.
    """
    @abstractmethod
    def get_route(
        self,
        origin_lat: float,
        origin_lon: float,
        dest_lat: float,
        dest_lon: float,
        travel_mode: str = "driving"
    ) -> RouteMapResponse:
        pass


class OSRMRouteProvider(AbstractRouteProvider):
    """
    Open Source Routing Machine (OSRM) Provider implementation.
    Query public/self-hosted OSRM services using OpenStreetMap road network.
    """

    MODE_PROFILE_MAP = {
        "driving": "driving",
        "car": "driving",
        "walking": "foot",
        "foot": "foot",
        "cycling": "bike",
        "bike": "bike",
        "bicycle": "bike"
    }

    # Speed benchmarks in km/h for fallback duration estimation
    FALLBACK_SPEEDS = {
        "driving": 40.0,
        "cycling": 15.0,
        "walking": 4.5
    }

    def get_route(
        self,
        origin_lat: float,
        origin_lon: float,
        dest_lat: float,
        dest_lon: float,
        travel_mode: str = "driving"
    ) -> RouteMapResponse:
        """
        Query OSRM API for turn-by-turn route and GeoJSON geometry with fallback.
        """
        # Validate coordinate boundaries
        if not (-90.0 <= origin_lat <= 90.0) or not (-90.0 <= dest_lat <= 90.0):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Latitude must be between -90.0 and 90.0 degrees."
            )

        if not (-180.0 <= origin_lon <= 180.0) or not (-180.0 <= dest_lon <= 180.0):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Longitude must be between -180.0 and 180.0 degrees."
            )

        mode_clean = (travel_mode or "driving").strip().lower()
        if mode_clean not in self.MODE_PROFILE_MAP:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid travel_mode. Supported modes are: walking, cycling, driving."
            )

        profile = self.MODE_PROFILE_MAP[mode_clean]
        std_mode = "walking" if profile == "foot" else ("cycling" if profile == "bike" else "driving")

        # Cache key
        cache_key = f"{round(origin_lat, 4)}:{round(origin_lon, 4)}:{round(dest_lat, 4)}:{round(dest_lon, 4)}:{std_mode}"
        cached = _get_from_cache(cache_key)
        if cached is not None:
            return cached

        osrm_url = f"{OSRM_BASE_URL}/{profile}/{origin_lon},{origin_lat};{dest_lon},{dest_lat}"
        params = {
            "overview": "full",
            "geometries": "geojson"
        }
        headers = {
            "User-Agent": OSRM_USER_AGENT,
            "Accept": "application/json"
        }

        distance_meters = 0.0
        duration_sec = 0.0
        geometry: Optional[Dict[str, Any]] = None
        provider_name = "OSRM (Open Source Routing Machine)"

        try:
            with httpx.Client(timeout=8.0) as client:
                response = client.get(osrm_url, params=params, headers=headers)
                response.raise_for_status()
                data = response.json()

            routes = data.get("routes", [])
            if routes and isinstance(routes, list) and len(routes) > 0:
                first_route = routes[0]
                distance_meters = float(first_route.get("distance", 0.0))
                duration_sec = float(first_route.get("duration", 0.0))
                geometry = first_route.get("geometry")
        except (httpx.TimeoutException, httpx.HTTPStatusError, Exception) as exc:
            logger.warning(f"OSRM query failed or timed out ({exc}). Using direct Haversine fallback geometry.")
            # Fallback estimation using direct line segment and standard mode speeds
            distance_meters = haversine_distance(origin_lat, origin_lon, dest_lat, dest_lon)
            speed_kmh = self.FALLBACK_SPEEDS.get(std_mode, 40.0)
            duration_sec = round((distance_meters / 1000.0) / speed_kmh * 3600.0, 1)
            geometry = {
                "type": "LineString",
                "coordinates": [
                    [origin_lon, origin_lat],
                    [dest_lon, dest_lat]
                ]
            }
            provider_name = "OSRM Fallback (Direct Haversine Segment)"

        distance_km = round(distance_meters / 1000.0, 2)
        dur_str = format_duration(duration_sec)

        resp = RouteMapResponse(
            distance=round(distance_meters, 1),
            distance_km=distance_km,
            duration=round(duration_sec, 1),
            duration_formatted=dur_str,
            origin=LocationPoint(latitude=origin_lat, longitude=origin_lon),
            destination=LocationPoint(latitude=dest_lat, longitude=dest_lon),
            travel_mode=std_mode,
            route_geometry=geometry,
            provider_info=provider_name
        )

        _set_in_cache(cache_key, resp)
        return resp


def get_route_provider() -> AbstractRouteProvider:
    """
    Factory function for Routing Provider.
    Returns OSRMRouteProvider instance, easily swappable via config to GraphHopper or self-hosted OSRM.
    """
    return OSRMRouteProvider()
