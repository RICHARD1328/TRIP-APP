import pytest
from unittest.mock import patch, MagicMock
import httpx


MOCK_OSRM_RESPONSE = {
    "code": "Ok",
    "routes": [
        {
            "geometry": {
                "coordinates": [
                    [76.6950, 11.4102],
                    [76.6955, 11.4110],
                    [76.6960, 11.4120]
                ],
                "type": "LineString"
            },
            "legs": [
                {
                    "steps": [],
                    "summary": "Commercial Road",
                    "weight": 145.2,
                    "duration": 145.2,
                    "distance": 1820.5
                }
            ],
            "weight_name": "routability",
            "weight": 145.2,
            "duration": 145.2,
            "distance": 1820.5
        }
    ],
    "waypoints": [
        {"hint": "abc", "distance": 0.5, "name": "Start", "location": [76.6950, 11.4102]},
        {"hint": "def", "distance": 0.2, "name": "End", "location": [76.6960, 11.4120]}
    ]
}


def test_get_route_driving(client):
    """
    Test GET /api/v1/routes for driving travel_mode with OSRM response.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_OSRM_RESPONSE
    mock_client_instance.get.return_value = mock_response

    with patch("app.services.route_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        url = "/api/v1/routes?origin_latitude=11.4102&origin_longitude=76.6950&destination_latitude=11.4120&destination_longitude=76.6960&travel_mode=driving"
        response = client.get(url)
        assert response.status_code == 200
        data = response.json()

        assert data["travel_mode"] == "driving"
        assert data["distance"] == 1820.5
        assert data["distance_km"] == 1.82
        assert data["duration"] == 145.2
        assert data["duration_formatted"] == "2 mins"
        assert data["origin"]["latitude"] == 11.4102
        assert data["origin"]["longitude"] == 76.6950
        assert data["destination"]["latitude"] == 11.4120
        assert data["destination"]["longitude"] == 76.6960
        assert data["route_geometry"]["type"] == "LineString"
        assert len(data["route_geometry"]["coordinates"]) == 3


def test_get_route_walking(client):
    """
    Test GET /api/v1/routes for walking travel_mode.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_OSRM_RESPONSE
    mock_client_instance.get.return_value = mock_response

    with patch("app.services.route_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        url = "/api/v1/routes?origin_latitude=11.4102&origin_longitude=76.6950&destination_latitude=11.4120&destination_longitude=76.6960&travel_mode=walking"
        response = client.get(url)
        assert response.status_code == 200
        data = response.json()
        assert data["travel_mode"] == "walking"


def test_get_route_cycling(client):
    """
    Test GET /api/v1/routes for cycling travel_mode.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_OSRM_RESPONSE
    mock_client_instance.get.return_value = mock_response

    with patch("app.services.route_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        url = "/api/v1/routes?origin_latitude=11.4102&origin_longitude=76.6950&destination_latitude=11.4120&destination_longitude=76.6960&travel_mode=cycling"
        response = client.get(url)
        assert response.status_code == 200
        data = response.json()
        assert data["travel_mode"] == "cycling"


def test_get_route_invalid_coordinates(client):
    """
    Test GET /api/v1/routes with invalid latitude (> 90) returns 400/422 validation error.
    """
    url = "/api/v1/routes?origin_latitude=999.0&origin_longitude=76.6950&destination_latitude=11.4120&destination_longitude=76.6960"
    response = client.get(url)
    assert response.status_code in [400, 422]


def test_get_route_invalid_mode(client):
    """
    Test GET /api/v1/routes with unsupported travel_mode returns 400 error.
    """
    url = "/api/v1/routes?origin_latitude=11.4102&origin_longitude=76.6950&destination_latitude=11.4120&destination_longitude=76.6960&travel_mode=spacecraft"
    response = client.get(url)
    assert response.status_code == 400


def test_get_route_timeout_fallback(client):
    """
    Test GET /api/v1/routes fallback when OSRM times out.
    """
    mock_client_instance = MagicMock()
    mock_client_instance.get.side_effect = httpx.TimeoutException("OSRM timeout")

    with patch("app.services.route_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        url = "/api/v1/routes?origin_latitude=10.0&origin_longitude=70.0&destination_latitude=10.1&destination_longitude=70.1&travel_mode=driving"
        response = client.get(url)
        assert response.status_code == 200
        data = response.json()

        assert data["distance"] > 0
        assert data["duration"] > 0
        assert "Fallback" in data["provider_info"]
        assert data["route_geometry"]["type"] == "LineString"


def test_get_route_caching(client):
    """
    Test repeated route requests are served from in-memory cache.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_OSRM_RESPONSE
    mock_client_instance.get.return_value = mock_response

    with patch("app.services.route_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        url = "/api/v1/routes?origin_latitude=15.0001&origin_longitude=75.0001&destination_latitude=15.1001&destination_longitude=75.1001&travel_mode=driving"
        res1 = client.get(url)
        assert res1.status_code == 200
        call_count = mock_client_instance.get.call_count

        res2 = client.get(url)
        assert res2.status_code == 200
        assert mock_client_instance.get.call_count == call_count
        assert res1.json() == res2.json()
