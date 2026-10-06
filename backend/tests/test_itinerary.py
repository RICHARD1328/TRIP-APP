import pytest


def get_auth_token(client, email="itin_user@example.com", password="Password123!"):
    register_payload = {
        "full_name": "Itinerary User",
        "email": email,
        "password": password
    }
    res = client.post("/api/v1/auth/register", json=register_payload)
    if res.status_code == 201:
        return res.json()["access_token"]
    login_res = client.post("/api/v1/auth/login", json={"email": email, "password": password})
    return login_res.json()["access_token"]


def test_create_and_get_itinerary(client):
    token = get_auth_token(client, "itin_test1@example.com")
    headers = {"Authorization": f"Bearer {token}"}

    create_payload = {
        "title": "Summer Ooty Escape",
        "destination": "Ooty",
        "start_date": "2026-10-01",
        "end_date": "2026-10-03",
        "budget": 12000.0
    }

    # 1. Create Itinerary
    response = client.post("/api/v1/itineraries", json=create_payload, headers=headers)
    assert response.status_code == 201
    data = response.json()

    assert data["title"] == "Summer Ooty Escape"
    assert data["destination"] == "Ooty"
    assert data["budget"] == 12000.0
    assert len(data["days"]) == 3  # Oct 1, Oct 2, Oct 3
    trip_id = data["id"]
    day1_id = data["days"][0]["id"]

    # 2. List Itineraries
    list_res = client.get("/api/v1/itineraries", headers=headers)
    assert list_res.status_code == 200
    trips_list = list_res.json()
    assert len(trips_list) > 0

    # 3. Get Itinerary by ID
    get_res = client.get(f"/api/v1/itineraries/{trip_id}", headers=headers)
    assert get_res.status_code == 200
    assert get_res.json()["id"] == trip_id

    # 4. Add Item to Day 1
    item_payload = {
        "place_id": "ooty_lake",
        "title": "Pedal Boating at Ooty Lake",
        "description": "Boating and walking around eucalyptus trees",
        "start_time": "09:30 AM",
        "end_time": "11:30 AM",
        "estimated_cost": 250.0,
        "latitude": 11.4087,
        "longitude": 76.6896,
        "notes": "Remember to buy boating tickets early"
    }
    add_item_res = client.post(f"/api/v1/itineraries/{trip_id}/days/{day1_id}/items", json=item_payload, headers=headers)
    assert add_item_res.status_code == 201
    item_data = add_item_res.json()
    assert item_data["title"] == "Pedal Boating at Ooty Lake"
    item_id = item_data["id"]

    # 5. Update Item
    update_item_res = client.put(f"/api/v1/itineraries/{trip_id}/items/{item_id}", json={"estimated_cost": 300.0, "notes": "Updated note"}, headers=headers)
    assert update_item_res.status_code == 200
    assert update_item_res.json()["estimated_cost"] == 300.0

    # 6. Delete Item
    del_item_res = client.delete(f"/api/v1/itineraries/{trip_id}/items/{item_id}", headers=headers)
    assert del_item_res.status_code == 200

    # 7. Update Itinerary Title
    update_trip_res = client.put(f"/api/v1/itineraries/{trip_id}", json={"title": "Updated Ooty Trip Title"}, headers=headers)
    assert update_trip_res.status_code == 200
    assert update_trip_res.json()["title"] == "Updated Ooty Trip Title"

    # 8. Delete Itinerary
    del_trip_res = client.delete(f"/api/v1/itineraries/{trip_id}", headers=headers)
    assert del_trip_res.status_code == 200

    # Verify 404 after deletion
    verify_del = client.get(f"/api/v1/itineraries/{trip_id}", headers=headers)
    assert verify_del.status_code == 404


def test_itinerary_user_isolation(client):
    """
    Test user access authorization isolation: User B cannot access User A's itinerary.
    """
    token_a = get_auth_token(client, "usera@example.com")
    token_b = get_auth_token(client, "userb@example.com")

    headers_a = {"Authorization": f"Bearer {token_a}"}
    headers_b = {"Authorization": f"Bearer {token_b}"}

    # User A creates trip A
    res_a = client.post("/api/v1/itineraries", json={
        "title": "User A Private Trip",
        "destination": "Goa",
        "start_date": "2026-11-01",
        "end_date": "2026-11-03",
        "budget": 20000.0
    }, headers=headers_a)
    assert res_a.status_code == 201
    trip_a_id = res_a.json()["id"]

    # User B attempts to access trip A -> expect 404
    res_b_get = client.get(f"/api/v1/itineraries/{trip_a_id}", headers=headers_b)
    assert res_b_get.status_code == 404

    # User B attempts to update trip A -> expect 404
    res_b_put = client.put(f"/api/v1/itineraries/{trip_a_id}", json={"title": "Hacked Title"}, headers=headers_b)
    assert res_b_put.status_code == 404

    # User B attempts to delete trip A -> expect 404
    res_b_del = client.delete(f"/api/v1/itineraries/{trip_a_id}", headers=headers_b)
    assert res_b_del.status_code == 404
