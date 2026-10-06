import uuid
from typing import List, Dict, Any, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.core.security import get_current_user_optional, get_current_user
from app.models.user import User
from app.schemas.ai_planner_schemas import AiPlannerRequest, AiPlannerResponseSchema
from app.schemas.trip import SaveTripRequest
from app.services import trip_planner_service
from app.repositories import trip_repository

router = APIRouter()


@router.post("/plan", response_model=AiPlannerResponseSchema, status_code=status.HTTP_200_OK)
def plan_trip(
    request: AiPlannerRequest,
    db: Session = Depends(get_db)
):
    """
    Generate customized AI trip plan with transparent scoring algorithm, budget optimization,
    day-by-day itinerary, weather forecast, and OSRM routing.
    POST /api/v1/trips/plan
    """
    return trip_planner_service.generate_ai_trip_plan(db=db, request=request)


@router.post("/save", status_code=status.HTTP_201_CREATED)
def save_trip_legacy(
    request: SaveTripRequest,
    current_user: Optional[User] = Depends(get_current_user_optional),
    db: Session = Depends(get_db)
):
    """
    Legacy Save Trip endpoint.
    POST /api/v1/trips/save
    """
    plan = request.trip_plan
    if isinstance(plan, dict):
        trip_id = plan.get("trip_id") or f"trip_{uuid.uuid4().hex[:8]}"
        dest = plan.get("trip_summary", {}).get("destination") if isinstance(plan.get("trip_summary"), dict) else None
        if not dest:
            dest = plan.get("destination", {}).get("name") if isinstance(plan.get("destination"), dict) else "Destination"

        start_date = plan.get("trip_summary", {}).get("start_date") if isinstance(plan.get("trip_summary"), dict) else "2026-09-10"
        end_date = plan.get("trip_summary", {}).get("end_date") if isinstance(plan.get("trip_summary"), dict) else "2026-09-13"
        duration_days = plan.get("trip_summary", {}).get("duration_days") if isinstance(plan.get("trip_summary"), dict) else 3
        travelers = plan.get("trip_summary", {}).get("number_of_travelers") if isinstance(plan.get("trip_summary"), dict) else 2
        user_budget = plan.get("estimated_cost_breakdown", {}).get("user_budget") if isinstance(plan.get("estimated_cost_breakdown"), dict) else 10000.0
        est_cost = plan.get("estimated_cost_breakdown", {}).get("total_estimated_cost") if isinstance(plan.get("estimated_cost_breakdown"), dict) else user_budget
        plan_dict = plan
    else:
        trip_id = plan.trip_id
        dest = plan.trip_summary.destination
        start_date = plan.trip_summary.start_date
        end_date = plan.trip_summary.end_date
        duration_days = plan.trip_summary.duration_days
        travelers = plan.trip_summary.number_of_travelers
        user_budget = plan.estimated_cost_breakdown.user_budget
        est_cost = plan.estimated_cost_breakdown.total_estimated_cost
        plan_dict = plan.model_dump() if hasattr(plan, "model_dump") else dict(plan)

    user_id = current_user.id if current_user else None
    saved = trip_repository.save_trip_plan(
        db=db,
        trip_id=str(trip_id),
        user_id=user_id,
        title=f"Trip to {dest}",
        destination=str(dest),
        start_date=str(start_date),
        end_date=str(end_date),
        duration_days=int(duration_days),
        number_of_travelers=int(travelers),
        budget=float(user_budget),
        estimated_total_cost=float(est_cost),
        plan_dict=plan_dict
    )
    return {
        "message": "Trip saved successfully.",
        "trip_id": saved.trip_id,
        "title": saved.title
    }


# ==========================================
# RESTFUL SAVED TRIPS API (Specification #15)
# ==========================================

@router.post("", status_code=status.HTTP_201_CREATED)
def create_saved_trip(
    trip_payload: Dict[str, Any],
    current_user: Optional[User] = Depends(get_current_user_optional),
    db: Session = Depends(get_db)
):
    """
    Save a trip plan.
    POST /api/v1/trips
    """
    trip_id = trip_payload.get("trip_id") or f"trip_{uuid.uuid4().hex[:8]}"
    dest_name = trip_payload.get("destination", {}).get("name") if isinstance(trip_payload.get("destination"), dict) else (trip_payload.get("destination") or "Destination")
    budget_val = trip_payload.get("budget", {}).get("user_budget") if isinstance(trip_payload.get("budget"), dict) else 10000.0
    est_cost = trip_payload.get("budget", {}).get("total_estimated_cost") if isinstance(trip_payload.get("budget"), dict) else budget_val
    itinerary = trip_payload.get("itinerary", [])
    duration_days = len(itinerary) if itinerary else 1
    start_date = itinerary[0].get("date", "2026-09-10") if itinerary and isinstance(itinerary[0], dict) else "2026-09-10"
    end_date = itinerary[-1].get("date", "2026-09-13") if itinerary and isinstance(itinerary[-1], dict) else "2026-09-13"
    user_id = current_user.id if current_user else None

    saved = trip_repository.save_trip_plan(
        db=db,
        trip_id=str(trip_id),
        user_id=user_id,
        title=f"Trip to {dest_name}",
        destination=str(dest_name),
        start_date=str(start_date),
        end_date=str(end_date),
        duration_days=duration_days,
        number_of_travelers=2,
        budget=float(budget_val),
        estimated_total_cost=float(est_cost),
        plan_dict=trip_payload
    )

    return {
        "message": "Trip saved successfully.",
        "trip_id": saved.trip_id,
        "title": saved.title
    }


@router.get("", status_code=status.HTTP_200_OK)
def list_saved_trips(
    current_user: Optional[User] = Depends(get_current_user_optional),
    db: Session = Depends(get_db)
):
    """
    Get all saved trips. User only accesses their own saved trips if authenticated.
    GET /api/v1/trips
    """
    user_id = current_user.id if current_user else None
    return trip_repository.list_user_trips(db=db, user_id=user_id)


@router.get("/{trip_id}", status_code=status.HTTP_200_OK)
def get_saved_trip_by_id(
    trip_id: str,
    current_user: Optional[User] = Depends(get_current_user_optional),
    db: Session = Depends(get_db)
):
    """
    Retrieve a specific saved trip plan by trip_id.
    GET /api/v1/trips/{trip_id}
    """
    user_id = current_user.id if current_user else None
    try:
        plan_dict = trip_repository.get_saved_trip(db=db, trip_id=trip_id, user_id=user_id)
    except PermissionError as e:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=str(e))

    if not plan_dict:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Saved trip with ID '{trip_id}' not found."
        )
    return plan_dict


@router.put("/{trip_id}", status_code=status.HTTP_200_OK)
def update_saved_trip_by_id(
    trip_id: str,
    trip_payload: Dict[str, Any],
    current_user: Optional[User] = Depends(get_current_user_optional),
    db: Session = Depends(get_db)
):
    """
    Update a specific saved trip plan by trip_id.
    PUT /api/v1/trips/{trip_id}
    """
    user_id = current_user.id if current_user else None
    try:
        updated = trip_repository.update_saved_trip(db=db, trip_id=trip_id, plan_dict=trip_payload, user_id=user_id)
    except PermissionError as e:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=str(e))

    if not updated:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Saved trip with ID '{trip_id}' not found.")

    return updated


@router.delete("/{trip_id}", status_code=status.HTTP_200_OK)
def delete_saved_trip_by_id(
    trip_id: str,
    current_user: Optional[User] = Depends(get_current_user_optional),
    db: Session = Depends(get_db)
):
    """
    Delete a specific saved trip plan by trip_id.
    DELETE /api/v1/trips/{trip_id}
    """
    user_id = current_user.id if current_user else None
    try:
        success = trip_repository.delete_saved_trip(db=db, trip_id=trip_id, user_id=user_id)
    except PermissionError as e:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=str(e))

    if not success:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Saved trip with ID '{trip_id}' not found.")

    return {"message": "Trip deleted successfully.", "trip_id": trip_id}
