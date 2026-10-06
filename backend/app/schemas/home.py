from typing import List
from pydantic import BaseModel, ConfigDict


class PlaceItemResponse(BaseModel):
    id: int
    name: str
    location: str
    latitude: float
    longitude: float
    image_url: str
    rating: float
    category: str
    short_description: str

    model_config = ConfigDict(from_attributes=True)


class PaginatedPlaceResponse(BaseModel):
    total: int
    page: int
    size: int
    items: List[PlaceItemResponse]


class QuickStatistics(BaseModel):
    total_destinations: int
    total_attractions: int
    total_hotels: int
    satisfied_travelers: str = "10,000+"


class HomeDashboardResponse(BaseModel):
    popular_destinations: List[PlaceItemResponse]
    popular_attractions: List[PlaceItemResponse]
    recommended_hotels: List[PlaceItemResponse]
    featured_places: List[PlaceItemResponse]
    quick_statistics: QuickStatistics
