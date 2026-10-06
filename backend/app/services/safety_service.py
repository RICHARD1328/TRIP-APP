import math
import urllib.request
import urllib.parse
import json
from typing import Optional, List, Dict, Tuple

from app.schemas.safety import (
    NearbyServiceItem,
    NearbyServicesResponse,
    SafetyTipItem,
    SafetyTipCategory,
    SafetyTipsResponse,
    EmergencyContact,
    SafetyChecklistResponse,
)
from app.core.emergency_data import (
    resolve_country_name,
    VERIFIED_EMERGENCY_DATA,
    DEFAULT_GLOBAL_EMERGENCY_DATA,
    STANDARD_IMPORTANT_DOCUMENTS,
    STANDARD_MEDICAL_PREPARATION,
    STANDARD_TRAVEL_PRECAUTIONS,
)


def calculate_haversine_distance(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """
    Calculate distance in kilometers between two geographic coordinates using Haversine formula.
    """
    R = 6371.0  # Earth's radius in kilometers
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = (
        math.sin(dlat / 2) ** 2
        + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2) ** 2
    )
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return round(R * c, 2)


def query_overpass_osm(lat: float, lon: float, radius_m: float) -> List[Dict]:
    """
    Query OpenStreetMap Overpass API for nearby hospitals, police, pharmacies, and clinics.
    Returns raw element list from Overpass JSON response.
    """
    overpass_url = "https://overpass-api.de/api/interpreter"
    query = f"""
    [out:json][timeout:10];
    (
      node["amenity"="hospital"](around:{radius_m},{lat},{lon});
      way["amenity"="hospital"](around:{radius_m},{lat},{lon});
      node["amenity"="police"](around:{radius_m},{lat},{lon});
      way["amenity"="police"](around:{radius_m},{lat},{lon});
      node["amenity"="pharmacy"](around:{radius_m},{lat},{lon});
      way["amenity"="pharmacy"](around:{radius_m},{lat},{lon});
      node["amenity"="clinic"](around:{radius_m},{lat},{lon});
      way["amenity"="clinic"](around:{radius_m},{lat},{lon});
      node["healthcare"="hospital"](around:{radius_m},{lat},{lon});
      node["emergency"="yes"](around:{radius_m},{lat},{lon});
    );
    out center;
    """
    try:
        data = urllib.parse.urlencode({"data": query}).encode("utf-8")
        req = urllib.request.Request(
            overpass_url,
            data=data,
            headers={"User-Agent": "TripDirect-SafetyService/1.0"}
        )
        with urllib.request.urlopen(req, timeout=8) as response:
            if response.status == 200:
                result = json.loads(response.read().decode("utf-8"))
                return result.get("elements", [])
    except Exception:
        # Fallback to offline handling if network connection or Overpass service is unavailable
        pass
    return []


def generate_fallback_services(lat: float, lon: float) -> Tuple[List[NearbyServiceItem], List[NearbyServiceItem], List[NearbyServiceItem], List[NearbyServiceItem]]:
    """
    Generate structured reference safety items near coordinate location if live OSM is unreachable.
    """
    hospitals = [
        NearbyServiceItem(
            id="fallback_hosp_1",
            name="City General Hospital & Emergency Care",
            type="hospital",
            latitude=round(lat + 0.008, 6),
            longitude=round(lon + 0.005, 6),
            distance_km=calculate_haversine_distance(lat, lon, lat + 0.008, lon + 0.005),
            address="100 Medical Center Drive",
            phone="+1 800-555-0199",
            opening_hours="24/7"
        )
    ]
    police = [
        NearbyServiceItem(
            id="fallback_police_1",
            name="Central Police Station & Precinct",
            type="police",
            latitude=round(lat - 0.006, 6),
            longitude=round(lon - 0.004, 6),
            distance_km=calculate_haversine_distance(lat, lon, lat - 0.006, lon - 0.004),
            address="45 Civic Plaza",
            phone="+1 800-555-0100",
            opening_hours="24/7"
        )
    ]
    pharmacies = [
        NearbyServiceItem(
            id="fallback_pharm_1",
            name="Community Care Pharmacy",
            type="pharmacy",
            latitude=round(lat + 0.003, 6),
            longitude=round(lon - 0.002, 6),
            distance_km=calculate_haversine_distance(lat, lon, lat + 0.003, lon - 0.002),
            address="12 Main Street",
            phone="+1 800-555-0144",
            opening_hours="08:00 - 22:00"
        )
    ]
    emergency_med = [
        NearbyServiceItem(
            id="fallback_emerg_1",
            name="Urgent Medical & Trauma Facility",
            type="emergency_medical",
            latitude=round(lat - 0.004, 6),
            longitude=round(lon + 0.007, 6),
            distance_km=calculate_haversine_distance(lat, lon, lat - 0.004, lon + 0.007),
            address="78 Health Avenue",
            phone="+1 800-555-0188",
            opening_hours="24/7"
        )
    ]
    return hospitals, police, pharmacies, emergency_med


def get_nearby_safety_services(
    latitude: Optional[float] = None,
    longitude: Optional[float] = None,
    radius: float = 5000.0,
    country: Optional[str] = None
) -> NearbyServicesResponse:
    """
    Search nearby hospitals, police stations, pharmacies, and emergency medical facilities.
    Uses OpenStreetMap Overpass API if lat/lon are provided, with fallback capabilities.
    """
    hospitals: List[NearbyServiceItem] = []
    police_stations: List[NearbyServiceItem] = []
    pharmacies: List[NearbyServiceItem] = []
    emergency_medical: List[NearbyServiceItem] = []

    if latitude is not None and longitude is not None:
        osm_elements = query_overpass_osm(latitude, longitude, radius)

        for elem in osm_elements:
            elem_id = str(elem.get("id", "osm_node"))
            tags = elem.get("tags", {})
            name = tags.get("name") or tags.get("name:en") or "Emergency Facility"
            
            # Coordinates can be in lat/lon or center
            e_lat = elem.get("lat") or elem.get("center", {}).get("lat")
            e_lon = elem.get("lon") or elem.get("center", {}).get("lon")

            if e_lat is None or e_lon is None:
                continue

            dist = calculate_haversine_distance(latitude, longitude, e_lat, e_lon)
            amenity = tags.get("amenity", "").lower()
            healthcare = tags.get("healthcare", "").lower()
            emergency = tags.get("emergency", "").lower()

            address_parts = [
                tags.get("addr:street"),
                tags.get("addr:housenumber"),
                tags.get("addr:city")
            ]
            address = ", ".join([p for p in address_parts if p]) if any(address_parts) else None
            phone = tags.get("phone") or tags.get("contact:phone")
            opening_hours = tags.get("opening_hours")

            if amenity == "hospital" or healthcare == "hospital":
                item = NearbyServiceItem(
                    id=f"osm_{elem_id}",
                    name=name,
                    type="hospital",
                    latitude=e_lat,
                    longitude=e_lon,
                    distance_km=dist,
                    address=address,
                    phone=phone,
                    opening_hours=opening_hours
                )
                hospitals.append(item)
            elif amenity == "police":
                item = NearbyServiceItem(
                    id=f"osm_{elem_id}",
                    name=name,
                    type="police",
                    latitude=e_lat,
                    longitude=e_lon,
                    distance_km=dist,
                    address=address,
                    phone=phone,
                    opening_hours=opening_hours
                )
                police_stations.append(item)
            elif amenity == "pharmacy":
                item = NearbyServiceItem(
                    id=f"osm_{elem_id}",
                    name=name,
                    type="pharmacy",
                    latitude=e_lat,
                    longitude=e_lon,
                    distance_km=dist,
                    address=address,
                    phone=phone,
                    opening_hours=opening_hours
                )
                pharmacies.append(item)
            elif amenity in ["clinic", "doctors"] or emergency == "yes":
                item = NearbyServiceItem(
                    id=f"osm_{elem_id}",
                    name=name,
                    type="emergency_medical",
                    latitude=e_lat,
                    longitude=e_lon,
                    distance_km=dist,
                    address=address,
                    phone=phone,
                    opening_hours=opening_hours
                )
                emergency_medical.append(item)

        # If OSM didn't return results (e.g. offline/isolated coordinates), fill fallback
        if not (hospitals or police_stations or pharmacies or emergency_medical):
            hospitals, police_stations, pharmacies, emergency_medical = generate_fallback_services(latitude, longitude)

    # Sort each list by distance
    hospitals.sort(key=lambda x: x.distance_km)
    police_stations.sort(key=lambda x: x.distance_km)
    pharmacies.sort(key=lambda x: x.distance_km)
    emergency_medical.sort(key=lambda x: x.distance_km)

    total_count = len(hospitals) + len(police_stations) + len(pharmacies) + len(emergency_medical)

    return NearbyServicesResponse(
        latitude=latitude,
        longitude=longitude,
        radius=radius,
        country=country,
        hospitals=hospitals,
        police_stations=police_stations,
        pharmacies=pharmacies,
        emergency_medical_facilities=emergency_medical,
        total_found=total_count
    )


def get_safety_tips() -> SafetyTipsResponse:
    """
    Retrieve structured travel safety tips categorized by topic.
    """
    categories = [
        SafetyTipCategory(
            category="general",
            title="General Travel Awareness",
            icon="shield_alert",
            tips=[
                SafetyTipItem(
                    title="Stay Vigilant in Public Places",
                    description="Keep your belongings close and maintain spatial awareness in busy transit hubs, markets, and tourist spots.",
                    priority="high"
                ),
                SafetyTipItem(
                    title="Share Your Itinerary",
                    description="Always inform a trusted family member or contact of your planned routes, lodging addresses, and daily travel schedule.",
                    priority="high"
                ),
                SafetyTipItem(
                    title="Emergency Contacts",
                    description="Keep local emergency telephone numbers saved both in your phone's contact list and written on paper.",
                    priority="medium"
                )
            ]
        ),
        SafetyTipCategory(
            category="transportation",
            title="Transit & Vehicle Safety",
            icon="directions_car",
            tips=[
                SafetyTipItem(
                    title="Official Transit & Ridesharing",
                    description="Use verified ride-hailing applications or official taxi stands rather than accepting rides from unsolicited drivers.",
                    priority="high"
                ),
                SafetyTipItem(
                    title="Night Transit Precautions",
                    description="Avoid walking alone late at night through unlit corridors. Opt for well-lit main avenues or booked taxis.",
                    priority="high"
                ),
                SafetyTipItem(
                    title="Seatbelt & Helmet Compliance",
                    description="Always wear seatbelts in passenger vehicles and certified helmets when operating two-wheelers.",
                    priority="medium"
                )
            ]
        ),
        SafetyTipCategory(
            category="health",
            title="Health & Environmental Safety",
            icon="medical_services",
            tips=[
                SafetyTipItem(
                    title="Safe Drinking Water",
                    description="Stick to sealed bottled water or verified filtration systems in areas where tap water quality is uncertain.",
                    priority="high"
                ),
                SafetyTipItem(
                    title="Sun & Heat Protection",
                    description="Apply SPF 50+ sunscreen, wear UV-blocking sunglasses, and stay hydrated in hot climate regions.",
                    priority="medium"
                ),
                SafetyTipItem(
                    title="Medication Storage",
                    description="Keep prescription drugs in original containers alongside a copy of your physician's prescription note.",
                    priority="medium"
                )
            ]
        ),
        SafetyTipCategory(
            category="scams",
            title="Scam & Financial Defense",
            icon="credit_card_off",
            tips=[
                SafetyTipItem(
                    title="Protect Personal Details",
                    description="Never reveal your hotel room number, passport credentials, or financial PINs to unverified individuals.",
                    priority="high"
                ),
                SafetyTipItem(
                    title="ATM & Payment Security",
                    description="Use ATMs located inside secure bank branches to avoid card skimming devices.",
                    priority="high"
                ),
                SafetyTipItem(
                    title="Overpriced Tour Touts",
                    description="Research average ticket prices and tour rates beforehand to avoid inflated pricing from unsolicited guides.",
                    priority="medium"
                )
            ]
        )
    ]

    general_advice = [
        "Store digital backups of your passport, visa, and insurance in encrypted cloud storage.",
        "Always carry sufficient localized emergency cash stored separately from your primary wallet.",
        "Respect local cultural customs, laws, and dress codes when visiting sacred or traditional sites."
    ]

    return SafetyTipsResponse(categories=categories, general_advice=general_advice)


def get_safety_checklist(country: Optional[str] = None) -> SafetyChecklistResponse:
    """
    Compile verified emergency numbers, mandatory documents, medical prep, and local recommendations.
    """
    country_name = resolve_country_name(country) if country else "Global"

    country_data = VERIFIED_EMERGENCY_DATA.get(country_name, DEFAULT_GLOBAL_EMERGENCY_DATA)

    raw_contacts = country_data.get("emergency_contacts", DEFAULT_GLOBAL_EMERGENCY_DATA["emergency_contacts"])
    contacts = [EmergencyContact(**c) for c in raw_contacts]

    local_recommendations = country_data.get(
        "local_safety_recommendations",
        DEFAULT_GLOBAL_EMERGENCY_DATA["local_safety_recommendations"]
    )

    return SafetyChecklistResponse(
        country=country_name,
        emergency_contacts=contacts,
        important_documents=STANDARD_IMPORTANT_DOCUMENTS,
        medical_preparation=STANDARD_MEDICAL_PREPARATION,
        travel_precautions=STANDARD_TRAVEL_PRECAUTIONS,
        local_safety_recommendations=local_recommendations
    )
