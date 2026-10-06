from typing import Optional
from fastapi import APIRouter, Depends, Header, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.schemas.social_content_schemas import (
    SocialContentSchema,
    CreateSocialContentRequest,
    UpdateSocialContentRequest
)
from app.repositories import social_content_repository

router = APIRouter()

ADMIN_API_KEY = "admin_secret_key_tripdirect_2026"


def verify_admin_key(x_admin_key: Optional[str] = Header(None)):
    """Security check ensuring admin header key is valid."""
    if not x_admin_key or x_admin_key != ADMIN_API_KEY:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Unauthorized. Valid X-Admin-Key header required."
        )


@router.post("/social-content", response_model=SocialContentSchema, status_code=status.HTTP_201_CREATED)
def create_social_content_admin(
    payload: CreateSocialContentRequest,
    db: Session = Depends(get_db),
    _: None = Depends(verify_admin_key)
):
    """
    Admin endpoint to add new verified destination social content (Reel).
    POST /api/v1/admin/social-content
    """
    created = social_content_repository.create_social_content(db=db, data=payload.model_dump())
    return SocialContentSchema(
        id=created.id,
        destination_id=created.destination_id,
        platform=created.platform,
        title=created.title,
        reel_url=created.reel_url,
        thumbnail_url=created.thumbnail_url,
        creator_name=created.creator_name,
        description=created.description,
        verified=created.verified,
        active=created.active
    )


@router.put("/social-content/{content_id}", response_model=SocialContentSchema, status_code=status.HTTP_200_OK)
def update_social_content_admin(
    content_id: str,
    payload: UpdateSocialContentRequest,
    db: Session = Depends(get_db),
    _: None = Depends(verify_admin_key)
):
    """
    Admin endpoint to edit/verify/disable existing destination social content.
    PUT /api/v1/admin/social-content/{content_id}
    """
    updated = social_content_repository.update_social_content(
        db=db,
        content_id=content_id,
        data=payload.model_dump(exclude_unset=True)
    )
    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Social content item with ID '{content_id}' not found."
        )

    return SocialContentSchema(
        id=updated.id,
        destination_id=updated.destination_id,
        platform=updated.platform,
        title=updated.title,
        reel_url=updated.reel_url,
        thumbnail_url=updated.thumbnail_url,
        creator_name=updated.creator_name,
        description=updated.description,
        verified=updated.verified,
        active=updated.active
    )


@router.delete("/social-content/{content_id}", status_code=status.HTTP_200_OK)
def delete_social_content_admin(
    content_id: str,
    db: Session = Depends(get_db),
    _: None = Depends(verify_admin_key)
):
    """
    Admin endpoint to delete social content item.
    DELETE /api/v1/admin/social-content/{content_id}
    """
    success = social_content_repository.delete_social_content(db=db, content_id=content_id)
    if not success:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Social content item with ID '{content_id}' not found."
        )
    return {"message": "Social content item deleted successfully.", "id": content_id}
