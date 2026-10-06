from typing import List, Optional
from pydantic import BaseModel, Field, ConfigDict


class NearbyServiceItem(BaseModel):
    id: str = Field(..., description="Unique ID or OSM identifier")
    name: str = Field(..., description="Name of the facility or service")
    type: str = Field(..., description="Service category: hospital, police, pharmacy, emergency_medical")
    latitude: float = Field(..., description="Geographic latitude")
    longitude: float = Field(..., description="Geographic longitude")
    distance_km: float = Field(..., description="Distance in kilometers from query location")
    address: Optional[str] = Field(None, description="Physical address if available")
    phone: Optional[str] = Field(None, description="Contact phone number if available")
    opening_hours: Optional[str] = Field(None, description="Operating hours if available")

    model_config = ConfigDict(from_attributes=True)


class NearbyServicesResponse(BaseModel):
    latitude: Optional[float] = Field(None, description="Query center latitude")
    longitude: Optional[float] = Field(None, description="Query center longitude")
    radius: float = Field(..., description="Search radius in meters")
    country: Optional[str] = Field(None, description="Target country name or ISO code")
    hospitals: List[NearbyServiceItem] = Field(default_factory=list)
    police_stations: List[NearbyServiceItem] = Field(default_factory=list)
    pharmacies: List[NearbyServiceItem] = Field(default_factory=list)
    emergency_medical_facilities: List[NearbyServiceItem] = Field(default_factory=list)
    total_found: int = Field(0, description="Total count of nearby services found")


class SafetyTipItem(BaseModel):
    title: str = Field(..., description="Short title of the safety tip")
    description: str = Field(..., description="Detailed explanation or advice")
    priority: str = Field("medium", description="Priority level: high, medium, low")


class SafetyTipCategory(BaseModel):
    category: str = Field(..., description="Category slug e.g. general, transportation, health, scams")
    title: str = Field(..., description="User-facing title")
    icon: Optional[str] = Field(None, description="Icon identifier for UI rendering")
    tips: List[SafetyTipItem] = Field(default_factory=list)


class SafetyTipsResponse(BaseModel):
    categories: List[SafetyTipCategory] = Field(default_factory=list)
    general_advice: List[str] = Field(default_factory=list)


class EmergencyContact(BaseModel):
    title: str = Field(..., description="Service title e.g. Police Emergency, Ambulance")
    number: str = Field(..., description="Verified emergency telephone number")
    description: str = Field(..., description="When to use this number")
    is_dialable: bool = Field(True, description="True if number is a direct dialable phone number")
    category: str = Field("general", description="Category: police, medical, fire, general, tourist")


class SafetyChecklistResponse(BaseModel):
    country: str = Field(..., description="Target country or Global")
    emergency_contacts: List[EmergencyContact] = Field(default_factory=list)
    important_documents: List[str] = Field(default_factory=list)
    medical_preparation: List[str] = Field(default_factory=list)
    travel_precautions: List[str] = Field(default_factory=list)
    local_safety_recommendations: List[str] = Field(default_factory=list)
