from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.security import get_current_user
from app.models.user import User
from app.schemas.user import (
    UserCreate,
    UserLogin,
    UserResponse,
    TokenResponse,
    TokenRefreshResponse,
    MessageResponse,
)
from app.services import auth_service

router = APIRouter()


@router.post("/register", response_model=TokenResponse, status_code=status.HTTP_201_CREATED)
def register(user_in: UserCreate, db: Session = Depends(get_db)):
    """
    Register a new user account with full_name, email, and password.
    POST /api/v1/auth/register
    """
    return auth_service.register_user(db=db, user_in=user_in)


@router.post("/login", response_model=TokenResponse, status_code=status.HTTP_200_OK)
def login(login_in: UserLogin, db: Session = Depends(get_db)):
    """
    Authenticate user with email and password, returning JWT access token.
    POST /api/v1/auth/login
    """
    return auth_service.authenticate_user(db=db, login_in=login_in)


@router.get("/me", response_model=UserResponse, status_code=status.HTTP_200_OK)
def get_me(current_user: User = Depends(get_current_user)):
    """
    Get profile information of currently authenticated user.
    GET /api/v1/auth/me
    Authorization: Bearer <token>
    """
    return UserResponse.model_validate(current_user)


@router.post("/refresh", response_model=TokenRefreshResponse, status_code=status.HTTP_200_OK)
def refresh_token(current_user: User = Depends(get_current_user)):
    """
    Generate a refreshed access token for the authenticated user.
    POST /api/v1/auth/refresh
    Authorization: Bearer <token>
    """
    return auth_service.refresh_user_token(current_user=current_user)


@router.post("/logout", response_model=MessageResponse, status_code=status.HTTP_200_OK)
def logout(current_user: User = Depends(get_current_user)):
    """
    Log out the current user session.
    POST /api/v1/auth/logout
    Authorization: Bearer <token>
    """
    return MessageResponse(detail="Successfully logged out.")
