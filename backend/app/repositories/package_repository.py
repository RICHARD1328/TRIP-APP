import json
from typing import List, Tuple, Optional
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.models.package import Package


def seed_initial_packages_if_empty(db: Session):
    """
    Seed demo travel packages if the packages table is currently empty.
    """
    if db.query(Package).count() > 0:
        return

    seed_data = [
        Package(
            id=1,
            title="Munnar Misty Hills Express (3D/2N)",
            destination="Munnar, Kerala",
            total_price=9999.0,
            currency="INR",
            duration_days=3,
            duration_nights=2,
            accommodation="3-Star Mountain View Resort with Deluxe Room & Balcony",
            transport="Private AC Hatchback for transfers & local sightseeing",
            included_attractions=json.dumps([
                "Tea Gardens & Tea Museum Tour",
                "Mattupetty Dam & Lake Boating",
                "Echo Point Visit",
                "Kundala Lake Scenic Viewpoint"
            ]),
            meals="Daily Buffet Breakfast",
            rating=4.6,
            review_count=88,
            exclusions=json.dumps([
                "Airfare / Train tickets to Kochi",
                "Boating & entry fees at attractions",
                "Lunch & Dinner",
                "Personal expenses & tipping"
            ]),
            provider_name="TripDirect Demo Operator",
            is_demo_data=True,
            data_source="Demo Package Catalog - Not a real-time booking provider quote",
            image_url="https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?q=80&w=1000&auto=format&fit=crop"
        ),
        Package(
            id=2,
            title="Munnar Luxury Tea & Wildlife Sanctuary Retreat (5D/4N)",
            destination="Munnar, Kerala",
            total_price=24999.0,
            currency="INR",
            duration_days=5,
            duration_nights=4,
            accommodation="5-Star Luxury Tea Estate Heritage Villa with Jacuzzi & Breakfast/Dinner",
            transport="Premium Private AC SUV with Dedicated Chauffeur",
            included_attractions=json.dumps([
                "Eravikulam National Park (Nilgiri Tahr Safari)",
                "Kolukkumalai Sunrise Jeep Trek",
                "Private Tea Plantation Guided Tour & Tasting",
                "Mattupetty Dam Boating Pass",
                "Attukad Waterfalls Visit",
                "Spice Plantation Guided Walk"
            ]),
            meals="Breakfast & Dinner Included (Half-Board)",
            rating=4.9,
            review_count=142,
            exclusions=json.dumps([
                "Flight / Train tickets",
                "Camera fees at National Park",
                "Personal shopping & spa treatments",
                "Travel & Medical Insurance"
            ]),
            provider_name="TripDirect Demo Operator",
            is_demo_data=True,
            data_source="Demo Package Catalog - Not a real-time booking provider quote",
            image_url="https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1000&auto=format&fit=crop"
        ),
        Package(
            id=3,
            title="Ooty Queen of Hill Stations Escape (4D/3N)",
            destination="Ooty, Tamil Nadu",
            total_price=14500.0,
            currency="INR",
            duration_days=4,
            duration_nights=3,
            accommodation="4-Star Colonial Heritage Hotel near Lake",
            transport="Private AC Sedan for Coimbatore pickup, drop & sightseeing",
            included_attractions=json.dumps([
                "UNESCO Heritage Nilgiri Mountain Toy Train Ride",
                "Ooty Lake & Pedal Boating",
                "Government Botanical Gardens",
                "Doddabetta Peak Viewpoint",
                "Rose Garden Visit",
                "Coonoor Tea Gardens & Sim's Park"
            ]),
            meals="Daily Breakfast Included",
            rating=4.7,
            review_count=115,
            exclusions=json.dumps([
                "Interstate vehicle permits",
                "Lunch & Dinner expenses",
                "Toy train tickets (subject to availability)",
                "Personal expenses"
            ]),
            provider_name="TripDirect Demo Operator",
            is_demo_data=True,
            data_source="Demo Package Catalog - Not a real-time booking provider quote",
            image_url="https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?q=80&w=1000&auto=format&fit=crop"
        ),
        Package(
            id=4,
            title="Goa Sun, Sand & Heritage Beach Holiday (4D/3N)",
            destination="Goa, India",
            total_price=16999.0,
            currency="INR",
            duration_days=4,
            duration_nights=3,
            accommodation="4-Star Beach Resort with Swimming Pool & Private Beach Access",
            transport="Private Airport Transfers & 1-day South Goa Tour Car",
            included_attractions=json.dumps([
                "Mandovi River Sunset Cruise with Cultural Dance",
                "Basilica of Bom Jesus & Se Cathedral Heritage Tour",
                "Calangute & Baga Beach Water Sports Pass (Parasailing & Jet Ski)",
                "Aguada Fort & Lighthouse Visit"
            ]),
            meals="Daily Buffet Breakfast + Welcome Drink",
            rating=4.5,
            review_count=210,
            exclusions=json.dumps([
                "Flight / Train fare to Goa",
                "Casino entry passes",
                "Lunch & Dinner",
                "Alcoholic beverages & room service"
            ]),
            provider_name="TripDirect Demo Operator",
            is_demo_data=True,
            data_source="Demo Package Catalog - Not a real-time booking provider quote",
            image_url="https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?q=80&w=1000&auto=format&fit=crop"
        ),
        Package(
            id=5,
            title="Jaipur Royal Palaces & Forts Heritage Tour (3D/2N)",
            destination="Jaipur, Rajasthan",
            total_price=12499.0,
            currency="INR",
            duration_days=3,
            duration_nights=2,
            accommodation="4-Star Haveli Style Heritage Hotel",
            transport="Private AC Sedan with English/Hindi Speaking Driver Guide",
            included_attractions=json.dumps([
                "Amber Fort Elephant / Jeep Ride",
                "Hawa Mahal (Palace of Winds) Photo Stop",
                "City Palace Museum & Jantar Mantar",
                "Nahargarh Fort Sunset View Point",
                "Traditional Rajasthani Dinner at Chokhi Dhani"
            ]),
            meals="Daily Breakfast + 1 Cultural Dinner Pass",
            rating=4.8,
            review_count=98,
            exclusions=json.dumps([
                "Airfare / Train fare",
                "Monument entry fees & camera permits",
                "Lunch expenses",
                "Shopping expenses & gratuities"
            ]),
            provider_name="TripDirect Demo Operator",
            is_demo_data=True,
            data_source="Demo Package Catalog - Not a real-time booking provider quote",
            image_url="https://images.unsplash.com/photo-1477587458883-47145ed94245?q=80&w=1000&auto=format&fit=crop"
        ),
        Package(
            id=6,
            title="Manali Adventure & Solang Valley Snow Package (5D/4N)",
            destination="Manali, Himachal Pradesh",
            total_price=18500.0,
            currency="INR",
            duration_days=5,
            duration_nights=4,
            accommodation="3-Star Valley View Resort with Bonfire",
            transport="Volvo Bus from Delhi to Manali + Local AC Cab",
            included_attractions=json.dumps([
                "Solang Valley Snow Point & Adventure Sports",
                "Atal Tunnel & Sissu Valley Day Excursion",
                "Hadimba Temple & Vashisht Hot Springs",
                "Jogini Waterfall Nature Trek",
                "Mall Road Evening Shopping Tour"
            ]),
            meals="Breakfast & Dinner Included",
            rating=4.6,
            review_count=130,
            exclusions=json.dumps([
                "Rohtang Pass special permit fee",
                "Adventure sports activities (Paragliding/Zipline)",
                "Heater charges at hotel",
                "Travel insurance"
            ]),
            provider_name="TripDirect Demo Operator",
            is_demo_data=True,
            data_source="Demo Package Catalog - Not a real-time booking provider quote",
            image_url="https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?q=80&w=1000&auto=format&fit=crop"
        ),
        Package(
            id=7,
            title="Coorg Coffee Estate & Waterfalls Trail (3D/2N)",
            destination="Coorg, Karnataka",
            total_price=11200.0,
            currency="INR",
            duration_days=3,
            duration_nights=2,
            accommodation="Lush Plantation Homestay / 3-Star Resort",
            transport="Private AC Car for Bangalore pickup & local sightseeings",
            included_attractions=json.dumps([
                "Abbey Falls Visit",
                "Raja's Seat Sunset Viewpoint",
                "Dubare Elephant Camp",
                "Bylakuppe Golden Temple (Tibetan Monastery)",
                "Coffee Plantation Guided Plantation Walk"
            ]),
            meals="Daily Breakfast Included",
            rating=4.5,
            review_count=76,
            exclusions=json.dumps([
                "Elephant interaction fee at Dubare",
                "Lunch & Dinner",
                "Personal expenses",
                "Driver night charges"
            ]),
            provider_name="TripDirect Demo Operator",
            is_demo_data=True,
            data_source="Demo Package Catalog - Not a real-time booking provider quote",
            image_url="https://images.unsplash.com/photo-1596422846543-75c6fc197f07?q=80&w=1000&auto=format&fit=crop"
        )
    ]

    db.add_all(seed_data)
    db.commit()


def get_all_packages(
    db: Session,
    destination: Optional[str] = None,
    max_price: Optional[float] = None,
    max_duration: Optional[int] = None,
    min_rating: Optional[float] = None,
    search: Optional[str] = None,
    limit: int = 20,
    offset: int = 0,
) -> Tuple[List[Package], int]:
    """
    Retrieve package list with filtering and total count.
    """
    seed_initial_packages_if_empty(db)
    query = db.query(Package)

    if destination:
        query = query.filter(Package.destination.ilike(f"%{destination}%"))

    if max_price is not None:
        query = query.filter(Package.total_price <= max_price)

    if max_duration is not None:
        query = query.filter(Package.duration_days <= max_duration)

    if min_rating is not None:
        query = query.filter(Package.rating >= min_rating)

    if search:
        search_filter = f"%{search}%"
        query = query.filter(
            (Package.title.ilike(search_filter)) |
            (Package.destination.ilike(search_filter)) |
            (Package.accommodation.ilike(search_filter)) |
            (Package.transport.ilike(search_filter)) |
            (Package.included_attractions.ilike(search_filter))
        )

    total_count = query.count()
    packages = query.offset(offset).limit(limit).all()
    return packages, total_count


def get_package_by_id(db: Session, package_id: int) -> Optional[Package]:
    """
    Retrieve single package by ID.
    """
    seed_initial_packages_if_empty(db)
    return db.query(Package).filter(Package.id == package_id).first()


def get_packages_by_ids(db: Session, package_ids: List[int]) -> List[Package]:
    """
    Retrieve multiple packages by IDs list.
    """
    seed_initial_packages_if_empty(db)
    return db.query(Package).filter(Package.id.in_(package_ids)).all()
