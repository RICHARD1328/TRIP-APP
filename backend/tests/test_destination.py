import pytest
from unittest.mock import patch, MagicMock
import httpx


MOCK_NOMINATIM_RESPONSE = [
    {
        "place_id": 12345,
        "licence": "Data © OpenStreetMap contributors",
        "osm_type": "node",
        "osm_id": 67890,
        "lat": "11.4102",
        "lon": "76.6950",
        "class": "place",
        "type": "town",
        "place_rank": 16,
        "importance": 0.5,
        "addresstype": "town",
        "name": "Ooty",
        "display_name": "Ooty, Udhagamandalam, Nilgiris, Tamil Nadu, 643001, India",
        "address": {
            "town": "Ooty",
            "county": "Nilgiris",
            "state": "Tamil Nadu",
            "ISO3166-2-LVL4": "IN-TN",
            "postcode": "643001",
            "country": "India",
            "country_code": "in"
        },
        "boundingbox": ["11.3702", "11.4502", "76.6550", "76.7350"]
    }
]


def test_search_destination_valid_query(client):
    """
    Test GET /api/v1/destinations/search?q=Ooty returns structured destination data.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_NOMINATIM_RESPONSE
    mock_client_instance.get.return_value = mock_response

    with patch("app.services.destination_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        response = client.get("/api/v1/destinations/search?q=Ooty_Test_Query")
        assert response.status_code == 200
        data = response.json()

        assert len(data) == 1
        item = data[0]
        assert item["place_name"] == "Ooty"
        assert item["display_name"] == "Ooty, Udhagamandalam, Nilgiris, Tamil Nadu, 643001, India"
        assert item["latitude"] == 11.4102
        assert item["longitude"] == 76.6950
        assert item["country"] == "India"
        assert item["state"] == "Tamil Nadu"
        assert item["city"] == "Ooty"
        assert item["place_type"] == "town"


def test_search_destination_short_query(client):
    """
    Test GET /api/v1/destinations/search?q=a returns validation error.
    """
    response = client.get("/api/v1/destinations/search?q=a")
    assert response.status_code in [400, 422]


def test_search_destination_empty_results(client):
    """
    Test GET /api/v1/destinations/search when Nominatim returns no results.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = []
    mock_client_instance.get.return_value = mock_response

    with patch("app.services.destination_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        response = client.get("/api/v1/destinations/search?q=NonExistentPlaceXYZ99")
        assert response.status_code == 200
        data = response.json()
        assert data == []


def test_search_destination_timeout(client):
    """
    Test GET /api/v1/destinations/search handles Nominatim timeout gracefully.
    """
    mock_client_instance = MagicMock()
    mock_client_instance.get.side_effect = httpx.TimeoutException("Connection timed out")

    with patch("app.services.destination_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        response = client.get("/api/v1/destinations/search?q=TimeoutTestQuery")
        assert response.status_code == 200
        data = response.json()
        assert data == []


def test_search_destination_caching(client):
    """
    Test that repeated requests use in-memory cache and do not invoke HTTP client again.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_NOMINATIM_RESPONSE
    mock_client_instance.get.return_value = mock_response

    with patch("app.services.destination_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        # First request populates cache
        res1 = client.get("/api/v1/destinations/search?q=CacheTestQuery")
        assert res1.status_code == 200
        initial_call_count = mock_client_instance.get.call_count

        # Second request should be served from cache
        res2 = client.get("/api/v1/destinations/search?q=CacheTestQuery")
        assert res2.status_code == 200
        assert mock_client_instance.get.call_count == initial_call_count
        assert res1.json() == res2.json()
