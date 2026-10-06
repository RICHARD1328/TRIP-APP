import pytest


def test_get_safety_services_with_coordinates(client):
    """
    Test GET /api/v1/safety/services with latitude and longitude parameters.
    """
    response = client.get("/api/v1/safety/services?latitude=11.4102&longitude=76.6950&radius=5000")
    assert response.status_code == 200
    data = response.json()

    assert data["latitude"] == 11.4102
    assert data["longitude"] == 76.6950
    assert data["radius"] == 5000.0
    assert "hospitals" in data
    assert "police_stations" in data
    assert "pharmacies" in data
    assert "emergency_medical_facilities" in data
    assert data["total_found"] >= 0


def test_get_safety_services_default(client):
    """
    Test GET /api/v1/safety/services without coordinates.
    """
    response = client.get("/api/v1/safety/services")
    assert response.status_code == 200
    data = response.json()
    assert data["radius"] == 5000.0
    assert isinstance(data["hospitals"], list)


def test_get_safety_tips(client):
    """
    Test GET /api/v1/safety/tips returns travel safety information categories.
    """
    response = client.get("/api/v1/safety/tips")
    assert response.status_code == 200
    data = response.json()

    assert "categories" in data
    assert "general_advice" in data
    assert len(data["categories"]) > 0

    category_names = [c["category"] for c in data["categories"]]
    assert "general" in category_names
    assert "transportation" in category_names
    assert "health" in category_names
    assert "scams" in category_names

    # Check structure of tips
    first_cat = data["categories"][0]
    assert len(first_cat["tips"]) > 0
    assert "title" in first_cat["tips"][0]
    assert "description" in first_cat["tips"][0]


def test_get_safety_checklist_global(client):
    """
    Test GET /api/v1/safety/checklist default global response.
    """
    response = client.get("/api/v1/safety/checklist")
    assert response.status_code == 200
    data = response.json()

    assert data["country"] == "Global"
    assert len(data["emergency_contacts"]) >= 2
    assert len(data["important_documents"]) > 0
    assert len(data["medical_preparation"]) > 0
    assert len(data["travel_precautions"]) > 0
    assert len(data["local_safety_recommendations"]) > 0

    # Ensure dialable flag exists
    for contact in data["emergency_contacts"]:
        assert "is_dialable" in contact
        assert isinstance(contact["is_dialable"], bool)


def test_get_safety_checklist_india(client):
    """
    Test GET /api/v1/safety/checklist?country=India returns verified Indian emergency numbers.
    """
    response = client.get("/api/v1/safety/checklist?country=India")
    assert response.status_code == 200
    data = response.json()

    assert data["country"] == "India"
    numbers = [c["number"] for c in data["emergency_contacts"]]
    assert "112" in numbers
    assert "100" in numbers
    assert "108" in numbers
    assert "101" in numbers
    assert "1363" in numbers  # Tourist Helpline


def test_get_safety_checklist_usa(client):
    """
    Test GET /api/v1/safety/checklist?country=US returns 911 emergency number.
    """
    response = client.get("/api/v1/safety/checklist?country=USA")
    assert response.status_code == 200
    data = response.json()

    assert data["country"] == "United States"
    numbers = [c["number"] for c in data["emergency_contacts"]]
    assert "911" in numbers


def test_get_safety_checklist_japan(client):
    """
    Test GET /api/v1/safety/checklist?country=Japan returns 110 (Police) and 119 (Ambulance).
    """
    response = client.get("/api/v1/safety/checklist?country=Japan")
    assert response.status_code == 200
    data = response.json()

    assert data["country"] == "Japan"
    numbers = [c["number"] for c in data["emergency_contacts"]]
    assert "110" in numbers
    assert "119" in numbers
