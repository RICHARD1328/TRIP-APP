import pytest
from unittest.mock import patch, MagicMock
from fastapi.testclient import TestClient

from app.main import app
from app.services import external_services, place_scoring_service, budget_engine, itinerary_generator

client = TestClient(app)

# ==========================================
# 1. UNIT TESTS FOR EXTERNAL SERVICES & ALGORITHMS
# ==========================================

def test_haversine_distance():
    # Distance between Chennai (13.0827, 80.2707) and Ooty (11.4102, 76.6950) ~ 430 km
    dist = external_services.haversine_distance_km(13.0827, 80.2707, 11.4102, 76.6950)
    assert 400 < dist < 470


def test_place_scoring_service():
    place = {
        "name": "Ooty Lake & Boating",
        "category": "nature",
        "distance_km": 2.5,
        "rating": 4.7,
        "tags": {"nature": "yes"}
    }
    score, reasons = place_scoring_service.score_place(
        place=place,
        user_interests=["nature", "boating"],
        daily_budget_per_person=2000.0,
        travel_style="balanced"
    )
    assert 0.0 <= score <= 1.0
    assert len(reasons) > 0
    assert any("Nature" in r or "Boating" in r for r in reasons)


def test_budget_engine():
    budget_result = budget_engine.calculate_trip_budget(
        user_budget=20000.0,
        duration_days=4,
        travelers=2,
        transport_preference="car",
        accommodation_preference="budget"
    )
    assert "categories" in budget_result
    assert budget_result["total_estimated_cost"] > 0
    assert budget_result["cost_per_traveler"] > 0
    assert budget_result["is_live_pricing"] is False
    assert "user preferences" in budget_result["price_data_source"]


def test_itinerary_generator_weather_aware():
    ranked_places = [
        {"place_id": "p1", "name": "Government Museum", "category": "museum", "opening_hours": "09:00-17:00"},
        {"place_id": "p2", "name": "Doddabetta Viewpoint", "category": "viewpoint", "opening_hours": "08:00-18:00"}
    ]
    rainy_weather = [
        {
            "date": "2026-09-10",
            "temperature": {"min": 15, "max": 20, "unit": "°C"},
            "precipitation_probability": 80,
            "weather_condition": "Moderate Rain",
            "forecast_available": True
        }
    ]

    itinerary = itinerary_generator.build_dynamic_itinerary(
        destination_name="Ooty",
        start_date_str="2026-09-10",
        duration_days=1,
        ranked_places=ranked_places,
        weather_forecast=rainy_weather
    )
    assert len(itinerary) == 1
    slots = itinerary[0]["slots"]
    assert slots[0]["place_id"] == "p1"


# ==========================================
# 2. INTEGRATION TESTS FOR API ENDPOINTS
# ==========================================

@patch("app.services.external_services.geocode_location")
@patch("app.services.external_services.fetch_nearby_places_overpass")
@patch("app.services.external_services.fetch_weather_forecast")
@patch("app.services.external_services.calculate_osrm_route")
def test_ai_planner_plan_endpoint(mock_osrm, mock_weather, mock_overpass, mock_geocode):
    mock_geocode.side_effect = lambda loc: {
        "name": loc,
        "latitude": 11.4102 if "ooty" in loc.lower() else 13.0827,
        "longitude": 76.6950 if "ooty" in loc.lower() else 80.2707,
        "city": loc,
        "state": "Tamil Nadu",
        "country": "India",
        "fetched_at": "2026-08-27T12:00:00Z",
        "source": "Nominatim / OpenStreetMap",
        "status": "available"
    }

    mock_overpass.return_value = (
        [
            {
                "place_id": "osm_node_101",
                "name": "Ooty Botanical Gardens",
                "category": "park",
                "latitude": 11.415,
                "longitude": 76.700,
                "distance_km": 1.2,
                "opening_hours": "08:00-18:00",
                "image_url": None,
                "tags": {},
                "rating": 4.8,
                "source": "OpenStreetMap / Overpass API"
            }
        ],
        {"source": "Overpass API", "fetched_at": "2026-08-27T12:00:00Z", "status": "available", "cached": False}
    )

    mock_weather.return_value = (
        [
            {
                "date": "2026-09-10",
                "temperature": {"min": 14.5, "max": 22.0, "unit": "°C"},
                "precipitation_probability": 10,
                "weather_condition": "Clear sky",
                "wind_speed": 8.5,
                "forecast_available": True
            }
        ],
        {"source": "Open-Meteo", "fetched_at": "2026-08-27T12:00:00Z", "status": "available"}
    )

    mock_osrm.return_value = {
        "distance_km": 535.0,
        "duration_minutes": 480.0,
        "geometry": None,
        "source": "OSRM Routing",
        "status": "available"
    }

    payload = {
        "origin": "Chennai",
        "destination": "Ooty",
        "start_date": "2026-09-10",
        "end_date": "2026-09-12",
        "travelers": 2,
        "budget": 20000.0,
        "interests": ["nature", "photography"],
        "travel_style": "balanced",
        "transport_preference": "car",
        "accommodation_preference": "budget"
    }

    response = client.post("/api/v1/ai-planner/plan", json=payload)
    assert response.status_code == 200
    data = response.json()

    assert data["destination"]["name"] == "Ooty"
    assert len(data["recommended_places"]) > 0
    assert len(data["itinerary"]) == 3
    assert data["budget"]["user_budget"] == 20000.0
    assert data["live_data"] is True


def test_ai_planner_invalid_dates():
    payload = {
        "origin": "Chennai",
        "destination": "Ooty",
        "start_date": "2026-09-15",
        "end_date": "2026-09-10",
        "travelers": 2,
        "budget": 20000.0
    }
    response = client.post("/api/v1/ai-planner/plan", json=payload)
    assert response.status_code == 400


def test_ai_planner_negative_budget():
    payload = {
        "origin": "Chennai",
        "destination": "Ooty",
        "start_date": "2026-09-10",
        "end_date": "2026-09-12",
        "travelers": 2,
        "budget": -500.0
    }
    response = client.post("/api/v1/ai-planner/plan", json=payload)
    assert response.status_code == 422


def test_ai_planner_zero_travelers():
    payload = {
        "origin": "Chennai",
        "destination": "Ooty",
        "start_date": "2026-09-10",
        "end_date": "2026-09-12",
        "travelers": 0,
        "budget": 20000.0
    }
    response = client.post("/api/v1/ai-planner/plan", json=payload)
    assert response.status_code == 422


def test_ai_planner_empty_destination():
    payload = {
        "origin": "Chennai",
        "destination": "  ",
        "start_date": "2026-09-10",
        "end_date": "2026-09-12",
        "travelers": 2,
        "budget": 20000.0
    }
    response = client.post("/api/v1/ai-planner/plan", json=payload)
    assert response.status_code in [400, 422]


@patch("app.services.external_services.geocode_location")
@patch("app.services.external_services.fetch_nearby_places_overpass")
@patch("app.services.external_services.fetch_weather_forecast")
@patch("app.services.external_services.calculate_osrm_route")
def test_ai_planner_api_failure_graceful_fallback(mock_osrm, mock_weather, mock_overpass, mock_geocode):
    mock_geocode.return_value = None
    mock_overpass.return_value = ([], {"source": "Overpass API", "fetched_at": "2026-08-27T12:00:00Z", "status": "unavailable"})
    mock_weather.return_value = ([], {"source": "Open-Meteo", "fetched_at": "2026-08-27T12:00:00Z", "status": "unavailable"})
    mock_osrm.return_value = {"distance_km": 400.0, "duration_minutes": 600.0, "geometry": None, "source": "Haversine"}

    payload = {
        "origin": "Chennai",
        "destination": "Ooty",
        "start_date": "2026-09-10",
        "end_date": "2026-09-12",
        "travelers": 2,
        "budget": 15000.0
    }

    response = client.post("/api/v1/ai-planner/plan", json=payload)
    assert response.status_code == 200
    data = response.json()

    assert data["live_data"] is False
    assert len(data["recommended_places"]) > 0


def test_saved_trips_crud():
    trip_payload = {
        "trip_id": "test_saved_trip_999",
        "destination": {"name": "Ooty"},
        "budget": {"user_budget": 20000.0, "total_estimated_cost": 18000.0},
        "itinerary": [{"date": "2026-09-10"}, {"date": "2026-09-11"}]
    }

    save_resp = client.post("/api/v1/trips", json=trip_payload)
    assert save_resp.status_code == 201

    get_resp = client.get("/api/v1/trips/test_saved_trip_999")
    assert get_resp.status_code == 200
    assert get_resp.json()["trip_id"] == "test_saved_trip_999"

    list_resp = client.get("/api/v1/trips")
    assert list_resp.status_code == 200

    del_resp = client.delete("/api/v1/trips/test_saved_trip_999")
    assert del_resp.status_code == 200
