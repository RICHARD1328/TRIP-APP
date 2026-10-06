import pytest
import time
from sqlalchemy.orm import Session
from app.core.database import SessionLocal
from app.schemas.ai_planner_schemas import AiPlannerRequest
from app.services import trip_planner_service


def test_real_ai_planner_ooty_execution():
    """
    Real-data integration test for Chennai -> Ooty.
    Executes actual network requests against Nominatim, Overpass API, Open-Meteo, and OSRM,
    and prints formatted summary details.
    """
    db: Session = SessionLocal()
    try:
        start_time = time.time()
        payload = AiPlannerRequest(
            origin="Chennai",
            destination="Ooty",
            start_date="2026-09-10",
            end_date="2026-09-13",
            travelers=2,
            budget=20000.0,
            interests=["nature", "photography", "food"],
            travel_style="balanced",
            transport_preference="car",
            accommodation_preference="budget"
        )

        result = trip_planner_service.generate_ai_trip_plan(db=db, request=payload)
        elapsed_sec = round(time.time() - start_time, 2)

        print("\n" + "=" * 60)
        print("REAL-DATA INTEGRATION TEST RESULTS (Chennai -> Ooty)")
        print("=" * 60)
        print(f"Trip ID                : {result['trip_id']}")
        print(f"Destination Data       : {result['destination']['name']} ({result['destination']['latitude']}, {result['destination']['longitude']})")
        print(f"Number of Places Found : {len(result['recommended_places'])}")
        print(f"Weather Status         : {len(result['weather'])} days forecast retrieved")
        print(f"Routes Calculated      : {len(result['routes'])} routes ({result['routes'][0]['distance_km']} km)")
        print(f"Recommended Places     : {[p['name'] for p in result['recommended_places'][:5]]}")
        print(f"Itinerary Days         : {len(result['itinerary'])} days generated")
        print(f"Total Budget           : INR {result['budget']['user_budget']} (Estimated Cost: INR {result['budget']['total_estimated_cost']})")
        print(f"Data Sources Used      : {[ds['source'] + ':' + ds['status'] for ds in result['data_sources']]}")
        print(f"Live Data Status       : {result['live_data']}")
        print(f"Fetch & Processing Time: {elapsed_sec} seconds")
        print("=" * 60 + "\n")

        assert result["destination"]["name"].lower() in ["ooty", "udhagamandalam"]
        assert len(result["recommended_places"]) > 0
        assert len(result["itinerary"]) == 4
        assert result["budget"]["user_budget"] == 20000.0
        assert len(result["data_sources"]) >= 3
    finally:
        db.close()


if __name__ == "__main__":
    test_real_ai_planner_ooty_execution()
