from typing import Optional
from pydantic import BaseModel, Field


class NearbyPlaceItem(BaseModel):
    id: str = Field(..., description="Unique place identifier (e.g. node/12345 or way/67890)")
    name: str = Field(..., description="Name of the place")
    category: str = Field(..., description="Matched category (e.g. attraction, hotel, restaurant)")
    latitude: float = Field(..., description="Latitude coordinate")
    longitude: float = Field(..., description="Longitude coordinate")
    address: str = Field("", description="Formated address or street information")
    distance: float = Field(..., description="Distance from requested location in meters")
    image_url: Optional[str] = Field(None, description="Image URL if available from OSM metadata")
