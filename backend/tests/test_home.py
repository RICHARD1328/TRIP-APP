import pytest


def test_get_home_dashboard(client):
    response = client.get("/api/v1/home")
    assert response.status_code == 200
    data = response.json()

    assert "popular_destinations" in data
    assert "popular_attractions" in data
    assert "recommended_hotels" in data
    assert "featured_places" in data
    assert "quick_statistics" in data

    # Verify structured item fields on popular destinations
    assert len(data["popular_destinations"]) > 0
    item = data["popular_destinations"][0]
    expected_fields = [
        "id", "name", "location", "latitude", "longitude",
        "image_url", "rating", "category", "short_description"
    ]
    for field in expected_fields:
        assert field in item, f"Field '{field}' missing from place item"
        assert item[field] is not None

    # Verify quick statistics structure
    stats = data["quick_statistics"]
    assert "total_destinations" in stats
    assert "total_attractions" in stats
    assert "total_hotels" in stats
    assert "satisfied_travelers" in stats


def test_get_popular_destinations_paginated(client):
    response = client.get("/api/v1/home/popular-destinations?page=1&size=2")
    assert response.status_code == 200
    data = response.json()

    assert data["page"] == 1
    assert data["size"] == 2
    assert "total" in data
    assert len(data["items"]) <= 2
    if len(data["items"]) > 0:
        item = data["items"][0]
        assert "image_url" in item
        assert item["image_url"].startswith("http")


def test_get_popular_attractions_paginated(client):
    response = client.get("/api/v1/home/popular-attractions?page=1&size=3")
    assert response.status_code == 200
    data = response.json()

    assert data["page"] == 1
    assert data["size"] == 3
    assert data["total"] > 0
    assert len(data["items"]) > 0


def test_get_recommended_hotels_paginated(client):
    response = client.get("/api/v1/home/recommended-hotels?page=1&size=3")
    assert response.status_code == 200
    data = response.json()

    assert data["page"] == 1
    assert data["size"] == 3
    assert data["total"] > 0
    assert len(data["items"]) > 0
    item = data["items"][0]
    assert item["category"] == "Hotel"
    assert item["image_url"].startswith("http")


def test_get_featured_places_paginated(client):
    response = client.get("/api/v1/home/featured?page=1&size=5")
    assert response.status_code == 200
    data = response.json()

    assert data["page"] == 1
    assert data["size"] == 5
    assert data["total"] > 0
    assert len(data["items"]) > 0
    item = data["items"][0]
    assert item["name"] is not None
    assert item["short_description"] is not None


def test_destination_images_and_coordinates_validity(client):
    """
    Verify that every destination item in the home dashboard has a non-random,
    valid image URL and non-zero latitude/longitude coordinates.
    """
    response = client.get("/api/v1/home")
    assert response.status_code == 200
    data = response.json()

    for category_key in ["popular_destinations", "popular_attractions", "recommended_hotels", "featured_places"]:
        items = data[category_key]
        for place in items:
            assert place["image_url"].startswith("http://") or place["image_url"].startswith("https://")
            assert place["latitude"] != 0.0
            assert place["longitude"] != 0.0
            assert len(place["name"].strip()) > 0
            assert len(place["location"].strip()) > 0
            assert len(place["short_description"].strip()) > 0

