from pydantic import BaseModel
from app.schemas.user import (
    UserCreate,
    UserLogin,
    UserResponse,
    TokenResponse,
    TokenRefreshResponse,
    MessageResponse,
)
from app.schemas.home import (
    PlaceItemResponse,
    PaginatedPlaceResponse,
    QuickStatistics,
    HomeDashboardResponse,
)
from app.schemas.destination import DestinationSearchItem


class HealthResponse(BaseModel):
    status: str
    service: str


__all__ = [
    "HealthResponse",
    "UserCreate",
    "UserLogin",
    "UserResponse",
    "TokenResponse",
    "TokenRefreshResponse",
    "MessageResponse",
    "PlaceItemResponse",
    "PaginatedPlaceResponse",
    "QuickStatistics",
    "HomeDashboardResponse",
    "DestinationSearchItem",
]
