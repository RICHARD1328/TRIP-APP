import pytest


def get_auth_token(client, email="user@example.com", name="Test User"):
    """
    Helper to register a new user and return JWT access token.
    """
    response = client.post("/api/v1/auth/register", json={
        "full_name": name,
        "email": email,
        "password": "password123"
    })
    assert response.status_code == 201
    return response.json()["access_token"]


def test_unauthenticated_review_creation_fails(client):
    """
    Test POST /api/v1/reviews without Bearer auth header returns 401 Unauthorized.
    """
    payload = {
        "target_type": "destination",
        "target_id": "ooty",
        "rating": 5.0,
        "title": "Amazing Place",
        "comment": "Loved the eucalyptus hills!"
    }
    response = client.post("/api/v1/reviews", json=payload)
    assert response.status_code == 401


def test_create_review_success(client):
    """
    Test creating a review with valid authentication and fields.
    """
    token = get_auth_token(client, email="alice@example.com", name="Alice")
    headers = {"Authorization": f"Bearer {token}"}

    payload = {
        "target_type": "destination",
        "target_id": "munnar",
        "rating": 5.0,
        "title": "Breathtaking Tea Plantations",
        "comment": "Lush green rolling hills and pleasant weather year round."
    }
    response = client.post("/api/v1/reviews", json=payload, headers=headers)
    assert response.status_code == 201
    data = response.json()

    assert data["id"] > 0
    assert data["user_name"] == "Alice"
    assert data["target_type"] == "destination"
    assert data["target_id"] == "munnar"
    assert data["rating"] == 5.0
    assert data["title"] == "Breathtaking Tea Plantations"


def test_duplicate_review_prevention(client):
    """
    Test user cannot submit duplicate review for the same target entity.
    """
    token = get_auth_token(client, email="bob@example.com", name="Bob")
    headers = {"Authorization": f"Bearer {token}"}

    payload = {
        "target_type": "hotel",
        "target_id": "hotel_101",
        "rating": 4.0,
        "title": "Great Stay",
        "comment": "Clean rooms and nice view."
    }
    res1 = client.post("/api/v1/reviews", json=payload, headers=headers)
    assert res1.status_code == 201

    res2 = client.post("/api/v1/reviews", json=payload, headers=headers)
    assert res2.status_code == 400
    assert "already submitted a review" in res2.json()["detail"].lower()


def test_rating_out_of_range_validation(client):
    """
    Test invalid rating (<1 or >5) returns validation error.
    """
    token = get_auth_token(client, email="charlie@example.com", name="Charlie")
    headers = {"Authorization": f"Bearer {token}"}

    payload_high = {
        "target_type": "attraction",
        "target_id": "lake_1",
        "rating": 6.0,
        "title": "Over 5 Stars",
        "comment": "Too high"
    }
    res_high = client.post("/api/v1/reviews", json=payload_high, headers=headers)
    assert res_high.status_code in [400, 422]

    payload_low = {
        "target_type": "attraction",
        "target_id": "lake_1",
        "rating": 0.0,
        "title": "Zero Stars",
        "comment": "Too low"
    }
    res_low = client.post("/api/v1/reviews", json=payload_low, headers=headers)
    assert res_low.status_code in [400, 422]


def test_get_reviews_target_stats_and_pagination(client):
    """
    Test GET /api/v1/reviews/{target_type}/{target_id} computes average rating and rating breakdown correctly.
    """
    token1 = get_auth_token(client, email="user1@example.com", name="User One")
    token2 = get_auth_token(client, email="user2@example.com", name="User Two")

    # User 1 posts 5-star review
    client.post("/api/v1/reviews", json={
        "target_type": "attraction",
        "target_id": "botanical_gardens",
        "rating": 5.0,
        "title": "Stunning Flowers",
        "comment": "Very well maintained garden."
    }, headers={"Authorization": f"Bearer {token1}"})

    # User 2 posts 3-star review
    client.post("/api/v1/reviews", json={
        "target_type": "attraction",
        "target_id": "botanical_gardens",
        "rating": 3.0,
        "title": "Decent place",
        "comment": "A bit crowded during weekends."
    }, headers={"Authorization": f"Bearer {token2}"})

    # Fetch reviews
    response = client.get("/api/v1/reviews/attraction/botanical_gardens?page=1&limit=10")
    assert response.status_code == 200
    data = response.json()

    assert data["total_reviews"] == 2
    assert data["average_rating"] == 4.0  # (5.0 + 3.0) / 2 = 4.0
    assert data["rating_breakdown"]["5"] == 1
    assert data["rating_breakdown"]["3"] == 1
    assert data["rating_breakdown"]["4"] == 0
    assert len(data["reviews"]) == 2


def test_update_own_review_success(client):
    """
    Test user updating their own review.
    """
    token = get_auth_token(client, email="diana@example.com", name="Diana")
    headers = {"Authorization": f"Bearer {token}"}

    create_res = client.post("/api/v1/reviews", json={
        "target_type": "hotel",
        "target_id": "resort_77",
        "rating": 4.0,
        "title": "Good resort",
        "comment": "Nice pool."
    }, headers=headers)
    review_id = create_res.json()["id"]

    update_payload = {
        "rating": 5.0,
        "title": "Updated: Exceptional resort!",
        "comment": "Updated: Staff were incredibly welcoming and pool was heated."
    }
    update_res = client.put(f"/api/v1/reviews/{review_id}", json=update_payload, headers=headers)
    assert update_res.status_code == 200
    updated_data = update_res.json()

    assert updated_data["rating"] == 5.0
    assert updated_data["title"] == "Updated: Exceptional resort!"


def test_update_and_delete_other_user_review_forbidden(client):
    """
    Test user cannot update or delete another user's review (403 Forbidden).
    """
    token1 = get_auth_token(client, email="owner@example.com", name="Owner")
    token2 = get_auth_token(client, email="hacker@example.com", name="Hacker")

    create_res = client.post("/api/v1/reviews", json={
        "target_type": "destination",
        "target_id": "goa",
        "rating": 5.0,
        "title": "Goa Beach Holiday",
        "comment": "Sun and sand!"
    }, headers={"Authorization": f"Bearer {token1}"})
    review_id = create_res.json()["id"]

    # Hacker attempts update
    update_res = client.put(f"/api/v1/reviews/{review_id}", json={
        "title": "Hacked Title"
    }, headers={"Authorization": f"Bearer {token2}"})
    assert update_res.status_code == 403

    # Hacker attempts delete
    delete_res = client.delete(f"/api/v1/reviews/{review_id}", headers={"Authorization": f"Bearer {token2}"})
    assert delete_res.status_code == 403


def test_delete_own_review_success(client):
    """
    Test user deleting their own review.
    """
    token = get_auth_token(client, email="evan@example.com", name="Evan")
    headers = {"Authorization": f"Bearer {token}"}

    create_res = client.post("/api/v1/reviews", json={
        "target_type": "destination",
        "target_id": "jaipur",
        "rating": 4.5,
        "title": "Royal Palaces",
        "comment": "Historical forts everywhere."
    }, headers=headers)
    review_id = create_res.json()["id"]

    delete_res = client.delete(f"/api/v1/reviews/{review_id}", headers=headers)
    assert delete_res.status_code == 200

    # Verify review count is now 0
    get_res = client.get("/api/v1/reviews/destination/jaipur")
    assert get_res.status_code == 200
    assert get_res.json()["total_reviews"] == 0
    assert get_res.json()["average_rating"] == 0.0
