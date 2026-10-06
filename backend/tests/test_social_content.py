import pytest
from fastapi.testclient import TestClient
from app.main import app
from app.schemas.social_content_schemas import CreateSocialContentRequest

client = TestClient(app)
ADMIN_HEADER = {"X-Admin-Key": "admin_secret_key_tripdirect_2026"}


def test_instagram_url_validation():
    """Verify Instagram URL validator accepts valid Reels/Posts and rejects arbitrary URLs."""
    valid_payload = {
        "destination_id": "munnar",
        "title": "Munnar Reel",
        "reel_url": "https://www.instagram.com/reel/C3SampleReel/"
    }
    req = CreateSocialContentRequest(**valid_payload)
    assert req.reel_url == "https://www.instagram.com/reel/C3SampleReel/"

    with pytest.raises(ValueError):
        CreateSocialContentRequest(
            destination_id="munnar",
            title="Fake Reel",
            reel_url="https://fake-video-sharing-site.com/video123"
        )


def test_destination_details_returns_social_content():
    """Verify GET /api/v1/destinations/{id} returns verified social content for Munnar and empty for unknown."""
    response = client.get("/api/v1/destinations/Munnar")
    assert response.status_code == 200
    data = response.json()
    assert data["name"].lower() in ["munnar"]
    assert "social_content" in data
    assert len(data["social_content"]) >= 1
    assert data["social_content"][0]["platform"] == "instagram"
    assert data["social_content"][0]["verified"] is True

    # Test destination without verified reels
    empty_resp = client.get("/api/v1/destinations/UnknownLocationWithoutReels99")
    assert empty_resp.status_code == 200
    empty_data = empty_resp.json()
    assert empty_data["social_content"] == []


def test_admin_social_content_crud():
    """Verify admin endpoints: POST, PUT, DELETE social content with authorization."""
    create_payload = {
        "destination_id": "test_dest_coorg",
        "platform": "instagram",
        "title": "Coorg Travel Reel Test",
        "reel_url": "https://www.instagram.com/reel/CTestCoorgReel9/",
        "thumbnail_url": "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800",
        "creator_name": "TestCreator",
        "description": "Test description",
        "verified": True,
        "active": True
    }

    # 1. Create
    post_resp = client.post("/api/v1/admin/social-content", json=create_payload, headers=ADMIN_HEADER)
    assert post_resp.status_code == 201
    content_data = post_resp.json()
    item_id = content_data["id"]
    assert content_data["title"] == "Coorg Travel Reel Test"

    # 2. Update / Disable
    update_payload = {"title": "Updated Coorg Reel Title", "active": False}
    put_resp = client.put(f"/api/v1/admin/social-content/{item_id}", json=update_payload, headers=ADMIN_HEADER)
    assert put_resp.status_code == 200
    assert put_resp.json()["title"] == "Updated Coorg Reel Title"
    assert put_resp.json()["active"] is False

    # 3. Delete
    del_resp = client.delete(f"/api/v1/admin/social-content/{item_id}", headers=ADMIN_HEADER)
    assert del_resp.status_code == 200


def test_admin_unauthorized():
    """Verify admin endpoints return HTTP 401 when X-Admin-Key is missing or invalid."""
    payload = {
        "destination_id": "test_dest",
        "title": "Unauthorized Reel",
        "reel_url": "https://www.instagram.com/reel/C123456789/"
    }
    resp = client.post("/api/v1/admin/social-content", json=payload)
    assert resp.status_code == 401
