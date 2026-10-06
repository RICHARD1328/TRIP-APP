import pytest
from unittest.mock import patch, MagicMock
import httpx


MOCK_OVERPASS_RESPONSE = {
    "version": 0.6,
    "generator": "Overpass API 0.7.62",
    "elements": [
        {
            "type": "node",
            "id": 1001,
            "lat": 11.4110,
            "lon": 76.6960,
            "tags": {
                "name": "Ooty Lake Boating",
                "tourism": "attraction",
                "addr:street": "Lake Road",
                "addr:city": "Ooty",
                "image": "https://images.unsplash.com/photo-1590490360182-c33d57733427"
            }
        },
        {
            "type": "way",
            "id": 2002,
            "center": {
                "lat": 11.4150,
                "lon": 76.7000
            },
            "tags": {
                "name": "Botanical Garden View",
                "tourism": "attraction",
                "addr:city": "Ooty"
            }
        }
    ]
}


def test_get_nearby_places_valid(client):
    """
    Test GET /api/v1/places/nearby returns structured place list.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_OVERPASS_RESPONSE
    mock_client_instance.post.return_value = mock_response

    with patch("app.services.place_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        response = client.get("/api/v1/places/nearby?latitude=11.4102&longitude=76.6950&radius=5000&category=attraction")
        assert response.status_code == 200
        data = response.json()

        assert len(data) == 2
        item1 = data[0]
        assert item1["id"] == "node/1001"
        assert item1["name"] == "Ooty Lake Boating"
        assert item1["category"] == "attraction"
        assert item1["latitude"] == 11.4110
        assert item1["longitude"] == 76.6960
        assert "Lake Road" in item1["address"]
        assert item1["distance"] > 0
        assert item1["image_url"] == "https://images.unsplash.com/photo-1590490360182-c33d57733427"


def test_get_nearby_places_invalid_lat(client):
    """
    Test GET /api/v1/places/nearby with invalid latitude (> 90) returns validation error.
    """
    response = client.get("/api/v1/places/nearby?latitude=999.0&longitude=76.6950")
    assert response.status_code in [400, 422]


def test_get_nearby_places_invalid_lon(client):
    """
    Test GET /api/v1/places/nearby with invalid longitude (> 180) returns validation error.
    """
    response = client.get("/api/v1/places/nearby?latitude=11.4102&longitude=500.0")
    assert response.status_code in [400, 422]


def test_get_nearby_places_empty_results(client):
    """
    Test GET /api/v1/places/nearby returns empty list when Overpass yields no elements.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = {"elements": []}
    mock_client_instance.post.return_value = mock_response

    with patch("app.services.place_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        response = client.get("/api/v1/places/nearby?latitude=0.0&longitude=0.0&radius=1000")
        assert response.status_code == 200
        data = response.json()
        assert data == []


def test_get_nearby_places_timeout(client):
    """
    Test GET /api/v1/places/nearby handles Overpass timeout gracefully.
    """
    mock_client_instance = MagicMock()
    mock_client_instance.post.side_effect = httpx.TimeoutException("Overpass timeout")

    with patch("app.services.place_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        response = client.get("/api/v1/places/nearby?latitude=11.4102&longitude=76.6950&radius=5000")
        assert response.status_code == 200
        data = response.json()
        assert data == []


def test_get_nearby_places_caching(client):
    """
    Test that repeated requests for nearby places use in-memory cache.
    """
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.raise_for_status.return_value = None
    mock_response.json.return_value = MOCK_OVERPASS_RESPONSE
    mock_client_instance.post.return_value = mock_response

    with patch("app.services.place_service.httpx.Client") as mock_client_cls:
        mock_client_cls.return_value.__enter__.return_value = mock_client_instance

        res1 = client.get("/api/v1/places/nearby?latitude=12.9716&longitude=77.5946&radius=2000&category=hotel")
        assert res1.status_code == 200
        call_count = mock_client_instance.post.call_count

        res2 = client.get("/api/v1/places/nearby?latitude=12.9716&longitude=77.5946&radius=2000&category=hotel")
        assert res2.status_code == 200
        assert mock_client_instance.post.call_count == call_count
        assert res1.json() == res2.json()
