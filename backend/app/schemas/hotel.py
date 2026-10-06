from typing import List, Optional
from pydantic import BaseModel, Field


class HotelSearchItem(BaseModel):
    hotel_id: str = Field(..., description="Unique hotel identifier")
    name: str = Field(..., description="Name of the hotel")
    location: str = Field(..., description="Location address or area description")
    latitude: float = Field(..., description="Latitude coordinate")
    longitude: float = Field(..., description="Longitude coordinate")
    image_url: str = Field(..., description="Image URL of the hotel property")
    rating: float = Field(..., description="Guest rating out of 5.0")
    price_per_night: float = Field(..., description="Price per night in local currency")
    available_rooms: int = Field(..., description="Estimated/demo available room count")
    amenities: List[str] = Field(default_factory=list, description="List of hotel amenities")
    description: str = Field("", description="Short description of the hotel")
    distance_km: Optional[float] = Field(None, description="Calculated distance in kilometers from search location")
    source_type: str = Field("demo", description="Source data type flag ('demo' or 'local') to clarify availability provenance")
