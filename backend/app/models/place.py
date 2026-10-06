from sqlalchemy import Column, Integer, String, Float, Boolean, Text, DateTime
from sqlalchemy.sql import func
from app.core.database import Base


class PlaceItem(Base):
    __tablename__ = "places"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), nullable=False)
    location = Column(String(255), nullable=False)
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    image_url = Column(String(512), nullable=False)
    rating = Column(Float, nullable=False, default=4.5)
    category = Column(String(100), nullable=False)  # e.g., "Destination", "Attraction", "Hotel", "Featured"
    short_description = Column(Text, nullable=False)

    is_popular_destination = Column(Boolean, default=False, index=True)
    is_popular_attraction = Column(Boolean, default=False, index=True)
    is_recommended_hotel = Column(Boolean, default=False, index=True)
    is_featured = Column(Boolean, default=False, index=True)

    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now(), nullable=False)
