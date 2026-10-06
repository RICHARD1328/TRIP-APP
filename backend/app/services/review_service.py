import math
from typing import Optional
from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.user import User
from app.models.review import Review
from app.repositories import review_repository
from app.schemas.review import (
    CreateReviewRequest,
    UpdateReviewRequest,
    ReviewResponse,
    ReviewListResponse,
)

ALLOWED_TARGET_TYPES = {"destination", "hotel", "attraction"}


def validate_target_type(target_type: str) -> str:
    """
    Ensure target_type is a recognized entity category.
    """
    normalized = target_type.strip().lower()
    if normalized not in ALLOWED_TARGET_TYPES:
        allowed_str = ", ".join(sorted(ALLOWED_TARGET_TYPES))
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid target_type '{target_type}'. Must be one of: {allowed_str}"
        )
    return normalized


def review_to_response(review: Review) -> ReviewResponse:
    """
    Convert a Review SQLAlchemy model instance into a ReviewResponse Pydantic schema.
    """
    user_name = review.user.full_name if review.user else None
    user_avatar = review.user.profile_image_url if review.user else None

    return ReviewResponse(
        id=review.id,
        user_id=review.user_id,
        user_name=user_name,
        user_avatar=user_avatar,
        target_type=review.target_type,
        target_id=review.target_id,
        rating=review.rating,
        title=review.title,
        comment=review.comment,
        created_at=review.created_at,
        updated_at=review.updated_at,
    )


def create_user_review(
    db: Session,
    current_user: User,
    request: CreateReviewRequest
) -> ReviewResponse:
    """
    Create a new review for an authenticated user.
    Enforces rating bounds (1.0 - 5.0) and prevents duplicate reviews for the same target entity.
    """
    target_type = validate_target_type(request.target_type)

    if not (1.0 <= request.rating <= 5.0):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Rating must be between 1.0 and 5.0"
        )

    # Check for existing review by this user for the target entity
    existing = review_repository.get_user_review_for_target(
        db=db,
        user_id=current_user.id,
        target_type=target_type,
        target_id=request.target_id
    )
    if existing:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"You have already submitted a review for this {target_type}."
        )

    new_review = Review(
        user_id=current_user.id,
        target_type=target_type,
        target_id=request.target_id,
        rating=float(request.rating),
        title=request.title.strip(),
        comment=request.comment.strip(),
    )

    created = review_repository.create_review(db, new_review)
    return review_to_response(created)


def list_reviews_for_target(
    db: Session,
    target_type: str,
    target_id: str,
    rating_filter: Optional[int] = None,
    page: int = 1,
    limit: int = 10,
) -> ReviewListResponse:
    """
    Get paginated reviews for a specific target entity with average rating and review counts.
    """
    target_type = validate_target_type(target_type)
    offset = (page - 1) * limit

    reviews, filtered_count = review_repository.get_reviews_for_target(
        db=db,
        target_type=target_type,
        target_id=target_id,
        rating_filter=rating_filter,
        limit=limit,
        offset=offset,
    )

    total_reviews, average_rating, rating_breakdown = review_repository.get_target_review_stats(
        db=db,
        target_type=target_type,
        target_id=target_id,
    )

    total_pages = math.ceil(filtered_count / limit) if filtered_count > 0 else 1

    return ReviewListResponse(
        reviews=[review_to_response(r) for r in reviews],
        total_reviews=total_reviews,
        average_rating=average_rating,
        rating_breakdown=rating_breakdown,
        page=page,
        limit=limit,
        total_pages=total_pages,
    )


def update_user_review(
    db: Session,
    current_user: User,
    review_id: int,
    request: UpdateReviewRequest
) -> ReviewResponse:
    """
    Update an existing review. Enforces ownership check (only review author can modify).
    """
    review = review_repository.get_review_by_id(db, review_id)
    if not review:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Review with ID {review_id} not found."
        )

    if review.user_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not authorized to modify another user's review."
        )

    if request.rating is not None:
        if not (1.0 <= request.rating <= 5.0):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Rating must be between 1.0 and 5.0"
            )
        review.rating = float(request.rating)

    if request.title is not None:
        review.title = request.title.strip()

    if request.comment is not None:
        review.comment = request.comment.strip()

    updated = review_repository.update_review(db, review)
    return review_to_response(updated)


def delete_user_review(
    db: Session,
    current_user: User,
    review_id: int
) -> dict:
    """
    Delete a review. Enforces ownership check (only review author can delete).
    """
    review = review_repository.get_review_by_id(db, review_id)
    if not review:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Review with ID {review_id} not found."
        )

    if review.user_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not authorized to delete another user's review."
        )

    review_repository.delete_review(db, review)
    return {
        "message": "Review deleted successfully",
        "review_id": review_id
    }
