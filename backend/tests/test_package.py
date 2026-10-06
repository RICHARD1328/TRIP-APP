import pytest


def test_get_packages_catalog(client):
    """
    Test GET /api/v1/packages returns seeded demo packages with required comparison fields.
    """
    response = client.get("/api/v1/packages")
    assert response.status_code == 200
    data = response.json()

    assert data["is_demo_data"] is True
    assert "demo" in data["disclaimer"].lower()
    assert data["total"] >= 6
    assert len(data["packages"]) >= 6

    # Verify fields on package item
    first_pkg = data["packages"][0]
    required_keys = [
        "id", "title", "destination", "total_price", "currency",
        "duration_days", "duration_nights", "accommodation", "transport",
        "included_attractions", "meals", "rating", "review_count",
        "exclusions", "provider_name", "is_demo_data", "data_source"
    ]
    for key in required_keys:
        assert key in first_pkg

    assert first_pkg["is_demo_data"] is True
    assert isinstance(first_pkg["included_attractions"], list)
    assert isinstance(first_pkg["exclusions"], list)


def test_get_packages_filters(client):
    """
    Test filtering packages by destination, max_price, max_duration, min_rating, and search.
    """
    # Filter by destination
    res_dest = client.get("/api/v1/packages?destination=Munnar")
    assert res_dest.status_code == 200
    pkgs_dest = res_dest.json()["packages"]
    assert len(pkgs_dest) > 0
    for pkg in pkgs_dest:
        assert "munnar" in pkg["destination"].lower()

    # Filter by max price
    res_price = client.get("/api/v1/packages?max_price=12000")
    assert res_price.status_code == 200
    pkgs_price = res_price.json()["packages"]
    assert len(pkgs_price) > 0
    for pkg in pkgs_price:
        assert pkg["total_price"] <= 12000.0

    # Filter by max duration
    res_dur = client.get("/api/v1/packages?max_duration=3")
    assert res_dur.status_code == 200
    pkgs_dur = res_dur.json()["packages"]
    assert len(pkgs_dur) > 0
    for pkg in pkgs_dur:
        assert pkg["duration_days"] <= 3

    # Filter by search term
    res_search = client.get("/api/v1/packages?search=Beach")
    assert res_search.status_code == 200
    pkgs_search = res_search.json()["packages"]
    assert len(pkgs_search) > 0
    assert any("goa" in p["destination"].lower() for p in pkgs_search)


def test_get_package_by_id_success(client):
    """
    Test GET /api/v1/packages/{id} for existing package.
    """
    response = client.get("/api/v1/packages/1")
    assert response.status_code == 200
    data = response.json()

    assert data["id"] == 1
    assert data["title"] == "Munnar Misty Hills Express (3D/2N)"
    assert data["destination"] == "Munnar, Kerala"
    assert data["total_price"] == 9999.0
    assert data["duration_days"] == 3
    assert data["is_demo_data"] is True


def test_get_package_by_id_not_found(client):
    """
    Test GET /api/v1/packages/{id} returns 404 for invalid ID.
    """
    response = client.get("/api/v1/packages/99999")
    assert response.status_code == 404
    assert "not found" in response.json()["detail"].lower()


def test_compare_packages_basic(client):
    """
    Test POST /api/v1/packages/compare with a list of valid package IDs.
    """
    payload = {
        "package_ids": [1, 2, 3]
    }
    response = client.post("/api/v1/packages/compare", json=payload)
    assert response.status_code == 200
    data = response.json()

    assert data["is_demo_data"] is True
    assert "demo" in data["disclaimer"].lower()

    # Verify compared_packages list
    compared = data["compared_packages"]
    assert len(compared) == 3

    pids = [cp["package"]["id"] for cp in compared]
    assert set(pids) == {1, 2, 3}

    for cp in compared:
        assert 0.0 <= cp["overall_score"] <= 100.0
        sb = cp["score_breakdown"]
        assert "price_score" in sb
        assert "rating_score" in sb
        assert "inclusions_score" in sb
        assert "duration_score" in sb
        assert "preference_match_score" in sb
        assert isinstance(cp["highlights"], list)
        assert isinstance(cp["drawbacks"], list)

    # Verify summary winners
    summary = data["summary"]
    assert summary["best_overall_package_id"] in [1, 2, 3]
    assert summary["cheapest_package_id"] == 1  # 9999.0 is lowest among 1, 2, 3
    assert summary["highest_rated_package_id"] == 2  # 4.9 is highest rating among 1, 2, 3
    assert summary["most_inclusive_package_id"] == 2  # 6 attractions vs 4 and 6

    # Verify comparison_matrix key dimensions
    matrix = data["comparison_matrix"]
    matrix_keys = {item["attribute_key"] for item in matrix}
    required_attribute_keys = {
        "total_price", "duration", "accommodation", "transport",
        "included_attractions", "meals", "rating", "exclusions"
    }
    assert required_attribute_keys.issubset(matrix_keys)

    for item in matrix:
        assert len(item["values"]) == 3
        for pid in [1, 2, 3]:
            assert str(pid) in item["values"]


def test_compare_packages_with_user_preferences(client):
    """
    Test POST /api/v1/packages/compare with explicit user preferences and weights.
    """
    payload = {
        "package_ids": [1, 2, 3],
        "user_preferences": {
            "max_budget": 12000.0,
            "preferred_duration_days": 3,
            "min_rating": 4.6,
            "required_inclusions": ["Boating", "Toy Train"],
            "importance_weights": {
                "price": 0.40,
                "rating": 0.20,
                "inclusions": 0.20,
                "duration": 0.20
            }
        }
    }
    response = client.post("/api/v1/packages/compare", json=payload)
    assert response.status_code == 200
    data = response.json()

    compared = data["compared_packages"]
    assert len(compared) == 3

    # Package 1 (9999.0 <= 12000 budget, 3 days duration match, 4.6 rating) should score high on price/duration/rating
    pkg1_cp = next(cp for cp in compared if cp["package"]["id"] == 1)
    assert pkg1_cp["score_breakdown"]["price_score"] > 50.0
    assert pkg1_cp["score_breakdown"]["duration_score"] == 100.0


def test_compare_packages_validation_errors(client):
    """
    Test POST /api/v1/packages/compare validation for invalid requests.
    """
    # Less than 2 package IDs
    res_single = client.post("/api/v1/packages/compare", json={"package_ids": [1]})
    assert res_single.status_code in [400, 422]

    # Non-existent package ID in list
    res_invalid_id = client.post("/api/v1/packages/compare", json={"package_ids": [1, 9999]})
    assert res_invalid_id.status_code == 404
    assert "not found" in res_invalid_id.json()["detail"].lower()
