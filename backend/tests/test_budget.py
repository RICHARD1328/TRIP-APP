import pytest


def test_calculate_budget_exact_decimal(client):
    """
    Test POST /api/v1/budget/calculate returns exact decimal-safe arithmetic subtotals,
    emergency reserve, total budget, per person cost, per day cost, and category percentages.
    """
    payload = {
        "trip_id": "test_trip_budget_001",
        "number_of_travelers": 2,
        "number_of_days": 4,
        "accommodation_cost": 5000.50,
        "transport_cost": 2000.25,
        "food_cost": 1500.00,
        "attraction_cost": 800.00,
        "shopping_cost": 1000.00,
        "emergency_reserve": 1000.00,
        "other_cost": 500.00
    }

    response = client.post("/api/v1/budget/calculate", json=payload)
    assert response.status_code == 200
    data = response.json()

    assert data["trip_id"] == "test_trip_budget_001"
    assert data["number_of_travelers"] == 2
    assert data["number_of_days"] == 4

    # Breakdown verification
    bd = data["breakdown"]
    assert bd["accommodation_cost"] == 5000.50
    assert bd["transport_cost"] == 2000.25
    assert bd["food_cost"] == 1500.00
    assert bd["attraction_cost"] == 800.00
    assert bd["shopping_cost"] == 1000.00
    assert bd["other_cost"] == 500.00

    # Subtotal = 5000.50 + 2000.25 + 1500.00 + 800.00 + 1000.00 + 500.00 = 10800.75
    assert bd["subtotal"] == 10800.75
    assert bd["emergency_reserve"] == 1000.00

    # Summary verification
    sm = data["summary"]
    assert sm["subtotal"] == 10800.75
    assert sm["emergency_reserve"] == 1000.00
    # Total budget = 10800.75 + 1000.00 = 11800.75
    assert sm["total_budget"] == 11800.75

    # Per person = 11800.75 / 2 = 5900.38 (rounded)
    assert sm["per_person_cost"] == 5900.38

    # Per day = 11800.75 / 4 = 2950.19 (rounded)
    assert sm["per_day_cost"] == 2950.19

    # Category percentages check
    pct = data["category_percentages"]
    assert "accommodation" in pct
    assert "transport" in pct
    assert "food" in pct
    assert "attraction" in pct
    assert "emergency_reserve" in pct


def test_calculate_budget_invalid_travelers(client):
    """
    Test POST /api/v1/budget/calculate with invalid travelers (< 1) returns 400 error.
    """
    payload = {
        "number_of_travelers": 0,
        "number_of_days": 3,
        "accommodation_cost": 2000.0
    }
    response = client.post("/api/v1/budget/calculate", json=payload)
    assert response.status_code in [400, 422]


def test_calculate_budget_invalid_days(client):
    """
    Test POST /api/v1/budget/calculate with invalid days (< 1) returns 400 error.
    """
    payload = {
        "number_of_travelers": 2,
        "number_of_days": 0,
        "accommodation_cost": 2000.0
    }
    response = client.post("/api/v1/budget/calculate", json=payload)
    assert response.status_code in [400, 422]


def test_get_budget_by_trip_id(client):
    """
    Test GET /api/v1/budget/{trip_id} retrieves saved budget calculation.
    """
    # 1. Calculate & auto-save with trip_id
    calc_payload = {
        "trip_id": "trip_budget_saved_099",
        "number_of_travelers": 3,
        "number_of_days": 5,
        "accommodation_cost": 12000.0,
        "transport_cost": 3500.0,
        "food_cost": 4000.0,
        "emergency_reserve": 2000.0
    }
    post_res = client.post("/api/v1/budget/calculate", json=calc_payload)
    assert post_res.status_code == 200

    # 2. Retrieve by trip_id
    get_res = client.get("/api/v1/budget/trip_budget_saved_099")
    assert get_res.status_code == 200
    retrieved = get_res.json()

    assert retrieved["trip_id"] == "trip_budget_saved_099"
    assert retrieved["summary"]["total_budget"] == 21500.0
    assert retrieved["number_of_travelers"] == 3
