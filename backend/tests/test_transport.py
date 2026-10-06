import pytest


def test_search_transport_all_demo(client):
    """
    Test GET /api/v1/transport/search returns sample transit options tagged with source_type 'demo'.
    """
    response = client.get("/api/v1/transport/search")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for item in data:
        assert item["source_type"] in ["demo", "local"]
        assert item["type"] in ["bus", "train", "flight", "taxi", "car"]
        assert item["price"] > 0
        assert item["duration_minutes"] > 0
        assert len(item["operator"]) > 0


def test_search_transport_route_filter(client):
    """
    Test filtering transport options by origin and destination.
    """
    response = client.get("/api/v1/transport/search?origin=Bangalore&destination=Ooty")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for item in data:
        assert "bangalore" in item["origin"].lower()
        assert "ooty" in item["destination"].lower()


def test_search_transport_type_bus(client):
    """
    Test filtering by transport_type=bus.
    """
    response = client.get("/api/v1/transport/search?transport_type=bus")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for item in data:
        assert item["type"] == "bus"


def test_search_transport_type_train(client):
    """
    Test filtering by transport_type=train.
    """
    response = client.get("/api/v1/transport/search?transport_type=train")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for item in data:
        assert item["type"] == "train"


def test_search_transport_type_flight(client):
    """
    Test filtering by transport_type=flight.
    """
    response = client.get("/api/v1/transport/search?transport_type=flight")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for item in data:
        assert item["type"] == "flight"


def test_search_transport_type_taxi(client):
    """
    Test filtering by transport_type=taxi.
    """
    response = client.get("/api/v1/transport/search?transport_type=taxi")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for item in data:
        assert item["type"] == "taxi"


def test_search_transport_type_car(client):
    """
    Test filtering by transport_type=car.
    """
    response = client.get("/api/v1/transport/search?transport_type=car")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for item in data:
        assert item["type"] == "car"


def test_search_transport_price_sorting(client):
    """
    Test sorting transport by price ascending.
    """
    response = client.get("/api/v1/transport/search?sort_by=price")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    prices = [t["price"] for t in data]
    assert prices == sorted(prices)


def test_search_transport_duration_sorting(client):
    """
    Test sorting transport by duration ascending.
    """
    response = client.get("/api/v1/transport/search?sort_by=duration")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    durations = [t["duration_minutes"] for t in data]
    assert durations == sorted(durations)
