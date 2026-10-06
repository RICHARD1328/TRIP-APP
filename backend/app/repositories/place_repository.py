from typing import List, Tuple
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.models.place import PlaceItem


def seed_initial_places_if_empty(db: Session):
    """
    Seed initial place items with correct images and structured data if table is empty.
    """
    if db.query(PlaceItem).count() > 0:
        return

    seed_data = [
        # POPULAR DESTINATIONS
        PlaceItem(
            name="Ooty",
            location="Nilgiris, Tamil Nadu",
            latitude=11.4102,
            longitude=76.6950,
            image_url="https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?q=80&w=1000&auto=format&fit=crop",
            rating=4.8,
            category="Destination",
            short_description="Queen of Hill Stations surrounded by eucalyptus forests and tea gardens.",
            is_popular_destination=True,
            is_featured=True,
        ),
        PlaceItem(
            name="Munnar",
            location="Idukki, Kerala",
            latitude=10.0889,
            longitude=77.0595,
            image_url="https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?q=80&w=1000&auto=format&fit=crop",
            rating=4.9,
            category="Destination",
            short_description="Rolling hills covered with lush green tea plantations and mist.",
            is_popular_destination=True,
            is_featured=True,
        ),
        PlaceItem(
            name="Goa",
            location="Goa, India",
            latitude=15.2993,
            longitude=74.1240,
            image_url="https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?q=80&w=1000&auto=format&fit=crop",
            rating=4.7,
            category="Destination",
            short_description="Sun-kissed golden beaches, vibrant nightlife, and Portuguese heritage.",
            is_popular_destination=True,
            is_featured=True,
        ),
        PlaceItem(
            name="Jaipur",
            location="Rajasthan, India",
            latitude=26.9124,
            longitude=75.7873,
            image_url="https://images.unsplash.com/photo-1477587458883-47145ed94245?q=80&w=1000&auto=format&fit=crop",
            rating=4.8,
            category="Destination",
            short_description="The Pink City showcasing grand palaces, forts, and rich royal heritage.",
            is_popular_destination=True,
            is_featured=False,
        ),
        PlaceItem(
            name="Manali",
            location="Kullu, Himachal Pradesh",
            latitude=32.2432,
            longitude=77.1892,
            image_url="https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?q=80&w=1000&auto=format&fit=crop",
            rating=4.7,
            category="Destination",
            short_description="Snow-capped Himalayan peaks, adventure sports, and scenic valleys.",
            is_popular_destination=True,
            is_featured=False,
        ),
        PlaceItem(
            name="Coorg",
            location="Kodagu, Karnataka",
            latitude=12.3375,
            longitude=75.8069,
            image_url="https://images.unsplash.com/photo-1596422846543-75c6fc197f07?q=80&w=1000&auto=format&fit=crop",
            rating=4.6,
            category="Destination",
            short_description="Scotland of India known for aromatic coffee plantations and waterfalls.",
            is_popular_destination=True,
            is_featured=False,
        ),

        # POPULAR ATTRACTIONS
        PlaceItem(
            name="Ooty Lake & Boating",
            location="Ooty, Tamil Nadu",
            latitude=11.4087,
            longitude=76.6896,
            image_url="https://images.unsplash.com/photo-1590490360182-c33d57733427?q=80&w=1000&auto=format&fit=crop",
            rating=4.5,
            category="Attraction",
            short_description="Scenic 19th-century artificial lake offering pedal boating and eucalyptus groves.",
            is_popular_attraction=True,
        ),
        PlaceItem(
            name="Eravikulam National Park",
            location="Munnar, Kerala",
            latitude=10.1852,
            longitude=77.0725,
            image_url="https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1000&auto=format&fit=crop",
            rating=4.9,
            category="Attraction",
            short_description="High-altitude sanctuary home to the endangered Nilgiri Tahr goat.",
            is_popular_attraction=True,
        ),
        PlaceItem(
            name="Fort Aguada",
            location="Sinquerim, Goa",
            latitude=15.4927,
            longitude=73.7738,
            image_url="https://images.unsplash.com/photo-1587922546307-776227941871?q=80&w=1000&auto=format&fit=crop",
            rating=4.6,
            category="Attraction",
            short_description="17th-century Portuguese lighthouse and fortress overlooking the Arabian Sea.",
            is_popular_attraction=True,
        ),
        PlaceItem(
            name="Solang Valley Adventure",
            location="Manali, Himachal Pradesh",
            latitude=32.3166,
            longitude=77.1578,
            image_url="https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=1000&auto=format&fit=crop",
            rating=4.7,
            category="Attraction",
            short_description="Premier alpine hub for paragliding, zorbing, and snow skiing.",
            is_popular_attraction=True,
        ),
        PlaceItem(
            name="Hawa Mahal",
            location="Jaipur, Rajasthan",
            latitude=26.9239,
            longitude=75.8267,
            image_url="https://images.unsplash.com/photo-1599661046289-e31897846e41?q=80&w=1000&auto=format&fit=crop",
            rating=4.8,
            category="Attraction",
            short_description="Palace of Breeze constructed with red and pink sandstone honeycomb windows.",
            is_popular_attraction=True,
        ),

        # RECOMMENDED HOTELS
        PlaceItem(
            name="Savoy IHCL SeleQtions",
            location="Ooty, Tamil Nadu",
            latitude=11.4120,
            longitude=76.6960,
            image_url="https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=1000&auto=format&fit=crop",
            rating=4.8,
            category="Hotel",
            short_description="5-star heritage colonial hotel featuring cottage stays and manicured gardens.",
            is_recommended_hotel=True,
        ),
        PlaceItem(
            name="Blanket Hotel & Spa",
            location="Munnar, Kerala",
            latitude=10.0578,
            longitude=77.0620,
            image_url="https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=1000&auto=format&fit=crop",
            rating=4.9,
            category="Hotel",
            short_description="Luxury eco-resort overlooking Attukad waterfalls and tea gardens.",
            is_recommended_hotel=True,
        ),
        PlaceItem(
            name="Taj Fort Aguada Resort",
            location="Candolim, Goa",
            latitude=15.4930,
            longitude=73.7745,
            image_url="https://images.unsplash.com/photo-1571896349842-33c89424de2d?q=80&w=1000&auto=format&fit=crop",
            rating=4.8,
            category="Hotel",
            short_description="Beachfront luxury resort overlooking sea-cliffs and historic fort ruins.",
            is_recommended_hotel=True,
        ),

        # FEATURED PLACES
        PlaceItem(
            name="Taj Mahal",
            location="Agra, Uttar Pradesh",
            latitude=27.1751,
            longitude=78.0421,
            image_url="https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=1000&auto=format&fit=crop",
            rating=4.9,
            category="Featured",
            short_description="Iconic white marble mausoleum and UNESCO World Heritage Wonder of the World.",
            is_featured=True,
        ),
        PlaceItem(
            name="Pangong Tso Lake",
            location="Leh Ladakh, UT",
            latitude=33.7595,
            longitude=78.6674,
            image_url="https://images.unsplash.com/photo-1581793745862-99fde7fa73d2?q=80&w=1000&auto=format&fit=crop",
            rating=4.9,
            category="Featured",
            short_description="Breathtaking high-altitude endorheic lake changing colors from turquoise to blue.",
            is_featured=True,
        ),
    ]

    db.add_all(seed_data)
    db.commit()


def get_popular_destinations(db: Session, skip: int = 0, limit: int = 10) -> Tuple[List[PlaceItem], int]:
    """
    Retrieve paginated popular destinations.
    """
    seed_initial_places_if_empty(db)
    query = db.query(PlaceItem).filter(
        (PlaceItem.is_popular_destination == True) | (func.lower(PlaceItem.category) == "destination")
    )
    total = query.count()
    items = query.offset(skip).limit(limit).all()
    return items, total


def get_popular_attractions(db: Session, skip: int = 0, limit: int = 10) -> Tuple[List[PlaceItem], int]:
    """
    Retrieve paginated popular attractions.
    """
    seed_initial_places_if_empty(db)
    query = db.query(PlaceItem).filter(
        (PlaceItem.is_popular_attraction == True) | (func.lower(PlaceItem.category) == "attraction")
    )
    total = query.count()
    items = query.offset(skip).limit(limit).all()
    return items, total


def get_recommended_hotels(db: Session, skip: int = 0, limit: int = 10) -> Tuple[List[PlaceItem], int]:
    """
    Retrieve paginated recommended hotels.
    """
    seed_initial_places_if_empty(db)
    query = db.query(PlaceItem).filter(
        (PlaceItem.is_recommended_hotel == True) | (func.lower(PlaceItem.category) == "hotel")
    )
    total = query.count()
    items = query.offset(skip).limit(limit).all()
    return items, total


def get_featured_places(db: Session, skip: int = 0, limit: int = 10) -> Tuple[List[PlaceItem], int]:
    """
    Retrieve paginated featured places.
    """
    seed_initial_places_if_empty(db)
    query = db.query(PlaceItem).filter(PlaceItem.is_featured == True)
    total = query.count()
    items = query.offset(skip).limit(limit).all()
    return items, total


def get_quick_statistics(db: Session) -> dict:
    """
    Retrieve summary statistics for the home dashboard.
    """
    seed_initial_places_if_empty(db)
    dest_count = db.query(PlaceItem).filter(
        (PlaceItem.is_popular_destination == True) | (func.lower(PlaceItem.category) == "destination")
    ).count()
    attr_count = db.query(PlaceItem).filter(
        (PlaceItem.is_popular_attraction == True) | (func.lower(PlaceItem.category) == "attraction")
    ).count()
    hotel_count = db.query(PlaceItem).filter(
        (PlaceItem.is_recommended_hotel == True) | (func.lower(PlaceItem.category) == "hotel")
    ).count()

    return {
        "total_destinations": dest_count,
        "total_attractions": attr_count,
        "total_hotels": hotel_count,
        "satisfied_travelers": "10,000+"
    }
