from abc import ABC, abstractmethod
import math
import logging
from typing import List, Optional
from app.schemas.hotel import HotelSearchItem

logger = logging.getLogger(__name__)


def haversine_distance_km(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """
    Calculate Haversine distance in kilometers between two lat/lon points.
    """
    r = 6371.0  # Earth radius in km
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lon2 - lon1)

    a = math.sin(delta_phi / 2.0) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2.0) ** 2
    c = 2.0 * math.atan2(math.sqrt(a), math.sqrt(1.0 - a))
    return round(r * c, 2)


class AbstractHotelProvider(ABC):
    """
    Abstract Base Class for Hotel Providers (Strategy Pattern).
    Allows seamless integration of live GDS or API providers (Amadeus, Booking.com, Expedia)
    alongside local demonstration dataset.
    """
    @abstractmethod
    def search_hotels(
        self,
        destination: Optional[str] = None,
        latitude: Optional[float] = None,
        longitude: Optional[float] = None,
        check_in: Optional[str] = None,
        check_out: Optional[str] = None,
        guests: int = 1,
        rooms: int = 1,
        min_price: Optional[float] = None,
        max_price: Optional[float] = None,
        min_rating: Optional[float] = None,
        amenities: Optional[List[str]] = None,
        max_distance: Optional[float] = None,
        sort_by: str = "price_low_to_high"
    ) -> List[HotelSearchItem]:
        pass


class LocalDemoHotelProvider(AbstractHotelProvider):
    """
    Local Demonstration Hotel Provider.
    Queries curated hotel dataset with source_type="demo" flag.
    """

    DEMO_HOTELS = [
        HotelSearchItem(
            hotel_id="ooty_savoy_1",
            name="Savoy IHCL SeleQtions",
            location="Ooty, Nilgiris, Tamil Nadu",
            latitude=11.4120,
            longitude=76.6960,
            image_url="https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=1000&auto=format&fit=crop",
            rating=4.8,
            price_per_night=8500.0,
            available_rooms=5,
            amenities=["Free Wi-Fi", "Breakfast", "Restaurant", "Garden", "Parking"],
            description="5-star heritage colonial hotel featuring cottage stays, fireplaces, and manicured gardens.",
            source_type="demo"
        ),
        HotelSearchItem(
            hotel_id="ooty_gem_park_2",
            name="Gem Park Ooty",
            location="Sheddon Road, Ooty, Tamil Nadu",
            latitude=11.4140,
            longitude=76.7020,
            image_url="https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=1000&auto=format&fit=crop",
            rating=4.5,
            price_per_night=4200.0,
            available_rooms=8,
            amenities=["Free Wi-Fi", "Pool", "Spa", "Restaurant", "Gym"],
            description="4-star luxury hotel overlooking Dodabetta valley with heated indoor pool.",
            source_type="demo"
        ),
        HotelSearchItem(
            hotel_id="munnar_blanket_3",
            name="Blanket Hotel & Spa",
            location="Attukad, Munnar, Kerala",
            latitude=10.0578,
            longitude=77.0620,
            image_url="https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=1000&auto=format&fit=crop",
            rating=4.9,
            price_per_night=9500.0,
            available_rooms=4,
            amenities=["Free Wi-Fi", "Breakfast", "Spa", "Pool", "Mountain View"],
            description="Luxury eco-resort overlooking Attukad waterfalls and misty tea gardens.",
            source_type="demo"
        ),
        HotelSearchItem(
            hotel_id="munnar_tea_county_4",
            name="KTDC Tea County Resort",
            location="Munnar Town, Kerala",
            latitude=10.0880,
            longitude=77.0600,
            image_url="https://images.unsplash.com/photo-1571896349842-33c89424de2d?q=80&w=1000&auto=format&fit=crop",
            rating=4.4,
            price_per_night=3800.0,
            available_rooms=12,
            amenities=["Free Wi-Fi", "Breakfast", "Ayurvedic Spa", "Parking"],
            description="Charming resort nestled between two hills in the heart of Munnar.",
            source_type="demo"
        ),
        HotelSearchItem(
            hotel_id="goa_taj_fort_5",
            name="Taj Fort Aguada Resort & Spa",
            location="Sinquerim, Candolim, Goa",
            latitude=15.4930,
            longitude=73.7745,
            image_url="https://images.unsplash.com/photo-1571896349842-33c89424de2d?q=80&w=1000&auto=format&fit=crop",
            rating=4.8,
            price_per_night=14000.0,
            available_rooms=6,
            amenities=["Free Wi-Fi", "Beachfront", "Pool", "Spa", "Bar", "Water Sports"],
            description="Iconic 5-star oceanfront resort built into 16th-century Portuguese fortress sea-cliffs.",
            source_type="demo"
        ),
        HotelSearchItem(
            hotel_id="goa_alila_diwa_6",
            name="Alila Diwa Goa",
            location="Majorda, South Goa",
            latitude=15.3120,
            longitude=73.9050,
            image_url="https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=1000&auto=format&fit=crop",
            rating=4.7,
            price_per_night=11500.0,
            available_rooms=7,
            amenities=["Free Wi-Fi", "Infinity Pool", "Spa", "Restaurant", "Airport Shuttle"],
            description="Serene 5-star resort surrounded by lush paddy fields near Majorda beach.",
            source_type="demo"
        ),
        HotelSearchItem(
            hotel_id="jaipur_rambagh_7",
            name="Rambagh Palace",
            location="Bhawani Singh Road, Jaipur, Rajasthan",
            latitude=26.8980,
            longitude=75.8080,
            image_url="https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=1000&auto=format&fit=crop",
            rating=4.95,
            price_per_night=28000.0,
            available_rooms=2,
            amenities=["Free Wi-Fi", "Royal Palace", "Pool", "Spa", "Fine Dining", "Peacock Gardens"],
            description="Former residence of the Maharaja of Jaipur, world-renowned luxury heritage palace.",
            source_type="demo"
        ),
        HotelSearchItem(
            hotel_id="manali_spanish_resort_8",
            name="The Span Resort & Spa",
            location="Baragran, Manali, Himachal Pradesh",
            latitude=32.1850,
            longitude=77.1710,
            image_url="https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=1000&auto=format&fit=crop",
            rating=4.7,
            price_per_night=8900.0,
            available_rooms=5,
            amenities=["Free Wi-Fi", "Riverfront", "Helipad", "Spa", "Heating"],
            description="Luxury riverside resort along Beas river with pine forest backdrops.",
            source_type="demo"
        )
    ]

    def search_hotels(
        self,
        destination: Optional[str] = None,
        latitude: Optional[float] = None,
        longitude: Optional[float] = None,
        check_in: Optional[str] = None,
        check_out: Optional[str] = None,
        guests: int = 1,
        rooms: int = 1,
        min_price: Optional[float] = None,
        max_price: Optional[float] = None,
        min_rating: Optional[float] = None,
        amenities: Optional[List[str]] = None,
        max_distance: Optional[float] = None,
        sort_by: str = "price_low_to_high"
    ) -> List[HotelSearchItem]:
        """
        Filter and sort demo hotel dataset.
        """
        results: List[HotelSearchItem] = []

        for hotel in self.DEMO_HOTELS:
            item = hotel.model_copy()

            # Calculate distance if user provided lat/lon coordinates
            if latitude is not None and longitude is not None:
                item.distance_km = haversine_distance_km(latitude, longitude, item.latitude, item.longitude)

            # Filter 1: Destination string match
            if destination:
                dest_clean = destination.strip().lower()
                name_loc = f"{item.name} {item.location}".lower()
                if dest_clean not in name_loc:
                    continue

            # Filter 2: Price range
            if min_price is not None and item.price_per_night < min_price:
                continue
            if max_price is not None and item.price_per_night > max_price:
                continue

            # Filter 3: Minimum rating
            if min_rating is not None and item.rating < min_rating:
                continue

            # Filter 4: Max distance (km)
            if max_distance is not None and item.distance_km is not None:
                if item.distance_km > max_distance:
                    continue

            # Filter 5: Amenities (must contain all requested amenities)
            if amenities:
                hotel_amenities_lower = [a.lower() for a in item.amenities]
                match_all = True
                for req_a in amenities:
                    if req_a.strip().lower() not in hotel_amenities_lower:
                        match_all = False
                        break
                if not match_all:
                    continue

            results.append(item)

        # Sorting logic
        sort_clean = (sort_by or "price_low_to_high").strip().lower()
        if sort_clean == "price_high_to_low":
            results.sort(key=lambda h: h.price_per_night, reverse=True)
        elif sort_clean == "rating":
            results.sort(key=lambda h: h.rating, reverse=True)
        elif sort_clean == "distance":
            results.sort(key=lambda h: (h.distance_km if h.distance_km is not None else 999999.0))
        else:
            # Default: price_low_to_high
            results.sort(key=lambda h: h.price_per_night)

        return results


def get_hotel_provider() -> AbstractHotelProvider:
    """
    Factory function for Hotel Provider.
    Returns LocalDemoHotelProvider instance, easily swappable via config to a live GDS/booking provider.
    """
    return LocalDemoHotelProvider()
