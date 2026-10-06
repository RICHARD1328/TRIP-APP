import math
import time
import logging
from typing import List, Dict, Tuple, Any, Optional
import httpx
from fastapi import HTTPException, status
from app.schemas.place import NearbyPlaceItem

logger = logging.getLogger(__name__)

OVERPASS_URL = "https://overpass-api.de/api/interpreter"
OVERPASS_USER_AGENT = "TripDirectBackend/1.0 (contact@tripdirect.com)"
CACHE_TTL_SECONDS = 900  # 15 minutes TTL

# In-memory query cache: {cache_key: (timestamp, List[NearbyPlaceItem])}
_place_cache: Dict[str, Tuple[float, List[NearbyPlaceItem]]] = {}

# Category tag mapping for Overpass QL
CATEGORY_TAG_MAP: Dict[str, List[Tuple[str, str, str]]] = {
    "attraction": [
        ("node", "tourism", "attraction"),
        ("way", "tourism", "attraction"),
        ("relation", "tourism", "attraction")
    ],
    "museum": [
        ("node", "tourism", "museum"),
        ("way", "tourism", "museum"),
        ("relation", "tourism", "museum")
    ],
    "park": [
        ("node", "leisure", "park"),
        ("way", "leisure", "park"),
        ("relation", "leisure", "park")
    ],
    "beach": [
        ("node", "natural", "beach"),
        ("way", "natural", "beach"),
        ("relation", "natural", "beach")
    ],
    "viewpoint": [
        ("node", "tourism", "viewpoint"),
        ("way", "tourism", "viewpoint"),
        ("relation", "tourism", "viewpoint")
    ],
    "restaurant": [
        ("node", "amenity", "restaurant"),
        ("way", "amenity", "restaurant"),
        ("relation", "amenity", "restaurant")
    ],
    "cafe": [
        ("node", "amenity", "cafe"),
        ("way", "amenity", "cafe"),
        ("relation", "amenity", "cafe")
    ],
    "hotel": [
        ("node", "tourism", "hotel"),
        ("way", "tourism", "hotel"),
        ("relation", "tourism", "hotel")
    ],
    "hospital": [
        ("node", "amenity", "hospital"),
        ("way", "amenity", "hospital"),
        ("relation", "amenity", "hospital")
    ],
    "police": [
        ("node", "amenity", "police"),
        ("way", "amenity", "police"),
        ("relation", "amenity", "police")
    ],
    "atm": [
        ("node", "amenity", "atm"),
        ("way", "amenity", "atm"),
        ("relation", "amenity", "atm")
    ],
    "transport": [
        ("node", "amenity", "bus_station"),
        ("way", "amenity", "bus_station"),
        ("node", "railway", "station"),
        ("way", "railway", "station"),
        ("node", "aeroway", "aerodrome"),
        ("way", "aeroway", "aerodrome")
    ]
}


def haversine_distance(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """
    Calculate Haversine distance between two lat/lon coordinates in meters.
    """
    r = 6371000.0  # Earth radius in meters
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lon2 - lon1)

    a = math.sin(delta_phi / 2.0) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2.0) ** 2
    c = 2.0 * math.atan2(math.sqrt(a), math.sqrt(1.0 - a))
    return round(r * c, 1)


def _get_from_cache(cache_key: str) -> Optional[List[NearbyPlaceItem]]:
    if cache_key in _place_cache:
        timestamp, results = _place_cache[cache_key]
        if time.time() - timestamp < CACHE_TTL_SECONDS:
            return results
        else:
            del _place_cache[cache_key]
    return None


def _set_in_cache(cache_key: str, results: List[NearbyPlaceItem]):
    _place_cache[cache_key] = (time.time(), results)


def build_overpass_query(lat: float, lon: float, radius: int, category: Optional[str]) -> str:
    """
    Construct a single efficient Overpass QL query string.
    """
    cat_clean = (category or "").strip().lower()
    tag_specs = CATEGORY_TAG_MAP.get(cat_clean)

    if not tag_specs:
        # Default union query for all popular place types if no specific category matched
        statements = f"""
        node["tourism"](around:{radius},{lat},{lon});
        way["tourism"](around:{radius},{lat},{lon});
        node["amenity"](around:{radius},{lat},{lon});
        way["amenity"](around:{radius},{lat},{lon});
        node["leisure"](around:{radius},{lat},{lon});
        way["leisure"](around:{radius},{lat},{lon});
        """
    else:
        statements = "\n".join(
            f'{elem}["{key}"="{val}"](around:{radius},{lat},{lon});'
            for elem, key, val in tag_specs
        )

    query = f"""[out:json][timeout:15];
(
{statements}
);
out center;
"""
    return query


def extract_image_url(tags: Dict[str, str]) -> Optional[str]:
    """
    Extract image URL from OSM tags if available.
    """
    if "image" in tags and tags["image"].startswith("http"):
        return tags["image"]
    if "wikimedia_commons" in tags:
        commons_val = tags["wikimedia_commons"]
        if commons_val.startswith("File:"):
            filename = commons_val.replace("File:", "").replace(" ", "_")
            return f"https://commons.wikimedia.org/wiki/Special:FilePath/{filename}"
        elif commons_val.startswith("Category:"):
            category_name = commons_val.replace("Category:", "").replace(" ", "_")
            return f"https://commons.wikimedia.org/wiki/Category:{category_name}"
    if "wikipedia" in tags and ":" in tags["wikipedia"]:
        lang, title = tags["wikipedia"].split(":", 1)
        return f"https://{lang}.wikipedia.org/wiki/{title.replace(' ', '_')}"
    return None


def extract_address(tags: Dict[str, str]) -> str:
    """
    Construct human-readable address from OSM address tags.
    """
    street = tags.get("addr:street", "")
    housenumber = tags.get("addr:housenumber", "")
    suburb = tags.get("addr:suburb") or tags.get("addr:neighbourhood", "")
    city = tags.get("addr:city") or tags.get("addr:town") or tags.get("addr:village", "")

    parts = []
    if housenumber and street:
        parts.append(f"{housenumber} {street}")
    elif street:
        parts.append(street)

    if suburb:
        parts.append(suburb)
    if city:
        parts.append(city)

    return ", ".join(parts) if parts else tags.get("address", "")


def get_nearby_places_overpass(
    latitude: float,
    longitude: float,
    radius: int = 5000,
    category: Optional[str] = None
) -> List[NearbyPlaceItem]:
    """
    Fetch nearby places from OpenStreetMap via Overpass API.
    Validates coordinates, builds efficient query, calculates distance,
    handles errors and caching.
    """
    if not (-90.0 <= latitude <= 90.0):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Latitude must be between -90.0 and 90.0 degrees."
        )

    if not (-180.0 <= longitude <= 180.0):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Longitude must be between -180.0 and 180.0 degrees."
        )

    # Sanitize radius
    radius = max(100, min(radius, 50000))
    cat_key = (category or "all").strip().lower()

    # Cache key based on rounded coordinates (~100m grid), radius, and category
    cache_key = f"{round(latitude, 3)}:{round(longitude, 3)}:{radius}:{cat_key}"
    cached_results = _get_from_cache(cache_key)
    if cached_results is not None:
        return cached_results

    overpass_ql = build_overpass_query(lat=latitude, lon=longitude, radius=radius, category=category)
    headers = {
        "User-Agent": OVERPASS_USER_AGENT,
        "Accept": "application/json"
    }

    try:
        with httpx.Client(timeout=12.0) as client:
            response = client.post(OVERPASS_URL, data={"data": overpass_ql}, headers=headers)
            response.raise_for_status()
            payload = response.json()
    except httpx.TimeoutException:
        logger.error(f"Overpass API timeout for coordinates ({latitude}, {longitude})")
        return []
    except httpx.HTTPStatusError as exc:
        logger.error(f"Overpass HTTP error ({exc.response.status_code}) for coordinates ({latitude}, {longitude})")
        return []
    except Exception as exc:
        logger.error(f"Unexpected error querying Overpass: {exc}")
        return []

    elements = payload.get("elements", [])
    if not isinstance(elements, list):
        return []

    results: List[NearbyPlaceItem] = []
    for elem in elements:
        try:
            tags = elem.get("tags", {})
            name = tags.get("name") or tags.get("name:en") or tags.get("brand")
            if not name:
                continue  # Skip unnamed raw nodes/ways

            # Extract latitude/longitude from node or center of way/relation
            if elem.get("type") == "node":
                item_lat = float(elem.get("lat", 0.0))
                item_lon = float(elem.get("lon", 0.0))
            else:
                center = elem.get("center", {})
                item_lat = float(center.get("lat", 0.0))
                item_lon = float(center.get("lon", 0.0))

            if item_lat == 0.0 or item_lon == 0.0:
                continue

            dist_meters = haversine_distance(latitude, longitude, item_lat, item_lon)
            if dist_meters > radius:
                continue

            elem_type = elem.get("type", "node")
            elem_id = elem.get("id")
            place_id = f"{elem_type}/{elem_id}"

            # Determine category
            matched_cat = cat_key if cat_key != "all" and cat_key in CATEGORY_TAG_MAP else (
                tags.get("tourism") or tags.get("amenity") or tags.get("leisure") or tags.get("natural") or "place"
            )

            address_str = extract_address(tags)
            img_url = extract_image_url(tags)

            results.append(
                NearbyPlaceItem(
                    id=place_id,
                    name=name,
                    category=matched_cat,
                    latitude=item_lat,
                    longitude=item_lon,
                    address=address_str,
                    distance=dist_meters,
                    image_url=img_url
                )
            )
        except (KeyError, ValueError, TypeError) as parse_err:
            logger.warning(f"Error parsing Overpass element: {parse_err}")
            continue

    # Sort results by distance ascending
    results.sort(key=lambda item: item.distance)

    _set_in_cache(cache_key, results)
    return results
