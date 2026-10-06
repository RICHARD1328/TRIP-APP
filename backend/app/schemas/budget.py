from typing import Optional, Dict
from pydantic import BaseModel, Field


class BudgetCalculateRequest(BaseModel):
    trip_id: Optional[str] = Field(None, description="Optional trip ID to associate budget calculation with")
    number_of_travelers: int = Field(1, ge=1, le=100, description="Total number of travelers")
    number_of_days: int = Field(1, ge=1, le=365, description="Total number of trip days")
    accommodation_cost: float = Field(0.0, ge=0.0, description="Total accommodation expense")
    transport_cost: float = Field(0.0, ge=0.0, description="Total transit/travel expense")
    food_cost: float = Field(0.0, ge=0.0, description="Total dining/food expense")
    attraction_cost: float = Field(0.0, ge=0.0, description="Total entry fees & activities expense")
    shopping_cost: float = Field(0.0, ge=0.0, description="Total shopping expense")
    emergency_reserve: float = Field(0.0, ge=0.0, description="Emergency reserve allocation")
    other_cost: float = Field(0.0, ge=0.0, description="Miscellaneous expenses")


class ExpenseCategoryBreakdown(BaseModel):
    accommodation_cost: float = Field(..., description="Accommodation total")
    transport_cost: float = Field(..., description="Transport total")
    food_cost: float = Field(..., description="Food total")
    attraction_cost: float = Field(..., description="Attractions total")
    shopping_cost: float = Field(..., description="Shopping total")
    other_cost: float = Field(..., description="Other cost total")
    subtotal: float = Field(..., description="Subtotal before emergency reserve")
    emergency_reserve: float = Field(..., description="Emergency reserve total")


class BudgetSummary(BaseModel):
    subtotal: float = Field(..., description="Sum of raw category expenses")
    emergency_reserve: float = Field(..., description="Emergency buffer amount")
    total_budget: float = Field(..., description="Subtotal + emergency reserve")
    per_person_cost: float = Field(..., description="Total budget divided by travelers")
    per_day_cost: float = Field(..., description="Total budget divided by days")


class BudgetCalculateResponse(BaseModel):
    trip_id: Optional[str] = Field(None, description="Associated trip ID")
    number_of_travelers: int = Field(..., description="Traveler count")
    number_of_days: int = Field(..., description="Day count")
    breakdown: ExpenseCategoryBreakdown = Field(..., description="Itemized expense category breakdown")
    summary: BudgetSummary = Field(..., description="Calculated budget summary metrics")
    category_percentages: Dict[str, float] = Field(..., description="Percentage share of each category")
    created_at: str = Field(..., description="ISO calculation timestamp")
