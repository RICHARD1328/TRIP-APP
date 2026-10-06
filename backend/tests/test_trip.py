import pytest


def test_plan_trip_valid(client):
    """
    Test POST /api/v1/trips/plan generates structured day-by-day itinerary and recommendations.
    """
    payload = {
        "destination": "Ooty",
        "start_date": "2026-09-01",
        "end_date": "2026-09-03",
        "budget": 15000.0,
        "number_of_travelers": 2,
        "interests": ["nature", "adventure", "food", "photography"],
        "travel_style": "balanced",
        "accommodation_preference": "hotel",
        "transport_preference": "bus"
    }

    response = client.post("/api/v1/trips/plan", json=payload)
    assert response.status_code == 200
    data = response.json()

    assert "trip_id" in data
    assert data["trip_id"].startswith("trip_plan_")

    # Trip Summary
    summary = data["trip_summary"]
    assert summary["destination"] == "Ooty"
    assert summary["duration_days"] == 3
    assert summary["number_of_travelers"] == 2
    assert "Transparent Weighted Scoring Algorithm" in summary["scoring_algorithm_used"]

    # Cost Breakdown
    breakdown = data["estimated_cost_breakdown"]
    assert breakdown["user_budget"] == 15000.0
    assert breakdown["total_estimated_cost"] > 0
    assert "is_within_budget" in breakdown

    # Daily Itinerary
    itinerary = data["daily_itinerary"]
    assert len(itinerary) == 3
    day1 = itinerary[0]
    assert day1["day_number"] == 1
    assert len(day1["slots"]) >= 3  # Morning, Afternoon, Evening (and Lunch)

    # Recommendations
    assert len(data["recommended_attractions"]) > 0
    assert len(data["hotel_suggestions"]) > 0
    assert len(data["transport_suggestions"]) > 0


def test_plan_trip_invalid_dates(client):
    """
    Test POST /api/v1/trips/plan with end_date before start_date returns 400 error.
    """
    payload = {
        "destination": "Ooty",
        "start_date": "2026-09-10",
        "end_date": "2026-09-05",
        "budget": 10000.0,
        "number_of_travelers": 1,
        "interests": ["nature"]
    }

    response = client.post("/api/v1/trips/plan", json=payload)
    assert response.status_code == 400
    assert "end_date must be on or after start_date" in response.json()["detail"]


def test_plan_trip_empty_destination(client):
    """
    Test POST /api/v1/trips/plan with empty destination returns 400 error.
    """
    payload = {
        "destination": "   ",
        "start_date": "2026-09-01",
        "end_date": "2026-09-02",
        "budget": 5000.0,
        "number_of_travelers": 1,
        "interests": ["nature"]
    }

    response = client.post("/api/v1/trips/plan", json=payload)
    assert response.status_code == 400


def test_save_and_retrieve_trip(client):
    """
    Test saving a generated trip plan and retrieving it by ID.
    """
    # 1. Generate plan
    plan_payload = {
        "destination": "Munnar",
        "start_date": "2026-10-01",
        "end_date": "2026-10-02",
        "budget": 8000.0,
        "number_of_travelers": 1,
        "interests": ["nature", "photography"]
    }
    plan_resp = client.post("/api/v1/trips/plan", json=plan_payload)
    assert plan_resp.status_code == 200
    plan_data = plan_resp.json()
    trip_id = plan_data["trip_id"]

    # 2. Save plan
    save_resp = client.post("/api/v1/trips/save", json={"trip_plan": plan_data})
    assert save_resp.status_code == 201
    assert save_resp.json()["trip_id"] == trip_id

    # 3. Retrieve saved plan
    get_resp = client.get(f"/api/v1/trips/{trip_id}")
    assert get_resp.status_code == 200
    retrieved_data = get_resp.json()
    assert retrieved_data["trip_id"] == trip_id
    assert retrieved_data["trip_summary"]["destination"] == "Munnar"
