import time
import math
import logging
from typing import Dict, Any, List, Optional, Tuple
import httpx

logger = logging.getLogger("tripdirect.external_services")

# In-memory TTL cache dictionary: { cache_key: (timestamp, data) }
_CACHE: Dict[str, Tuple[float, Any]] = {}

USER_AGENT = "TripDirect/1.0 (contact@tripdirect.com)"
HEADERS = {"User-Agent": USER_AGENT}


def get_cached(key: str, max_age_seconds: int) -> Optional[Any]:
    if key in _CACHE:
        timestamp, data = _CACHE[key]
        if time.time() - timestamp < max_age_seconds:
            logger.info(f"Cache hit for key: {key}")
            return data
    return None


def set_cached(key: str, data: Any) -> None:
    _CACHE[key] = (time.time(), data)


def haversine_distance_km(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """Calculate the great-circle distance between two points in km using Haversine formula."""
    R = 6371.0  # Earth radius in kilometers
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat / 2) ** 2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return round(R * c, 2)


# ==========================================
# 1. NOMINATIM GEOCODING SERVICE
# ==========================================

def geocode_location(location_name: str) -> Optional[Dict[str, Any]]:
    """
    Resolve location name to lat/lon, city, state, country using Nominatim API.
    """
    loc_clean = location_name.strip().lower()
    cache_key = f"nominatim_{loc_clean}"
    cached = get_cached(cache_key, max_age_seconds=86400)
    if cached is not None:
        return cached

    url = "https://nominatim.openstreetmap.org/search"
    params = {
        "q": location_name,
        "format": "json",
        "addressdetails": 1,
        "limit": 5
    }

    try:
        with httpx.Client(timeout=10.0, headers=HEADERS) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            results = response.json()

        if not results:
            logger.warning(f"Nominatim found no results for location: {location_name}")
            return None

        best = results[0]
        address = best.get("address", {})
        city = (
            address.get("city")
            or address.get("town")
            or address.get("village")
            or address.get("municipality")
            or address.get("county")
            or best.get("name", location_name)
        )
        state = address.get("state") or address.get("region") or ""
        country = address.get("country") or ""

        resolved = {
            "name": best.get("name") or location_name,
            "display_name": best.get("display_name", ""),
            "latitude": float(best["lat"]),
            "longitude": float(best["lon"]),
            "city": city,
            "state": state,
            "country": country,
            "fetched_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
            "source": "Nominatim / OpenStreetMap",
            "status": "available"
        }

        set_cached(cache_key, resolved)
        return resolved

    except Exception as e:
        logger.error(f"Error calling Nominatim API for '{location_name}': {e}")
        return None


# ==========================================
# 2. OVERPASS NEARBY PLACES DISCOVERY SERVICE
# ==========================================

WMO_WEATHER_CODES = {
    0: "Clear sky",
    1: "Mainly clear",
    2: "Partly cloudy",
    3: "Overcast",
    45: "Fog",
    48: "Depositing rime fog",
    51: "Light drizzle",
    53: "Moderate drizzle",
    55: "Dense drizzle",
    61: "Slight rain",
    63: "Moderate rain",
    65: "Heavy rain",
    71: "Slight snow fall",
    73: "Moderate snow fall",
    75: "Heavy snow fall",
    80: "Slight rain showers",
    81: "Moderate rain showers",
    82: "Violent rain showers",
    95: "Thunderstorm",
    96: "Thunderstorm with slight hail",
    99: "Thunderstorm with heavy hail"
}


def map_osm_category(tags: Dict[str, str]) -> str:
    """Map raw OSM tags to standard application categories."""
    tourism = tags.get("tourism", "").lower()
    amenity = tags.get("amenity", "").lower()
    leisure = tags.get("leisure", "").lower()
    historic = tags.get("historic", "").lower()
    natural = tags.get("natural", "").lower()

    if tourism in ["viewpoint", "artwork"]:
        return "viewpoint"
    if tourism in ["museum", "gallery"]:
        return "museum"
    if tourism in ["attraction", "theme_park", "zoo"]:
        return "attraction"
    if leisure in ["park", "garden"]:
        return "park"
    if leisure in ["nature_reserve"] or natural in ["peak", "wood", "water"]:
        return "nature"
    if historic in ["monument", "castle", "ruins", "memorial"]:
        return "historic"
    if amenity in ["place_of_worship"]:
        return "temple"
    if amenity in ["restaurant", "fast_food"]:
        return "restaurant"
    if amenity in ["cafe"]:
        return "cafe"
    if amenity in ["hospital", "clinic"]:
        return "hospital"
    if amenity in ["police"]:
        return "police"
    if amenity in ["pharmacy"]:
        return "pharmacy"
    if amenity in ["marketplace"] or tags.get("shop"):
        return "shopping"

    return "attraction"


def fetch_nearby_places_overpass(lat: float, lon: float, radius_meters: int = 15000) -> Tuple[List[Dict[str, Any]], Dict[str, Any]]:
    """
    Fetch nearby attractions, restaurants, parks, etc., using Overpass API around (lat, lon).
    Returns (places_list, source_metadata).
    """
    now_iso = time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    cache_key = f"overpass_{round(lat, 3)}_{round(lon, 3)}_{radius_meters}"
    cached = get_cached(cache_key, max_age_seconds=43200)
    if cached is not None:
        return cached, {"source": "Overpass API", "fetched_at": now_iso, "status": "available", "cached": True}

    overpass_url = "https://overpass-api.de/api/interpreter"
    query = f"""
    [out:json][timeout:25];
    (
      node["tourism"~"attraction|museum|viewpoint|zoo|theme_park"](around:{radius_meters},{lat},{lon});
      way["tourism"~"attraction|museum|viewpoint|zoo|theme_park"](around:{radius_meters},{lat},{lon});
      node["amenity"~"restaurant|cafe|place_of_worship|hospital|police|pharmacy"](around:{radius_meters},{lat},{lon});
      way["amenity"~"restaurant|cafe|place_of_worship|hospital|police|pharmacy"](around:{radius_meters},{lat},{lon});
      node["leisure"~"park|garden|nature_reserve"](around:{radius_meters},{lat},{lon});
      way["leisure"~"park|garden|nature_reserve"](around:{radius_meters},{lat},{lon});
      node["historic"](around:{radius_meters},{lat},{lon});
      way["historic"](around:{radius_meters},{lat},{lon});
    );
    out center 60;
    """

    try:
        with httpx.Client(timeout=20.0, headers=HEADERS) as client:
            resp = client.post(overpass_url, data={"data": query})
            resp.raise_for_status()
            data = resp.json()

        elements = data.get("elements", [])
        places = []

        for elem in elements:
            tags = elem.get("tags", {})
            name = tags.get("name")
            if not name:
                continue

            elem_lat = elem.get("lat") or (elem.get("center", {}).get("lat") if "center" in elem else None)
            elem_lon = elem.get("lon") or (elem.get("center", {}).get("lon") if "center" in elem else None)

            if elem_lat is None or elem_lon is None:
                continue

            dist_km = haversine_distance_km(lat, lon, elem_lat, elem_lon)
            category = map_osm_category(tags)

            image_url = tags.get("image") or tags.get("wikimedia_commons") or None
            if image_url and image_url.startswith("File:"):
                file_name = image_url.replace("File:", "").strip().replace(" ", "_")
                image_url = f"https://commons.wikimedia.org/wiki/Special:FilePath/{file_name}"

            website = tags.get("website") or tags.get("contact:website") or None
            phone = tags.get("phone") or tags.get("contact:phone") or None
            opening_hours = tags.get("opening_hours") or None

            address_parts = [tags.get("addr:housenumber"), tags.get("addr:street"), tags.get("addr:city")]
            address = ", ".join([p for p in address_parts if p]) or None

            place_item = {
                "place_id": f"osm_{elem['type']}_{elem['id']}",
                "name": name,
                "category": category,
                "latitude": float(elem_lat),
                "longitude": float(elem_lon),
                "distance_km": dist_km,
                "address": address,
                "website": website,
                "phone": phone,
                "opening_hours": opening_hours,
                "image_url": image_url,
                "tags": tags,
                "rating": 4.5,
                "source": "OpenStreetMap / Overpass API"
            }
            places.append(place_item)

        unique_places = {}
        for p in places:
            if p["name"] not in unique_places:
                unique_places[p["name"]] = p

        places_list = list(unique_places.values())
        set_cached(cache_key, places_list)

        meta = {
            "source": "Overpass API",
            "fetched_at": now_iso,
            "status": "available",
            "cached": False
        }
        return places_list, meta

    except Exception as e:
        logger.error(f"Error fetching Overpass places: {e}")
        meta = {
            "source": "Overpass API",
            "fetched_at": now_iso,
            "status": "unavailable",
            "cached": False,
            "error": str(e)
        }
        return [], meta


# ==========================================
# 3. OPEN-METEO WEATHER FORECAST SERVICE
# ==========================================

def fetch_weather_forecast(lat: float, lon: float, start_date_str: str, end_date_str: str) -> Tuple[List[Dict[str, Any]], Dict[str, Any]]:
    """
    Fetch weather forecast from Open-Meteo for requested date range.
    Returns (weather_daily_list, metadata).
    """
    now_iso = time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    cache_key = f"weather_{round(lat, 2)}_{round(lon, 2)}_{start_date_str}_{end_date_str}"
    cached = get_cached(cache_key, max_age_seconds=3600)
    if cached is not None:
        return cached, {"source": "Open-Meteo", "fetched_at": now_iso, "status": "available", "cached": True}

    url = "https://api.open-meteo.com/v1/forecast"
    params = {
        "latitude": lat,
        "longitude": lon,
        "daily": ["temperature_2m_max", "temperature_2m_min", "precipitation_probability_max", "weather_code", "wind_speed_10m_max"],
        "timezone": "auto",
        "start_date": start_date_str,
        "end_date": end_date_str
    }

    try:
        with httpx.Client(timeout=10.0, headers=HEADERS) as client:
            resp = client.get(url, params=params)
            
            if resp.status_code != 200:
                logger.info(f"Open-Meteo returned status {resp.status_code}. Dates likely out of forecast range.")
                meta = {
                    "source": "Open-Meteo",
                    "fetched_at": now_iso,
                    "status": "forecast_unavailable",
                    "reason": "Requested dates outside available 16-day forecast horizon."
                }
                return [], meta

            data = resp.json()

        daily = data.get("daily", {})
        dates = daily.get("time", [])
        max_temps = daily.get("temperature_2m_max", [])
        min_temps = daily.get("temperature_2m_min", [])
        precip_probs = daily.get("precipitation_probability_max", [])
        codes = daily.get("weather_code", [])
        winds = daily.get("wind_speed_10m_max", [])

        weather_list = []
        for i, d in enumerate(dates):
            wcode = codes[i] if i < len(codes) else 0
            condition = WMO_WEATHER_CODES.get(wcode, "Clear")

            item = {
                "date": d,
                "temperature": {
                    "min": min_temps[i] if i < len(min_temps) else 15.0,
                    "max": max_temps[i] if i < len(max_temps) else 25.0,
                    "unit": "°C"
                },
                "precipitation_probability": precip_probs[i] if i < len(precip_probs) else 0,
                "weather_condition": condition,
                "weather_code": wcode,
                "wind_speed": winds[i] if i < len(winds) else 10.0,
                "forecast_available": True
            }
            weather_list.append(item)

        set_cached(cache_key, weather_list)
        meta = {
            "source": "Open-Meteo",
            "fetched_at": now_iso,
            "status": "available",
            "cached": False
        }
        return weather_list, meta

    except Exception as e:
        logger.error(f"Error fetching Open-Meteo weather: {e}")
        meta = {
            "source": "Open-Meteo",
            "fetched_at": now_iso,
            "status": "unavailable",
            "error": str(e)
        }
        return [], meta


# ==========================================
# 4. OSRM ROUTING SERVICE
# ==========================================

def calculate_osrm_route(lat1: float, lon1: float, lat2: float, lon2: float) -> Dict[str, Any]:
    """
    Calculate driving distance, travel duration, and route geometry between two coordinates using OSRM.
    """
    cache_key = f"osrm_{round(lat1, 4)}_{round(lon1, 4)}_{round(lat2, 4)}_{round(lon2, 4)}"
    cached = get_cached(cache_key, max_age_seconds=86400)
    if cached is not None:
        return cached

    url = f"http://router.project-osrm.org/route/v1/driving/{lon1},{lat1};{lon2},{lat2}?overview=simplified&geometries=geojson"

    try:
        with httpx.Client(timeout=8.0, headers=HEADERS) as client:
            resp = client.get(url)
            resp.raise_for_status()
            data = resp.json()

        routes = data.get("routes", [])
        if routes:
            r = routes[0]
            distance_km = round(r.get("distance", 0) / 1000.0, 2)
            duration_minutes = round(r.get("duration", 0) / 60.0, 1)
            geometry = r.get("geometry")

            res = {
                "distance_km": distance_km,
                "duration_minutes": duration_minutes,
                "geometry": geometry,
                "source": "OSRM Routing",
                "status": "available"
            }
            set_cached(cache_key, res)
            return res

    except Exception as e:
        logger.warning(f"OSRM routing failed: {e}. Falling back to straight-line Haversine distance.")

    dist_km = haversine_distance_km(lat1, lon1, lat2, lon2)
    est_duration_minutes = round((dist_km / 40.0) * 60.0, 1)

    res = {
        "distance_km": dist_km,
        "duration_minutes": est_duration_minutes,
        "geometry": None,
        "source": "Haversine Estimation (OSRM Unavailable)",
        "status": "estimated"
    }
    return res
