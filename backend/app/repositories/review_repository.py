from typing import List, Tuple, Optional, Dict
from sqlalchemy.orm import Session, joinedload
from sqlalchemy import func
from app.models.review import Review


def create_review(db: Session, review: Review) -> Review:
    """
    Persist a new Review entity to the database.
    """
    db.add(review)
    db.commit()
    db.refresh(review)
    return review


def get_review_by_id(db: Session, review_id: int) -> Optional[Review]:
    """
    Find a Review by its primary key ID, eager loading the associated User.
    """
    return db.query(Review).options(joinedload(Review.user)).filter(Review.id == review_id).first()


def get_user_review_for_target(
    db: Session,
    user_id: int,
    target_type: str,
    target_id: str
) -> Optional[Review]:
    """
    Find existing review submitted by a specific user for a given target entity.
    """
    return db.query(Review).filter(
        Review.user_id == user_id,
        func.lower(Review.target_type) == target_type.lower(),
        Review.target_id == str(target_id)
    ).first()


def get_reviews_for_target(
    db: Session,
    target_type: str,
    target_id: str,
    rating_filter: Optional[int] = None,
    limit: int = 10,
    offset: int = 0
) -> Tuple[List[Review], int]:
    """
    Retrieve paginated reviews for a target entity.
    """
    query = db.query(Review).options(joinedload(Review.user)).filter(
        func.lower(Review.target_type) == target_type.lower(),
        Review.target_id == str(target_id)
    )

    if rating_filter is not None and 1 <= rating_filter <= 5:
        query = query.filter(func.round(Review.rating) == rating_filter)

    total_count = query.count()
    reviews = query.order_by(Review.created_at.desc()).offset(offset).limit(limit).all()
    return reviews, total_count


def get_target_review_stats(
    db: Session,
    target_type: str,
    target_id: str
) -> Tuple[int, float, Dict[int, int]]:
    """
    Calculate aggregate statistics (total count, average rating, star rating breakdown)
    for a target entity.
    """
    reviews = db.query(Review).filter(
        func.lower(Review.target_type) == target_type.lower(),
        Review.target_id == str(target_id)
    ).all()

    total_count = len(reviews)
    breakdown = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0}

    if total_count == 0:
        return 0, 0.0, breakdown

    total_rating_sum = 0.0
    for r in reviews:
        total_rating_sum += r.rating
        star = max(1, min(5, int(round(r.rating))))
        breakdown[star] += 1

    avg_rating = round(total_rating_sum / total_count, 2)
    return total_count, avg_rating, breakdown


def update_review(db: Session, review: Review) -> Review:
    """
    Commit updates to an existing Review entity.
    """
    db.commit()
    db.refresh(review)
    return review


def delete_review(db: Session, review: Review) -> None:
    """
    Delete a Review entity from the database.
    """
    db.delete(review)
    db.commit()
