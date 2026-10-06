import os
import uuid
import json
from typing import List
from fastapi import HTTPException, status, UploadFile
from sqlalchemy.orm import Session

from app.models.user import User
from app.repositories import profile_repository
from app.schemas.profile import (
    ProfileResponse,
    ProfileUpdateRequest,
    ProfileImageResponse,
)

UPLOAD_DIR = os.path.join("uploads", "profile_images")
ALLOWED_MIME_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"}
MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024  # 5 MB limit


def parse_json_or_list(value: str) -> List[str]:
    """
    Parse stored DB text into a Python list of strings.
    """
    if not value:
        return []
    try:
        data = json.loads(value)
        if isinstance(data, list):
            return [str(x).strip() for x in data if x]
    except Exception:
        pass
    # Fallback to comma-separated parsing
    return [x.strip() for x in value.split(",") if x.strip()]


def serialize_user_profile(user: User) -> ProfileResponse:
    """
    Convert User model instance to ProfileResponse schema.
    Guarantees password_hash and sensitive credentials are never included.
    """
    travel_prefs = parse_json_or_list(user.travel_preferences)
    interests_list = parse_json_or_list(user.interests)

    return ProfileResponse(
        id=user.id,
        full_name=user.full_name,
        email=user.email,
        phone_number=user.phone_number,
        profile_image_url=user.profile_image_url,
        date_of_birth=user.date_of_birth,
        preferred_language=user.preferred_language or "en",
        preferred_currency=user.preferred_currency or "INR",
        travel_preferences=travel_prefs,
        interests=interests_list,
        created_at=user.created_at,
        updated_at=user.updated_at,
        is_active=user.is_active,
    )


def get_user_profile(current_user: User) -> ProfileResponse:
    """
    Fetch current user's profile details.
    """
    return serialize_user_profile(current_user)


def update_user_profile(
    db: Session,
    current_user: User,
    request: ProfileUpdateRequest
) -> ProfileResponse:
    """
    Update profile attributes for the authenticated user.
    """
    if request.email is not None:
        new_email = request.email.strip().lower()
        if new_email != current_user.email.lower():
            existing = profile_repository.get_user_by_email_exclude_id(
                db, email=new_email, exclude_user_id=current_user.id
            )
            if existing:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Email address is already in use by another account."
                )
            current_user.email = new_email

    if request.full_name is not None:
        full_name_clean = request.full_name.strip()
        if not full_name_clean:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Full name cannot be empty."
            )
        current_user.full_name = full_name_clean

    if request.phone_number is not None:
        current_user.phone_number = request.phone_number.strip() if request.phone_number else None

    if request.date_of_birth is not None:
        current_user.date_of_birth = request.date_of_birth.strip() if request.date_of_birth else None

    if request.preferred_language is not None:
        current_user.preferred_language = request.preferred_language.strip() if request.preferred_language else "en"

    if request.preferred_currency is not None:
        current_user.preferred_currency = request.preferred_currency.strip().upper() if request.preferred_currency else "INR"

    if request.travel_preferences is not None:
        current_user.travel_preferences = json.dumps(request.travel_preferences)

    if request.interests is not None:
        current_user.interests = json.dumps(request.interests)

    updated = profile_repository.update_user_profile(db, current_user)
    return serialize_user_profile(updated)


def upload_profile_image(
    db: Session,
    current_user: User,
    file: UploadFile
) -> ProfileImageResponse:
    """
    Safely process and store user avatar picture.
    """
    if not file.content_type or file.content_type.lower() not in ALLOWED_MIME_TYPES:
        allowed_str = ", ".join(sorted(ALLOWED_MIME_TYPES))
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid image format '{file.content_type}'. Allowed types: {allowed_str}"
        )

    try:
        contents = file.file.read()
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Failed to read uploaded file."
        )

    if len(contents) > MAX_FILE_SIZE_BYTES:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="File size exceeds maximum allowable limit of 5 MB."
        )

    ext = os.path.splitext(file.filename)[1].lower() if file.filename else ".jpg"
    if not ext or ext not in [".jpg", ".jpeg", ".png", ".webp", ".gif"]:
        ext = ".png"

    os.makedirs(UPLOAD_DIR, exist_ok=True)
    filename = f"user_{current_user.id}_{uuid.uuid4().hex[:12]}{ext}"
    filepath = os.path.join(UPLOAD_DIR, filename)

    with open(filepath, "wb") as f:
        f.write(contents)

    image_url = f"/uploads/profile_images/{filename}"
    current_user.profile_image_url = image_url
    profile_repository.update_user_profile(db, current_user)

    return ProfileImageResponse(
        profile_image_url=image_url,
        message="Profile image uploaded and updated successfully"
    )


def delete_user_profile(db: Session, current_user: User) -> dict:
    """
    Delete or deactivate the user profile.
    """
    profile_repository.delete_user_profile(db, current_user, hard_delete=False)
    return {
        "message": "User profile deactivated successfully",
        "user_id": current_user.id
    }
