import re
from typing import Optional
from pydantic import BaseModel, Field, field_validator


INSTAGRAM_URL_REGEX = re.compile(
    r"^https?://(www\.)?instagram\.com/(reel|reels|p|tv)/[A-Za-z0-9_-]+/?(\?.*)?$",
    re.IGNORECASE
)


class SocialContentSchema(BaseModel):
    id: str
    destination_id: str
    platform: str = "instagram"
    title: str
    reel_url: str
    thumbnail_url: Optional[str] = None
    creator_name: Optional[str] = None
    description: Optional[str] = None
    verified: bool = True
    active: bool = True


class CreateSocialContentRequest(BaseModel):
    destination_id: str = Field(..., description="Target destination ID or name (e.g. munnar, ooty)")
    platform: str = Field("instagram", description="Social media platform name")
    title: str = Field(..., description="Title of the visual reel content")
    reel_url: str = Field(..., description="Public Instagram Reel/Post URL")
    thumbnail_url: Optional[str] = Field(None, description="Preview thumbnail image URL")
    creator_name: Optional[str] = Field(None, description="Creator attribution name")
    description: Optional[str] = Field(None, description="Short caption or description")
    verified: bool = Field(True, description="Application content process verification status")
    active: bool = Field(True, description="Whether content is enabled for display")

    @field_validator("reel_url")
    @classmethod
    def validate_instagram_url(cls, v: str) -> str:
        url_str = v.strip()
        if not INSTAGRAM_URL_REGEX.match(url_str):
            raise ValueError("URL must be a valid public Instagram Reel/Post URL (e.g. https://www.instagram.com/reel/...).")
        return url_str


class UpdateSocialContentRequest(BaseModel):
    destination_id: Optional[str] = None
    platform: Optional[str] = None
    title: Optional[str] = None
    reel_url: Optional[str] = None
    thumbnail_url: Optional[str] = None
    creator_name: Optional[str] = None
    description: Optional[str] = None
    verified: Optional[bool] = None
    active: Optional[bool] = None

    @field_validator("reel_url")
    @classmethod
    def validate_instagram_url_if_present(cls, v: Optional[str]) -> Optional[str]:
        if v is not None:
            url_str = v.strip()
            if not INSTAGRAM_URL_REGEX.match(url_str):
                raise ValueError("URL must be a valid public Instagram Reel/Post URL.")
            return url_str
        return v
