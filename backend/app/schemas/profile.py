from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, EmailStr, Field, ConfigDict


class ProfileResponse(BaseModel):
    id: int
    full_name: str
    email: EmailStr
    phone_number: Optional[str] = Field(None, description="User's contact phone number")
    profile_image_url: Optional[str] = Field(None, description="Profile avatar picture URL")
    date_of_birth: Optional[str] = Field(None, description="Date of birth in YYYY-MM-DD format")
    preferred_language: Optional[str] = Field("en", description="Preferred application language (e.g. en, hi, es)")
    preferred_currency: Optional[str] = Field("INR", description="Preferred currency code (e.g. INR, USD, EUR)")
    travel_preferences: List[str] = Field(default_factory=list, description="List of travel preferences")
    interests: List[str] = Field(default_factory=list, description="List of personal interests and hobbies")
    created_at: datetime
    updated_at: datetime
    is_active: bool

    model_config = ConfigDict(from_attributes=True)


class ProfileUpdateRequest(BaseModel):
    full_name: Optional[str] = Field(None, min_length=1, max_length=255, description="Updated full name")
    email: Optional[EmailStr] = Field(None, description="Updated email address")
    phone_number: Optional[str] = Field(None, max_length=50, description="Updated contact phone number")
    date_of_birth: Optional[str] = Field(None, max_length=50, description="Updated date of birth (YYYY-MM-DD)")
    preferred_language: Optional[str] = Field(None, max_length=50, description="Updated preferred language code")
    preferred_currency: Optional[str] = Field(None, max_length=10, description="Updated preferred currency code")
    travel_preferences: Optional[List[str]] = Field(None, description="Updated travel preferences list")
    interests: Optional[List[str]] = Field(None, description="Updated interests list")


class ProfileImageResponse(BaseModel):
    profile_image_url: str
    message: str = "Profile image updated successfully"
