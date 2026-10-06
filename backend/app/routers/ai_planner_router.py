from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.schemas.ai_planner_schemas import AiPlannerRequest, AiPlannerRegenerateRequest, AiPlannerResponseSchema
from app.services import trip_planner_service

router = APIRouter()


@router.post("/plan", response_model=AiPlannerResponseSchema, status_code=status.HTTP_200_OK)
def plan_trip(
    request: AiPlannerRequest,
    db: Session = Depends(get_db)
):
    """
    Generate dynamic AI trip plan powered by Nominatim, Overpass POIs, Open-Meteo weather, and OSRM routing.
    POST /api/v1/ai-planner/plan
    """
    return trip_planner_service.generate_ai_trip_plan(db=db, request=request)


@router.post("/regenerate", response_model=AiPlannerResponseSchema, status_code=status.HTTP_200_OK)
def regenerate_trip(
    request: AiPlannerRegenerateRequest,
    db: Session = Depends(get_db)
):
    """
    Regenerate itinerary with updated preferences, dates, or budget parameters.
    POST /api/v1/ai-planner/regenerate
    """
    return trip_planner_service.regenerate_ai_trip_plan(db=db, request=request)
