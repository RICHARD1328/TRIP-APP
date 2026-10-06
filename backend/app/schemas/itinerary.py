from typing import List, Optional
from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class ItineraryItemCreate(BaseModel):
    place_id: Optional[str] = Field(None, description="Associated place ID")
    title: str = Field(..., description="Activity title")
    description: Optional[str] = Field(None, description="Activity description")
    start_time: Optional[str] = Field(None, description="Start time display (e.g. 09:00 AM)")
    end_time: Optional[str] = Field(None, description="End time display (e.g. 11:30 AM)")
    estimated_cost: float = Field(0.0, ge=0.0, description="Estimated cost")
    latitude: Optional[float] = Field(None, description="Latitude coordinate")
    longitude: Optional[float] = Field(None, description="Longitude coordinate")
    notes: Optional[str] = Field(None, description="Personal notes or reminders")


class ItineraryItemUpdate(BaseModel):
    title: Optional[str] = Field(None, description="Activity title")
    description: Optional[str] = Field(None, description="Activity description")
    start_time: Optional[str] = Field(None, description="Start time display")
    end_time: Optional[str] = Field(None, description="End time display")
    estimated_cost: Optional[float] = Field(None, ge=0.0, description="Estimated cost")
    latitude: Optional[float] = Field(None, description="Latitude coordinate")
    longitude: Optional[float] = Field(None, description="Longitude coordinate")
    notes: Optional[str] = Field(None, description="Personal notes or reminders")


class ItineraryItemResponse(BaseModel):
    id: int
    trip_day_id: int
    place_id: Optional[str] = None
    title: str
    description: Optional[str] = None
    start_time: Optional[str] = None
    end_time: Optional[str] = None
    estimated_cost: float
    latitude: Optional[float] = None
    longitude: Optional[float] = None
    notes: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)


class TripDayResponse(BaseModel):
    id: int
    trip_id: int
    day_number: int
    date: str
    title: str
    items: List[ItineraryItemResponse] = Field(default_factory=list)

    model_config = ConfigDict(from_attributes=True)


class TripCreate(BaseModel):
    title: str = Field(..., description="Trip title (e.g. Summer Vacation in Ooty)")
    destination: str = Field(..., description="Destination location name")
    start_date: str = Field(..., description="Start date (YYYY-MM-DD)")
    end_date: str = Field(..., description="End date (YYYY-MM-DD)")
    budget: float = Field(0.0, ge=0.0, description="Total trip budget")


class TripUpdate(BaseModel):
    title: Optional[str] = Field(None, description="Trip title")
    destination: Optional[str] = Field(None, description="Destination location name")
    start_date: Optional[str] = Field(None, description="Start date (YYYY-MM-DD)")
    end_date: Optional[str] = Field(None, description="End date (YYYY-MM-DD)")
    budget: Optional[float] = Field(None, ge=0.0, description="Total trip budget")
    status: Optional[str] = Field(None, description="Trip status (planned, ongoing, completed, cancelled)")


class TripResponse(BaseModel):
    id: int
    user_id: int
    title: str
    destination: str
    start_date: str
    end_date: str
    budget: float
    status: str
    created_at: datetime
    updated_at: datetime
    days: List[TripDayResponse] = Field(default_factory=list)

    model_config = ConfigDict(from_attributes=True)
