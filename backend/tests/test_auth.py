import pytest


def test_health_check(client):
    response = client.get("/api/v1/health")
    assert response.status_code == 200
    assert response.json() == {
        "status": "ok",
        "service": "TripDirect Backend"
    }


def test_successful_registration(client):
    payload = {
        "full_name": "John Doe",
        "email": "john@example.com",
        "password": "securepassword123"
    }
    response = client.post("/api/v1/auth/register", json=payload)
    assert response.status_code == 201
    data = response.json()
    assert "access_token" in data
    assert data["token_type"] == "bearer"
    assert data["user"]["email"] == "john@example.com"
    assert data["user"]["full_name"] == "John Doe"
    assert "password_hash" not in data["user"]


def test_duplicate_registration(client):
    payload = {
        "full_name": "Jane Doe",
        "email": "jane@example.com",
        "password": "password123"
    }
    res1 = client.post("/api/v1/auth/register", json=payload)
    assert res1.status_code == 201

    res2 = client.post("/api/v1/auth/register", json=payload)
    assert res2.status_code == 400
    assert "already registered" in res2.json()["detail"].lower()


def test_successful_login(client):
    reg_payload = {
        "full_name": "Alice Traveler",
        "email": "alice@example.com",
        "password": "mypassword123"
    }
    client.post("/api/v1/auth/register", json=reg_payload)

    login_payload = {
        "email": "alice@example.com",
        "password": "mypassword123"
    }
    response = client.post("/api/v1/auth/login", json=login_payload)
    assert response.status_code == 200
    data = response.json()
    assert "access_token" in data
    assert data["token_type"] == "bearer"
    assert data["user"]["email"] == "alice@example.com"


def test_incorrect_password(client):
    reg_payload = {
        "full_name": "Bob Builder",
        "email": "bob@example.com",
        "password": "correctpassword"
    }
    client.post("/api/v1/auth/register", json=reg_payload)

    login_payload = {
        "email": "bob@example.com",
        "password": "wrongpassword"
    }
    response = client.post("/api/v1/auth/login", json=login_payload)
    assert response.status_code == 401
    assert "incorrect email or password" in response.json()["detail"].lower()


def test_me_with_valid_token(client):
    reg_payload = {
        "full_name": "Charlie Brown",
        "email": "charlie@example.com",
        "password": "password123"
    }
    reg_res = client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_res.json()["access_token"]

    headers = {"Authorization": f"Bearer {token}"}
    me_res = client.get("/api/v1/auth/me", headers=headers)
    assert me_res.status_code == 200
    user_data = me_res.json()
    assert user_data["email"] == "charlie@example.com"
    assert user_data["full_name"] == "Charlie Brown"


def test_me_without_token(client):
    response = client.get("/api/v1/auth/me")
    assert response.status_code == 401
    assert "could not validate credentials" in response.json()["detail"].lower()


def test_token_refresh(client):
    reg_payload = {
        "full_name": "David Smith",
        "email": "david@example.com",
        "password": "password123"
    }
    reg_res = client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_res.json()["access_token"]

    headers = {"Authorization": f"Bearer {token}"}
    refresh_res = client.post("/api/v1/auth/refresh", headers=headers)
    assert refresh_res.status_code == 200
    refreshed_data = refresh_res.json()
    assert "access_token" in refreshed_data
    assert refreshed_data["token_type"] == "bearer"


def test_invalid_email_registration_fails(client):
    """
    Test registration with invalid email format returns 422 Unprocessable Entity.
    """
    payload = {
        "full_name": "Invalid Email",
        "email": "not-an-email",
        "password": "password123"
    }
    response = client.post("/api/v1/auth/register", json=payload)
    assert response.status_code == 422


def test_weak_password_registration_fails(client):
    """
    Test registration with weak password (<6 chars) returns 422 Unprocessable Entity.
    """
    payload = {
        "full_name": "Short Password",
        "email": "short@example.com",
        "password": "123"
    }
    response = client.post("/api/v1/auth/register", json=payload)
    assert response.status_code == 422


def test_me_with_invalid_token_fails(client):
    """
    Test GET /api/v1/auth/me with bogus or malformed token returns 401 Unauthorized.
    """
    headers = {"Authorization": "Bearer invalid_malformed_token_123"}
    response = client.get("/api/v1/auth/me", headers=headers)
    assert response.status_code == 401
    assert "could not validate credentials" in response.json()["detail"].lower()


def test_logout(client):
    reg_payload = {
        "full_name": "Eve Adams",
        "email": "eve@example.com",
        "password": "password123"
    }
    reg_res = client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_res.json()["access_token"]

    headers = {"Authorization": f"Bearer {token}"}
    logout_res = client.post("/api/v1/auth/logout", headers=headers)
    assert logout_res.status_code == 200
    assert "successfully logged out" in logout_res.json()["detail"].lower()

