from sqlalchemy import Column, Integer, String, Boolean, DateTime, Text
from sqlalchemy.sql import func
from app.core.database import Base


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String(255), nullable=False)
    email = Column(String(255), unique=True, index=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
    phone_number = Column(String(50), nullable=True)
    profile_image_url = Column(String(512), nullable=True)
    date_of_birth = Column(String(50), nullable=True)
    preferred_language = Column(String(50), default="en", nullable=True)
    preferred_currency = Column(String(10), default="INR", nullable=True)
    travel_preferences = Column(Text, nullable=True)  # JSON or comma-separated list
    interests = Column(Text, nullable=True)  # JSON or comma-separated list
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now(), nullable=False)
    is_active = Column(Boolean, default=True, nullable=False)
