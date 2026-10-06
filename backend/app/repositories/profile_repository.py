from typing import Optional
from sqlalchemy.orm import Session
from app.models.user import User


def get_user_by_id(db: Session, user_id: int) -> Optional[User]:
    """
    Fetch user by ID.
    """
    return db.query(User).filter(User.id == user_id).first()


def get_user_by_email_exclude_id(db: Session, email: str, exclude_user_id: int) -> Optional[User]:
    """
    Check if email is already taken by another user.
    """
    return db.query(User).filter(
        User.email.ilike(email.strip()),
        User.id != exclude_user_id
    ).first()


def update_user_profile(db: Session, user: User) -> User:
    """
    Persist changes to a User model.
    """
    db.commit()
    db.refresh(user)
    return user


def delete_user_profile(db: Session, user: User, hard_delete: bool = False) -> None:
    """
    Deactivate or hard delete a User record.
    By default, sets is_active = False (soft delete / account deactivation).
    """
    if hard_delete:
        db.delete(user)
    else:
        user.is_active = False
    db.commit()
