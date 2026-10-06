from typing import List, Optional, Any
from pydantic import BaseModel, Field
from app.schemas.home import PlaceItemResponse
from app.schemas.hotel import HotelSearchItem
from app.schemas.transport import TransportSearchItem


class TripPlanRequest(BaseModel):
    destination: str = Field(..., description="Destination city or region name")
    start_date: str = Field(..., description="Trip start date (YYYY-MM-DD)")
    end_date: str = Field(..., description="Trip end date (YYYY-MM-DD)")
    budget: float = Field(..., gt=0, description="Total travel budget in local currency")
    number_of_travelers: int = Field(1, ge=1, le=50, description="Number of travelers")
    interests: List[str] = Field(..., description="List of user interests (e.g. nature, adventure, beach, history, food, photography)")
    travel_style: Optional[str] = Field("balanced", description="Travel style (budget, relaxed, balanced, luxury)")
    accommodation_preference: Optional[str] = Field("hotel", description="Accommodation preference (budget_hotel, hotel, resort, homestay)")
    transport_preference: Optional[str] = Field("bus", description="Transport preference (bus, train, flight, taxi, car)")


class ItinerarySlot(BaseModel):
    time_of_day: str = Field(..., description="Time of day (Morning, Afternoon, Evening)")
    activity: str = Field(..., description="Name of the activity or attraction")
    description: str = Field(..., description="Activity details or recommendations")
    estimated_cost: float = Field(0.0, description="Estimated activity cost per person")
    location: str = Field("", description="Activity location")
    category: str = Field("", description="Activity category")


class DailyItinerary(BaseModel):
    day_number: int = Field(..., description="Day number (1, 2, 3...)")
    date: str = Field(..., description="Formatted date string")
    title: str = Field(..., description="Day title overview")
    slots: List[ItinerarySlot] = Field(default_factory=list, description="Morning, Afternoon, and Evening activity slots")


class EstimatedCostBreakdown(BaseModel):
    transport_cost: float = Field(..., description="Estimated total transport cost")
    accommodation_cost: float = Field(..., description="Estimated total accommodation cost")
    activities_cost: float = Field(..., description="Estimated total activities and entry fees cost")
    food_cost: float = Field(..., description="Estimated total food and local transport cost")
    total_estimated_cost: float = Field(..., description="Sum of all estimated expenses")
    user_budget: float = Field(..., description="Original user budget constraint")
    budget_difference: float = Field(..., description="Budget minus estimated cost (positive = under budget)")
    is_within_budget: bool = Field(..., description="True if estimated cost <= user budget")


class TripSummary(BaseModel):
    destination: str = Field(..., description="Destination name")
    start_date: str = Field(..., description="Start date")
    end_date: str = Field(..., description="End date")
    duration_days: int = Field(..., description="Total trip duration in days")
    number_of_travelers: int = Field(..., description="Number of travelers")
    travel_style: str = Field(..., description="Selected travel style")
    scoring_algorithm_used: str = Field(
        "Transparent Weighted Scoring Algorithm (30% Interest + 25% Budget + 20% Rating + 15% Proximity + 10% Preference)",
        description="Explicit description of heuristic scoring model"
    )


class TripPlanResponse(BaseModel):
    trip_id: str = Field(..., description="Unique generated trip ID")
    trip_summary: TripSummary = Field(..., description="High-level trip summary")
    estimated_cost_breakdown: EstimatedCostBreakdown = Field(..., description="Detailed cost breakdown vs budget")
    daily_itinerary: List[DailyItinerary] = Field(..., description="Day-by-day itinerary schedule")
    recommended_attractions: List[PlaceItemResponse] = Field(..., description="Top ranked attractions")
    hotel_suggestions: List[HotelSearchItem] = Field(..., description="Hotel suggestions")
    transport_suggestions: List[TransportSearchItem] = Field(..., description="Transport options")
    created_at: str = Field(..., description="ISO creation timestamp")


class SaveTripRequest(BaseModel):
    trip_plan: Any = Field(..., description="Trip plan object to save")
