from typing import Optional
from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.security import get_current_user
from app.models.user import User
from app.schemas.review import (
    CreateReviewRequest,
    UpdateReviewRequest,
    ReviewResponse,
    ReviewListResponse,
)
from app.services import review_service

router = APIRouter()


@router.post("", response_model=ReviewResponse, status_code=status.HTTP_201_CREATED)
def create_review(
    request: CreateReviewRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """
    Create a new review for a destination, hotel, or attraction. Requires authentication.
    POST /api/v1/reviews
    """
    return review_service.create_user_review(
        db=db,
        current_user=current_user,
        request=request,
    )


@router.get("/{target_type}/{target_id}", response_model=ReviewListResponse, status_code=status.HTTP_200_OK)
def get_reviews_for_target(
    target_type: str,
    target_id: str,
    rating_filter: Optional[int] = Query(None, ge=1, le=5, description="Filter reviews by star rating (1-5)"),
    page: int = Query(1, ge=1, description="Page number"),
    limit: int = Query(10, ge=1, le=100, description="Items per page"),
    db: Session = Depends(get_db),
):
    """
    Get paginated reviews for a specific target entity (destination, hotel, or attraction)
    with total review count and calculated average rating.
    GET /api/v1/reviews/{target_type}/{target_id}
    """
    return review_service.list_reviews_for_target(
        db=db,
        target_type=target_type,
        target_id=target_id,
        rating_filter=rating_filter,
        page=page,
        limit=limit,
    )


@router.put("/{review_id}", response_model=ReviewResponse, status_code=status.HTTP_200_OK)
def update_review(
    review_id: int,
    request: UpdateReviewRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """
    Update an existing review. User can only modify their own review. Requires authentication.
    PUT /api/v1/reviews/{review_id}
    """
    return review_service.update_user_review(
        db=db,
        current_user=current_user,
        review_id=review_id,
        request=request,
    )


@router.delete("/{review_id}", status_code=status.HTTP_200_OK)
def delete_review(
    review_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """
    Delete an existing review. User can only delete their own review. Requires authentication.
    DELETE /api/v1/reviews/{review_id}
    """
    return review_service.delete_user_review(
        db=db,
        current_user=current_user,
        review_id=review_id,
    )
