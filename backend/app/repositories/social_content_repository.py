from typing import List, Optional, Dict, Any
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.models.social_content import DestinationSocialContent


def get_social_content_for_destination(db: Session, destination_id: str) -> List[DestinationSocialContent]:
    """
    Fetch all active, verified social media content items associated specifically with a destination.
    Matches destination_id exactly or by lowercased destination key (e.g. 'munnar' or 'dest_munnar').
    """
    if not destination_id or not destination_id.strip():
        return []

    dest_key = destination_id.strip().lower()

    # Query matching destination_id exactly or normalized destination string
    results = db.query(DestinationSocialContent).filter(
        func.lower(DestinationSocialContent.destination_id) == dest_key,
        DestinationSocialContent.verified == True,
        DestinationSocialContent.active == True
    ).order_by(DestinationSocialContent.created_at.desc()).all()

    return results


def get_social_content_by_id(db: Session, content_id: str) -> Optional[DestinationSocialContent]:
    """Retrieve social content item by ID."""
    return db.query(DestinationSocialContent).filter(DestinationSocialContent.id == content_id).first()


def create_social_content(db: Session, data: Dict[str, Any]) -> DestinationSocialContent:
    """Create a new social content record."""
    item = DestinationSocialContent(
        destination_id=data["destination_id"].strip(),
        platform=data.get("platform", "instagram"),
        title=data["title"].strip(),
        reel_url=data["reel_url"].strip(),
        thumbnail_url=data.get("thumbnail_url"),
        creator_name=data.get("creator_name"),
        description=data.get("description"),
        verified=data.get("verified", True),
        active=data.get("active", True)
    )
    db.add(item)
    db.commit()
    db.refresh(item)
    return item


def update_social_content(db: Session, content_id: str, data: Dict[str, Any]) -> Optional[DestinationSocialContent]:
    """Update an existing social content record (title, reel_url, verified status, active status)."""
    item = get_social_content_by_id(db, content_id)
    if not item:
        return None

    for field in ["destination_id", "platform", "title", "reel_url", "thumbnail_url", "creator_name", "description", "verified", "active"]:
        if field in data and data[field] is not None:
            setattr(item, field, data[field])

    db.commit()
    db.refresh(item)
    return item


def delete_social_content(db: Session, content_id: str) -> bool:
    """Delete a social content record."""
    item = get_social_content_by_id(db, content_id)
    if not item:
        return False

    db.delete(item)
    db.commit()
    return True
