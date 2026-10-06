import pytest


def test_search_hotels_all_demo(client):
    """
    Test GET /api/v1/hotels/search without filters returns default list tagged with source_type 'demo'.
    """
    response = client.get("/api/v1/hotels/search")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for hotel in data:
        assert hotel["source_type"] in ["demo", "local"]
        assert hotel["price_per_night"] > 0
        assert hotel["rating"] > 0
        assert len(hotel["name"]) > 0


def test_search_hotels_destination_filter(client):
    """
    Test filtering hotels by destination name (e.g. 'Ooty').
    """
    response = client.get("/api/v1/hotels/search?destination=Ooty")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for hotel in data:
        assert "ooty" in hotel["location"].lower() or "ooty" in hotel["name"].lower()


def test_search_hotels_price_filter(client):
    """
    Test filtering hotels by price range (min_price=4000, max_price=9000).
    """
    response = client.get("/api/v1/hotels/search?min_price=4000&max_price=9000")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for hotel in data:
        assert 4000.0 <= hotel["price_per_night"] <= 9000.0


def test_search_hotels_rating_filter(client):
    """
    Test filtering hotels by minimum rating (min_rating=4.8).
    """
    response = client.get("/api/v1/hotels/search?min_rating=4.8")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for hotel in data:
        assert hotel["rating"] >= 4.8


def test_search_hotels_amenities_filter(client):
    """
    Test filtering hotels by required amenities (e.g. 'Pool,Spa').
    """
    response = client.get("/api/v1/hotels/search?amenities=Pool,Spa")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    for hotel in data:
        amenities_lower = [a.lower() for a in hotel["amenities"]]
        assert "pool" in amenities_lower
        assert "spa" in amenities_lower


def test_search_hotels_distance_sorting(client):
    """
    Test hotel search with coordinates calculates distance_km and sorts by distance ascending.
    """
    # Coordinates for Ooty town center (11.4102, 76.6950)
    response = client.get("/api/v1/hotels/search?latitude=11.4102&longitude=76.6950&sort_by=distance")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    distances = []
    for hotel in data:
        assert hotel["distance_km"] is not None
        distances.append(hotel["distance_km"])

    # Verify distance sorting ascending
    assert distances == sorted(distances)


def test_search_hotels_price_sorting_descending(client):
    """
    Test hotel search with sort_by='price_high_to_low'.
    """
    response = client.get("/api/v1/hotels/search?sort_by=price_high_to_low")
    assert response.status_code == 200
    data = response.json()

    assert len(data) > 0
    prices = [h["price_per_night"] for h in data]
    assert prices == sorted(prices, reverse=True)
