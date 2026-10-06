from fastapi import APIRouter
from app.schemas import HealthResponse

router = APIRouter()


@router.get("/health", response_model=HealthResponse)
def get_health():
    """
    Health check endpoint to verify backend service status.
    GET /api/v1/health
    """
    return {
        "status": "ok",
        "service": "TripDirect Backend"
    }
