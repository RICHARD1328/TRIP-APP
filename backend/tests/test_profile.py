import io
import pytest


def get_auth_token(client, email="profile_user@example.com", name="Profile User"):
    """
    Helper to register a new user and return access token.
    """
    response = client.post("/api/v1/auth/register", json={
        "full_name": name,
        "email": email,
        "password": "password123"
    })
    assert response.status_code == 201
    return response.json()["access_token"]


def test_unauthenticated_profile_access_fails(client):
    """
    Test GET /api/v1/profile without Bearer auth returns 401 Unauthorized.
    """
    res_get = client.get("/api/v1/profile")
    assert res_get.status_code == 401

    res_put = client.put("/api/v1/profile", json={"full_name": "New Name"})
    assert res_put.status_code == 401

    res_delete = client.delete("/api/v1/profile")
    assert res_delete.status_code == 401


def test_get_my_profile_success(client):
    """
    Test GET /api/v1/profile returns authenticated user's profile and hides sensitive credentials.
    """
    token = get_auth_token(client, email="alice_profile@example.com", name="Alice Profile")
    headers = {"Authorization": f"Bearer {token}"}

    response = client.get("/api/v1/profile", headers=headers)
    assert response.status_code == 200
    data = response.json()

    assert data["full_name"] == "Alice Profile"
    assert data["email"] == "alice_profile@example.com"
    assert "password_hash" not in data
    assert "password" not in data
    assert data["is_active"] is True


def test_update_my_profile_success(client):
    """
    Test PUT /api/v1/profile updates profile fields.
    """
    token = get_auth_token(client, email="bob_profile@example.com", name="Bob Initial")
    headers = {"Authorization": f"Bearer {token}"}

    update_payload = {
        "full_name": "Bob Updated",
        "phone_number": "+1-555-0199",
        "date_of_birth": "1995-06-15",
        "preferred_language": "en",
        "preferred_currency": "USD",
        "travel_preferences": ["Solo Travel", "Nature & Wildlife", "Mountain Trekking"],
        "interests": ["Photography", "Hiking", "Culinary Tours"]
    }
    response = client.put("/api/v1/profile", json=update_payload, headers=headers)
    assert response.status_code == 200
    data = response.json()

    assert data["full_name"] == "Bob Updated"
    assert data["phone_number"] == "+1-555-0199"
    assert data["date_of_birth"] == "1995-06-15"
    assert data["preferred_currency"] == "USD"
    assert "Solo Travel" in data["travel_preferences"]
    assert "Hiking" in data["interests"]


def test_update_profile_duplicate_email_fails(client):
    """
    Test updating profile email to an existing user's email returns 400 Bad Request.
    """
    # Create User 1
    token1 = get_auth_token(client, email="user1_existing@example.com", name="User One")
    # Create User 2
    token2 = get_auth_token(client, email="user2_profile@example.com", name="User Two")

    # User 2 tries to change email to User 1's email
    response = client.put("/api/v1/profile", json={
        "email": "user1_existing@example.com"
    }, headers={"Authorization": f"Bearer {token2}"})

    assert response.status_code == 400
    assert "already in use" in response.json()["detail"].lower()


def test_upload_profile_image_success(client):
    """
    Test POST /api/v1/profile/image uploads image file safely.
    """
    token = get_auth_token(client, email="charlie_image@example.com", name="Charlie Image")
    headers = {"Authorization": f"Bearer {token}"}

    fake_image_bytes = b"\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89"
    files = {"file": ("avatar.png", io.BytesIO(fake_image_bytes), "image/png")}

    response = client.post("/api/v1/profile/image", files=files, headers=headers)
    assert response.status_code == 200
    data = response.json()

    assert "profile_image_url" in data
    assert "/uploads/profile_images/" in data["profile_image_url"]

    # Verify profile now has the new image URL
    get_res = client.get("/api/v1/profile", headers=headers)
    assert get_res.status_code == 200
    assert get_res.json()["profile_image_url"] == data["profile_image_url"]


def test_upload_profile_image_invalid_type_fails(client):
    """
    Test uploading a non-image file type returns 400 Bad Request.
    """
    token = get_auth_token(client, email="diana_file@example.com", name="Diana File")
    headers = {"Authorization": f"Bearer {token}"}

    fake_pdf = b"%PDF-1.4 header text..."
    files = {"file": ("document.pdf", io.BytesIO(fake_pdf), "application/pdf")}

    response = client.post("/api/v1/profile/image", files=files, headers=headers)
    assert response.status_code == 400
    assert "invalid image format" in response.json()["detail"].lower()


def test_delete_my_profile(client):
    """
    Test DELETE /api/v1/profile deactivates user profile.
    """
    token = get_auth_token(client, email="evan_delete@example.com", name="Evan Delete")
    headers = {"Authorization": f"Bearer {token}"}

    response = client.delete("/api/v1/profile", headers=headers)
    assert response.status_code == 200
    assert "deactivated" in response.json()["message"].lower()

    # Subsequent profile requests with old token should fail authorization because account is inactive
    get_res = client.get("/api/v1/profile", headers=headers)
    assert get_res.status_code in [400, 401]
