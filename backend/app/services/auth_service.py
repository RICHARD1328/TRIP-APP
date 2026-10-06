from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core import security
from app.models.user import User
from app.repositories import user_repository
from app.schemas.user import UserCreate, UserLogin, TokenResponse, TokenRefreshResponse, UserResponse


def register_user(db: Session, user_in: UserCreate) -> TokenResponse:
    """
    Register a new user, ensuring email uniqueness and password hashing.
    """
    existing_user = user_repository.get_user_by_email(db, email=user_in.email)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email address is already registered.",
        )

    hashed_password = security.get_password_hash(user_in.password)
    user = user_repository.create_user(db, user_in=user_in, password_hash=hashed_password)

    access_token = security.create_access_token(subject=user.id)
    return TokenResponse(
        access_token=access_token,
        token_type="bearer",
        user=UserResponse.model_validate(user),
    )


def authenticate_user(db: Session, login_in: UserLogin) -> TokenResponse:
    """
    Authenticate user credentials and issue a new JWT access token.
    """
    user = user_repository.get_user_by_email(db, email=login_in.email)
    if not user or not security.verify_password(login_in.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password.",
            headers={"WWW-Authenticate": "Bearer"},
        )

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Inactive user account."
        )

    access_token = security.create_access_token(subject=user.id)
    return TokenResponse(
        access_token=access_token,
        token_type="bearer",
        user=UserResponse.model_validate(user),
    )


def refresh_user_token(current_user: User) -> TokenRefreshResponse:
    """
    Generate a new access token for an authenticated active user.
    """
    access_token = security.create_access_token(subject=current_user.id)
    return TokenRefreshResponse(
        access_token=access_token,
        token_type="bearer",
    )
