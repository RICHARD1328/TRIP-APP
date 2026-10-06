from sqlalchemy import Column, Integer, String, Float, Boolean, Text, DateTime
from sqlalchemy.sql import func
from app.core.database import Base


class Package(Base):
    __tablename__ = "packages"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(255), nullable=False, index=True)
    destination = Column(String(255), nullable=False, index=True)
    total_price = Column(Float, nullable=False)
    currency = Column(String(10), nullable=False, default="INR")
    duration_days = Column(Integer, nullable=False)
    duration_nights = Column(Integer, nullable=False)
    accommodation = Column(Text, nullable=False)
    transport = Column(Text, nullable=False)
    included_attractions = Column(Text, nullable=False)  # Stored as JSON string or comma-separated
    meals = Column(Text, nullable=False)
    rating = Column(Float, nullable=False, default=4.5)
    review_count = Column(Integer, nullable=False, default=50)
    exclusions = Column(Text, nullable=False)  # Stored as JSON string or comma-separated
    provider_name = Column(String(255), nullable=False, default="TripDirect Demo Operator")
    is_demo_data = Column(Boolean, nullable=False, default=True)
    data_source = Column(String(255), nullable=False, default="Demo Package Catalog - Not a real-time booking provider quote")
    image_url = Column(String(512), nullable=True)

    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now(), nullable=False)
