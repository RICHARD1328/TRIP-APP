from typing import List, Dict, Any, Optional
from pydantic import BaseModel, Field, model_validator
from datetime import datetime, timezone
from app.schemas.social_content_schemas import SocialContentSchema


class AiPlannerRequest(BaseModel):
    origin: Optional[str] = Field("Chennai", description="Starting location name (e.g. Chennai)")
    from_location: Optional[str] = Field(None, description="Alias for starting location")
    destination: str = Field(..., description="Destination location name (e.g. Ooty, Munnar)")
    start_date: str = Field(..., description="Start date in YYYY-MM-DD format")
    end_date: str = Field(..., description="End date in YYYY-MM-DD format")
    travelers: Optional[int] = Field(2, ge=1, description="Number of travelers (minimum 1)")
    number_of_travelers: Optional[int] = Field(None, ge=1, description="Alias for number of travelers")
    budget: float = Field(..., gt=0, description="Total budget in currency amount")
    interests: List[str] = Field(default_factory=list, description="User interests (e.g. nature, photography, food)")
    travel_style: Optional[str] = Field("balanced", description="Pace/style: relaxed, balanced, active, budget")
    transport_preference: Optional[str] = Field("car", description="Preferred transport mode: car, bus, train, flight")
    accommodation_preference: Optional[str] = Field("budget", description="Preferred hotel tier: budget, mid-range, luxury")

    @model_validator(mode="before")
    @classmethod
    def normalize_aliases(cls, data: Any) -> Any:
        if isinstance(data, dict):
            if not data.get("origin"):
                data["origin"] = data.get("from") or data.get("from_location") or "Chennai"
            if not data.get("travelers") and data.get("number_of_travelers"):
                data["travelers"] = data.get("number_of_travelers")
        return data


class AiPlannerRegenerateRequest(AiPlannerRequest):
    trip_id: Optional[str] = Field(None, description="Existing trip ID to regenerate")


class DestinationInfo(BaseModel):
    name: str
    latitude: float
    longitude: float
    city: str
    state: str
    country: str


class WeatherTemperature(BaseModel):
    min: float
    max: float
    unit: str = "°C"


class WeatherItem(BaseModel):
    date: str
    temperature: WeatherTemperature
    precipitation_probability: int
    weather_condition: str
    wind_speed: float
    forecast_available: bool = True


class RecommendedPlace(BaseModel):
    place_id: str
    name: str
    category: str
    latitude: float
    longitude: float
    address: Optional[str] = None
    website: Optional[str] = None
    phone: Optional[str] = None
    opening_hours: Optional[str] = None
    image_url: Optional[str] = None
    score: float
    reasons: List[str] = Field(default_factory=list)
    source: str = "OpenStreetMap / Overpass API"


class RouteItem(BaseModel):
    from_location: str
    to_location: str
    distance_km: float
    duration_minutes: float
    geometry: Optional[Dict[str, Any]] = None
    source: str


class ItinerarySlotSchema(BaseModel):
    slot_id: str
    time_of_day: str
    activity: str
    place_id: Optional[str] = None
    description: str
    location: str
    category: str
    image_url: Optional[str] = None
    estimated_cost: float
    weather_note: Optional[str] = None


class DailyItinerarySchema(BaseModel):
    day_number: int
    date: str
    title: str
    weather_summary: str
    slots: List[ItinerarySlotSchema]


class BudgetCategoryBreakdown(BaseModel):
    transportation: float
    accommodation: float
    food: float
    activities: float
    local_travel: float
    other: float
    emergency_reserve: float


class BudgetSummarySchema(BaseModel):
    categories: BudgetCategoryBreakdown
    total_estimated_cost: float
    cost_per_traveler: float
    cost_per_day: float
    user_budget: float
    remaining_budget: float
    is_within_budget: bool
    is_live_pricing: bool = False
    price_data_source: str


class DataSourceMetadata(BaseModel):
    source: str
    fetched_at: str = Field(default_factory=lambda: datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"))
    status: str
    cached: bool = False


# Legacy backwards-compatibility models
class TripSummaryLegacy(BaseModel):
    destination: str
    start_date: str
    end_date: str
    duration_days: int
    number_of_travelers: int
    travel_style: str = "balanced"
    scoring_algorithm_used: str = "Transparent Weighted Scoring Algorithm (30% Interest + 25% Budget + 20% Rating + 15% Proximity + 10% Preference)"


class EstimatedCostBreakdownLegacy(BaseModel):
    transport_cost: float = 0.0
    accommodation_cost: float = 0.0
    food_cost: float = 0.0
    activities_cost: float = 0.0
    total_estimated_cost: float
    user_budget: float
    budget_difference: float = 0.0
    is_within_budget: bool


class AiPlannerResponseSchema(BaseModel):
    trip_id: str
    destination: DestinationInfo
    origin: DestinationInfo
    weather: List[WeatherItem]
    recommended_places: List[RecommendedPlace]
    itinerary: List[DailyItinerarySchema]
    routes: List[RouteItem]
    budget: BudgetSummarySchema
    data_sources: List[DataSourceMetadata]
    generated_at: str
    live_data: bool
    real_visuals: List[SocialContentSchema] = Field(default_factory=list, description="Destination Reels for user preview")

    # Backwards-compatibility aliases for legacy test support
    trip_summary: Optional[TripSummaryLegacy] = None
    estimated_cost_breakdown: Optional[EstimatedCostBreakdownLegacy] = None
    daily_itinerary: Optional[List[DailyItinerarySchema]] = None
    recommended_attractions: Optional[List[Dict[str, Any]]] = None
    hotel_suggestions: Optional[List[Dict[str, Any]]] = None
    transport_suggestions: Optional[List[Dict[str, Any]]] = None
