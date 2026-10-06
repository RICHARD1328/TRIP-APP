from typing import Optional
from pydantic import BaseModel, Field


class TransportSearchItem(BaseModel):
    transport_id: str = Field(..., description="Unique transport option identifier")
    type: str = Field(..., description="Transport category (bus, train, flight, taxi, car)")
    operator: str = Field(..., description="Name of the service provider or operator")
    origin: str = Field(..., description="Departure city or location")
    destination: str = Field(..., description="Arrival city or destination")
    departure_time: str = Field(..., description="Scheduled departure time")
    arrival_time: str = Field(..., description="Scheduled arrival time")
    duration: str = Field(..., description="Total travel duration display string (e.g. 5h 30m)")
    duration_minutes: int = Field(..., description="Total travel duration in minutes for sorting")
    price: float = Field(..., description="Price per passenger or rental unit in local currency")
    booking_url: Optional[str] = Field(None, description="Direct booking or partner URL if available")
    source_type: str = Field("demo", description="Source data type flag ('demo' or 'local') to clarify schedule provenance")
