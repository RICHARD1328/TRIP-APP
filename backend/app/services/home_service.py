from sqlalchemy.orm import Session
from app.repositories import place_repository
from app.schemas.home import (
    PlaceItemResponse,
    PaginatedPlaceResponse,
    QuickStatistics,
    HomeDashboardResponse,
)


def fetch_popular_destinations(db: Session, page: int = 1, size: int = 10) -> PaginatedPlaceResponse:
    """
    Fetch paginated popular destinations.
    """
    skip = (page - 1) * size
    items, total = place_repository.get_popular_destinations(db, skip=skip, limit=size)
    place_responses = [PlaceItemResponse.model_validate(item) for item in items]
    return PaginatedPlaceResponse(
        total=total,
        page=page,
        size=size,
        items=place_responses
    )


def fetch_popular_attractions(db: Session, page: int = 1, size: int = 10) -> PaginatedPlaceResponse:
    """
    Fetch paginated popular attractions.
    """
    skip = (page - 1) * size
    items, total = place_repository.get_popular_attractions(db, skip=skip, limit=size)
    place_responses = [PlaceItemResponse.model_validate(item) for item in items]
    return PaginatedPlaceResponse(
        total=total,
        page=page,
        size=size,
        items=place_responses
    )


def fetch_recommended_hotels(db: Session, page: int = 1, size: int = 10) -> PaginatedPlaceResponse:
    """
    Fetch paginated recommended hotels.
    """
    skip = (page - 1) * size
    items, total = place_repository.get_recommended_hotels(db, skip=skip, limit=size)
    place_responses = [PlaceItemResponse.model_validate(item) for item in items]
    return PaginatedPlaceResponse(
        total=total,
        page=page,
        size=size,
        items=place_responses
    )


def fetch_featured_places(db: Session, page: int = 1, size: int = 10) -> PaginatedPlaceResponse:
    """
    Fetch paginated featured places.
    """
    skip = (page - 1) * size
    items, total = place_repository.get_featured_places(db, skip=skip, limit=size)
    place_responses = [PlaceItemResponse.model_validate(item) for item in items]
    return PaginatedPlaceResponse(
        total=total,
        page=page,
        size=size,
        items=place_responses
    )


def fetch_home_dashboard(db: Session) -> HomeDashboardResponse:
    """
    Fetch consolidated home dashboard data (destinations, attractions, hotels, featured, quick stats).
    """
    pop_dests, _ = place_repository.get_popular_destinations(db, skip=0, limit=6)
    pop_attrs, _ = place_repository.get_popular_attractions(db, skip=0, limit=6)
    rec_hotels, _ = place_repository.get_recommended_hotels(db, skip=0, limit=6)
    feat_places, _ = place_repository.get_featured_places(db, skip=0, limit=6)
    stats_dict = place_repository.get_quick_statistics(db)

    return HomeDashboardResponse(
        popular_destinations=[PlaceItemResponse.model_validate(p) for p in pop_dests],
        popular_attractions=[PlaceItemResponse.model_validate(p) for p in pop_attrs],
        recommended_hotels=[PlaceItemResponse.model_validate(p) for p in rec_hotels],
        featured_places=[PlaceItemResponse.model_validate(p) for p in feat_places],
        quick_statistics=QuickStatistics(**stats_dict)
    )
