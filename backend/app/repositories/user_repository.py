from typing import Optional
from sqlalchemy.orm import Session
from app.models.user import User
from app.schemas.user import UserCreate


def get_user_by_email(db: Session, email: str) -> Optional[User]:
    """
    Fetch a user record by email address (case-insensitive search).
    """
    return db.query(User).filter(User.email.ilike(email.strip())).first()


def get_user_by_id(db: Session, user_id: int) -> Optional[User]:
    """
    Fetch a user record by primary key ID.
    """
    return db.query(User).filter(User.id == user_id).first()


def create_user(db: Session, user_in: UserCreate, password_hash: str) -> User:
    """
    Persist a new User record with pre-hashed password.
    """
    db_user = User(
        full_name=user_in.full_name.strip(),
        email=user_in.email.lower().strip(),
        password_hash=password_hash,
        is_active=True,
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user
