import time
import logging
from typing import List, Dict, Tuple, Any, Optional
import httpx
from sqlalchemy.orm import Session
from fastapi import HTTPException, status

from app.schemas.destination import DestinationSearchItem, DestinationDetailResponse
from app.schemas.social_content_schemas import SocialContentSchema
from app.repositories import social_content_repository, place_repository
from app.services import external_services

logger = logging.getLogger(__name__)

# User-Agent strictly required by OpenStreetMap Nominatim Usage Policy
NOMINATIM_USER_AGENT = "TripDirectBackend/1.0 (contact@tripdirect.com)"
NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search"
CACHE_TTL_SECONDS = 900  # 15 minutes in-memory cache TTL

_query_cache: Dict[str, Tuple[float, List[DestinationSearchItem]]] = {}


def _get_from_cache(query_key: str) -> List[DestinationSearchItem] | None:
    if query_key in _query_cache:
        timestamp, results = _query_cache[query_key]
        if time.time() - timestamp < CACHE_TTL_SECONDS:
            return results
        else:
            del _query_cache[query_key]
    return None


def _set_in_cache(query_key: str, results: List[DestinationSearchItem]):
    _query_cache[query_key] = (time.time(), results)


def search_destinations_nominatim(query: str, limit: int = 10) -> List[DestinationSearchItem]:
    clean_query = query.strip()
    if len(clean_query) < 2:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Search query must be at least 2 characters long."
        )

    cache_key = f"{clean_query.lower()}:{limit}"
    cached_results = _get_from_cache(cache_key)
    if cached_results is not None:
        return cached_results

    params = {
        "q": clean_query,
        "format": "json",
        "addressdetails": 1,
        "limit": limit,
    }
    headers = {
        "User-Agent": NOMINATIM_USER_AGENT,
        "Accept": "application/json",
    }

    try:
        with httpx.Client(timeout=8.0) as client:
            response = client.get(NOMINATIM_SEARCH_URL, params=params, headers=headers)
            response.raise_for_status()
            data = response.json()
    except httpx.TimeoutException:
        logger.error(f"Nominatim API timeout for query: '{clean_query}'")
        return []
    except httpx.HTTPStatusError as exc:
        logger.error(f"Nominatim HTTP status error ({exc.response.status_code}) for query '{clean_query}'")
        return []
    except Exception as exc:
        logger.error(f"Unexpected error querying Nominatim for '{clean_query}': {exc}")
        return []

    if not isinstance(data, list):
        return []

    results: List[DestinationSearchItem] = []
    for item in data:
        try:
            address: Dict[str, Any] = item.get("address", {})
            display_name: str = item.get("display_name", "")

            city = (
                address.get("city") or
                address.get("town") or
                address.get("village") or
                address.get("municipality") or
                address.get("county") or
                ""
            )
            state = address.get("state") or address.get("state_district") or ""
            country = address.get("country") or ""

            place_name = (
                address.get("city") or
                address.get("town") or
                address.get("village") or
                address.get("tourism") or
                address.get("attraction") or
                (display_name.split(",")[0].strip() if display_name else clean_query)
            )

            place_type = item.get("type") or item.get("class") or "place"
            lat = float(item["lat"])
            lon = float(item["lon"])

            results.append(
                DestinationSearchItem(
                    place_name=place_name,
                    display_name=display_name,
                    latitude=lat,
                    longitude=lon,
                    country=country,
                    state=state,
                    city=city,
                    place_type=place_type,
                )
            )
        except (KeyError, ValueError, TypeError) as parse_err:
            logger.warning(f"Error parsing Nominatim item: {parse_err}")
            continue

    _set_in_cache(cache_key, results)
    return results


def get_destination_details(db: Session, destination_id: str) -> DestinationDetailResponse:
    """
    Get detailed destination information including verified social media content (Reels).
    """
    clean_id = destination_id.strip()

    # 1. Geocode / resolve coordinates
    geo = external_services.geocode_location(clean_id)
    dest_name = geo["name"] if geo else clean_id.capitalize()
    lat = geo["latitude"] if geo else (10.0889 if "munnar" in clean_id.lower() else (11.4102 if "ooty" in clean_id.lower() else 12.3375))
    lon = geo["longitude"] if geo else (77.0595 if "munnar" in clean_id.lower() else (76.6950 if "ooty" in clean_id.lower() else 75.8069))
    location_str = f"{geo.get('state', '')}, {geo.get('country', 'India')}".strip(", ") if geo else "India"

    # 2. Fetch nearby places / attractions
    places, _ = external_services.fetch_nearby_places_overpass(lat=lat, lon=lon, radius_meters=15000)
    if not places:
        db_items, _ = place_repository.get_popular_attractions(db, skip=0, limit=6)
        attractions_list = [
            {
                "id": p.id,
                "name": p.name,
                "location": p.location,
                "rating": p.rating,
                "image_url": p.image_url
            }
            for p in db_items
        ]
    else:
        attractions_list = [
            {
                "id": p["place_id"],
                "name": p["name"],
                "location": p["address"] or dest_name,
                "rating": p["rating"],
                "image_url": p.get("image_url") or ""
            }
            for p in places[:6]
        ]

    # 3. Fetch verified social content specifically for this destination
    social_db_items = social_content_repository.get_social_content_for_destination(db, destination_id=clean_id)
    if not social_db_items and dest_name.lower() != clean_id.lower():
        # Fallback query using resolved place name
        social_db_items = social_content_repository.get_social_content_for_destination(db, destination_id=dest_name)

    social_content_list = [
        SocialContentSchema(
            id=s.id,
            destination_id=s.destination_id,
            platform=s.platform,
            title=s.title,
            reel_url=s.reel_url,
            thumbnail_url=s.thumbnail_url,
            creator_name=s.creator_name,
            description=s.description,
            verified=s.verified,
            active=s.active
        )
        for s in social_db_items
    ]

    return DestinationDetailResponse(
        id=clean_id,
        name=dest_name,
        location=location_str,
        latitude=lat,
        longitude=lon,
        description=f"Explore the natural beauty, culture, and attractions of {dest_name}.",
        images=[],
        attractions=attractions_list,
        weather={"temperature": 22.0, "condition": "Pleasant"},
        social_content=social_content_list
    )
