from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.schemas.budget import BudgetCalculateRequest, BudgetCalculateResponse
from app.services import budget_service

router = APIRouter()


@router.post("/calculate", response_model=BudgetCalculateResponse, status_code=status.HTTP_200_OK)
def calculate_budget(
    request: BudgetCalculateRequest,
    db: Session = Depends(get_db)
):
    """
    Perform decimal-safe budget calculation for travel expenses.
    POST /api/v1/budget/calculate
    """
    return budget_service.calculate_budget(db=db, request=request)


@router.get("/{trip_id}", status_code=status.HTTP_200_OK)
def get_budget_by_trip_id(
    trip_id: str,
    db: Session = Depends(get_db)
):
    """
    Retrieve saved budget calculation breakdown by trip_id.
    GET /api/v1/budget/{trip_id}
    """
    record = budget_service.get_budget_for_trip(db=db, trip_id=trip_id)
    if not record:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"No budget calculation found for trip ID '{trip_id}'."
        )
    return record
