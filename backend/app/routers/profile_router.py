from fastapi import APIRouter, Depends, File, UploadFile, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.security import get_current_user
from app.models.user import User
from app.schemas.profile import (
    ProfileResponse,
    ProfileUpdateRequest,
    ProfileImageResponse,
)
from app.services import profile_service

router = APIRouter()


@router.get("", response_model=ProfileResponse, status_code=status.HTTP_200_OK)
def get_my_profile(
    current_user: User = Depends(get_current_user),
):
    """
    Get profile information of currently authenticated user. Requires JWT authentication.
    GET /api/v1/profile
    """
    return profile_service.get_user_profile(current_user=current_user)


@router.put("", response_model=ProfileResponse, status_code=status.HTTP_200_OK)
def update_my_profile(
    request: ProfileUpdateRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """
    Update profile details for currently authenticated user. Requires JWT authentication.
    PUT /api/v1/profile
    """
    return profile_service.update_user_profile(
        db=db,
        current_user=current_user,
        request=request,
    )


@router.post("/image", response_model=ProfileImageResponse, status_code=status.HTTP_200_OK)
def upload_my_profile_image(
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """
    Safely upload and update profile picture for currently authenticated user. Requires JWT authentication.
    POST /api/v1/profile/image
    """
    return profile_service.upload_profile_image(
        db=db,
        current_user=current_user,
        file=file,
    )


@router.delete("", status_code=status.HTTP_200_OK)
def delete_my_profile(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """
    Deactivate or delete profile for currently authenticated user. Requires JWT authentication.
    DELETE /api/v1/profile
    """
    return profile_service.delete_user_profile(
        db=db,
        current_user=current_user,
    )
