from datetime import datetime
from typing import List, Optional, Dict
from pydantic import BaseModel, Field, ConfigDict


class CreateReviewRequest(BaseModel):
    target_type: str = Field(..., description="Target type e.g. destination, hotel, attraction")
    target_id: str = Field(..., min_length=1, description="Unique identifier for the target entity")
    rating: float = Field(..., ge=1.0, le=5.0, description="Rating score between 1.0 and 5.0")
    title: str = Field(..., min_length=1, max_length=255, description="Review title or summary")
    comment: str = Field(..., min_length=1, max_length=2000, description="Detailed review comment")


class UpdateReviewRequest(BaseModel):
    rating: Optional[float] = Field(None, ge=1.0, le=5.0, description="Updated rating score between 1.0 and 5.0")
    title: Optional[str] = Field(None, min_length=1, max_length=255, description="Updated review title")
    comment: Optional[str] = Field(None, min_length=1, max_length=2000, description="Updated review comment")


class ReviewResponse(BaseModel):
    id: int
    user_id: int
    user_name: Optional[str] = Field(None, description="Reviewer full name")
    user_avatar: Optional[str] = Field(None, description="Reviewer profile image URL")
    target_type: str
    target_id: str
    rating: float
    title: str
    comment: str
    created_at: datetime
    updated_at: datetime

    model_config = ConfigDict(from_attributes=True)


class ReviewListResponse(BaseModel):
    reviews: List[ReviewResponse]
    total_reviews: int
    average_rating: float
    rating_breakdown: Dict[int, int]  # Map of star count (1..5) -> count
    page: int
    limit: int
    total_pages: int
