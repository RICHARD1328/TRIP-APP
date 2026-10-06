from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field, ConfigDict


class PackageResponse(BaseModel):
    id: int
    title: str
    destination: str
    total_price: float
    currency: str = "INR"
    duration_days: int
    duration_nights: int
    accommodation: str
    transport: str
    included_attractions: List[str]
    meals: str
    rating: float
    review_count: int
    exclusions: List[str]
    provider_name: str
    is_demo_data: bool = True
    data_source: str
    image_url: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)


class PackageListResponse(BaseModel):
    total: int
    packages: List[PackageResponse]
    is_demo_data: bool = True
    disclaimer: str = "Package prices and inclusions shown are demo estimates and not connected to a live real-time booking provider."


class ImportanceWeights(BaseModel):
    price: float = Field(0.30, ge=0.0, le=1.0, description="Weight for price score")
    rating: float = Field(0.25, ge=0.0, le=1.0, description="Weight for rating score")
    inclusions: float = Field(0.25, ge=0.0, le=1.0, description="Weight for inclusions score")
    duration: float = Field(0.20, ge=0.0, le=1.0, description="Weight for duration score")


class UserPreferences(BaseModel):
    max_budget: Optional[float] = Field(None, description="Preferred maximum budget")
    preferred_duration_days: Optional[int] = Field(None, description="Preferred duration in days")
    min_rating: Optional[float] = Field(None, description="Minimum acceptable package rating")
    required_inclusions: List[str] = Field(default_factory=list, description="Must-have attractions or amenities")
    importance_weights: ImportanceWeights = Field(default_factory=ImportanceWeights, description="Custom weights for scoring factors")


class ComparePackagesRequest(BaseModel):
    package_ids: List[int] = Field(..., min_length=2, max_length=10, description="List of at least 2 package IDs to compare")
    user_preferences: Optional[UserPreferences] = Field(None, description="Optional user preferences for personalized comparison scoring")


class ScoreBreakdown(BaseModel):
    price_score: float = Field(..., description="Normalized price score (0-100)")
    rating_score: float = Field(..., description="Normalized rating score (0-100)")
    inclusions_score: float = Field(..., description="Normalized inclusions score (0-100)")
    duration_score: float = Field(..., description="Normalized duration score (0-100)")
    preference_match_score: float = Field(..., description="Preference alignment match score (0-100)")


class ComparedPackage(BaseModel):
    package: PackageResponse
    overall_score: float = Field(..., description="Calculated composite comparison score (0-100)")
    score_breakdown: ScoreBreakdown
    badge: Optional[str] = Field(None, description="Highlights badge e.g. Best Overall, Cheapest, Top Rated, Most Inclusive")
    highlights: List[str] = Field(default_factory=list, description="Key package pros/strengths")
    drawbacks: List[str] = Field(default_factory=list, description="Key package cons/limitations")


class AttributeComparisonItem(BaseModel):
    attribute_key: str = Field(..., description="Identifier for attribute e.g. total_price, duration, accommodation")
    attribute_label: str = Field(..., description="Human-readable label for attribute e.g. Total Price, Duration")
    values: Dict[str, Any] = Field(..., description="Map of package ID (as string) to attribute value")
    winner_package_id: Optional[int] = Field(None, description="ID of winning package for this attribute, if applicable")
    note: Optional[str] = Field(None, description="Summary comment or observation on comparison")


class ComparisonSummary(BaseModel):
    best_overall_package_id: int
    cheapest_package_id: int
    highest_rated_package_id: int
    most_inclusive_package_id: int


class PackageComparisonResponse(BaseModel):
    compared_packages: List[ComparedPackage]
    summary: ComparisonSummary
    comparison_matrix: List[AttributeComparisonItem]
    is_demo_data: bool = True
    disclaimer: str = "Package prices, inclusions, and availability shown are demo data and not connected to a real-time live provider."
