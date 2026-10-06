import uuid
from datetime import datetime
from sqlalchemy import Column, String, Boolean, DateTime, Text
from app.core.database import Base


class DestinationSocialContent(Base):
    """
    SQLAlchemy model for storing verified social media visual content (Reels/Shorts)
    associated with specific destinations.
    """
    __tablename__ = "destination_social_content"

    id = Column(String, primary_key=True, default=lambda: f"social_{uuid.uuid4().hex[:10]}")
    destination_id = Column(String, index=True, nullable=False)
    platform = Column(String, default="instagram", nullable=False)
    title = Column(String, nullable=False)
    reel_url = Column(String, nullable=False)
    thumbnail_url = Column(String, nullable=True)
    creator_name = Column(String, nullable=True)
    description = Column(Text, nullable=True)
    verified = Column(Boolean, default=True, nullable=False)
    active = Column(Boolean, default=True, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)
