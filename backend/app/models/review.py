from sqlalchemy import Column, Integer, String, Float, Text, DateTime, ForeignKey, UniqueConstraint
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.core.database import Base


class Review(Base):
    __tablename__ = "reviews"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    target_type = Column(String(50), nullable=False, index=True)  # e.g. "destination", "hotel", "attraction"
    target_id = Column(String(255), nullable=False, index=True)
    rating = Column(Float, nullable=False)
    title = Column(String(255), nullable=False)
    comment = Column(Text, nullable=False)

    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now(), nullable=False)

    user = relationship("User", backref="reviews")

    __table_args__ = (
        UniqueConstraint("user_id", "target_type", "target_id", name="uq_user_target_review"),
    )
