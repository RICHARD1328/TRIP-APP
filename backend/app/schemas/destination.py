from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field
from app.schemas.social_content_schemas import SocialContentSchema


class DestinationSearchItem(BaseModel):
    place_name: str = Field(..., description="Short name of the location")
    display_name: str = Field(..., description="Full display address")
    latitude: float = Field(..., description="Latitude coordinate")
    longitude: float = Field(..., description="Longitude coordinate")
    country: str = Field("", description="Country name")
    state: str = Field("", description="State or region name")
    city: str = Field("", description="City, town, or municipality name")
    place_type: str = Field("", description="Type of place (e.g. city, town, attraction)")


class DestinationDetailResponse(BaseModel):
    id: str = Field(..., description="Destination ID or slug")
    name: str = Field(..., description="Destination display name (e.g. Munnar)")
    location: str = Field("", description="State / Country location string")
    latitude: float = Field(..., description="Latitude coordinate")
    longitude: float = Field(..., description="Longitude coordinate")
    description: str = Field("", description="Detailed destination overview")
    images: List[str] = Field(default_factory=list, description="Destination photo URLs")
    attractions: List[Dict[str, Any]] = Field(default_factory=list, description="Top destination attractions")
    weather: Optional[Dict[str, Any]] = Field(None, description="Current or forecast weather summary")
    social_content: List[SocialContentSchema] = Field(default_factory=list, description="Verified destination social media Reels")
