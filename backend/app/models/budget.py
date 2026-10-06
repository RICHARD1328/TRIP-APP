from sqlalchemy import Column, Integer, String, Float, Text, DateTime
from sqlalchemy.sql import func
from app.core.database import Base


class SavedBudget(Base):
    __tablename__ = "saved_budgets"

    id = Column(Integer, primary_key=True, index=True)
    trip_id = Column(String(100), unique=True, index=True, nullable=False)
    number_of_travelers = Column(Integer, nullable=False, default=1)
    number_of_days = Column(Integer, nullable=False, default=1)
    subtotal = Column(Float, nullable=False)
    emergency_reserve = Column(Float, nullable=False)
    total_budget = Column(Float, nullable=False)
    per_person_cost = Column(Float, nullable=False)
    per_day_cost = Column(Float, nullable=False)
    budget_json = Column(Text, nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
