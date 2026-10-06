from abc import ABC, abstractmethod
import logging
from typing import List, Optional
from app.schemas.transport import TransportSearchItem

logger = logging.getLogger(__name__)


class AbstractTransportProvider(ABC):
    """
    Abstract Base Class for Transport Providers (Strategy Pattern).
    Allows seamless integration of live transit APIs (IRCTC, RedBus, Amadeus, Skyscanner)
    alongside local demonstration dataset.
    """
    @abstractmethod
    def search_transport(
        self,
        origin: Optional[str] = None,
        destination: Optional[str] = None,
        travel_date: Optional[str] = None,
        passengers: int = 1,
        transport_type: Optional[str] = None,
        sort_by: str = "price"
    ) -> List[TransportSearchItem]:
        pass


class LocalDemoTransportProvider(AbstractTransportProvider):
    """
    Local Demonstration Transport Provider.
    Queries curated sample transit dataset with source_type="demo" flag.
    """

    DEMO_TRANSPORTS = [
        # BUSES
        TransportSearchItem(
            transport_id="bus_ksrtc_volvo_1",
            type="bus",
            operator="KSRTC Airavat Club Class",
            origin="Bangalore",
            destination="Ooty",
            departure_time="10:30 PM",
            arrival_time="06:00 AM",
            duration="7h 30m",
            duration_minutes=450,
            price=850.0,
            booking_url="https://ksrtc.in",
            source_type="demo"
        ),
        TransportSearchItem(
            transport_id="bus_greenline_2",
            type="bus",
            operator="Greenline Travels Multi-Axle Volvo",
            origin="Bangalore",
            destination="Ooty",
            departure_time="11:15 PM",
            arrival_time="06:30 AM",
            duration="7h 15m",
            duration_minutes=435,
            price=920.0,
            booking_url="https://redbus.in",
            source_type="demo"
        ),
        TransportSearchItem(
            transport_id="bus_kerala_rtc_3",
            type="bus",
            operator="Kerala RTC Swift Deluxe",
            origin="Ernakulam",
            destination="Munnar",
            departure_time="07:00 AM",
            arrival_time="11:30 AM",
            duration="4h 30m",
            duration_minutes=270,
            price=240.0,
            booking_url="https://keralartc.com",
            source_type="demo"
        ),
        # TRAINS
        TransportSearchItem(
            transport_id="train_nilgiri_express_4",
            type="train",
            operator="Indian Railways - Nilgiri Express (12671)",
            origin="Chennai Central",
            destination="Mettupalayam (Ooty)",
            departure_time="09:05 PM",
            arrival_time="06:15 AM",
            duration="9h 10m",
            duration_minutes=550,
            price=480.0,
            booking_url="https://irctc.co.in",
            source_type="demo"
        ),
        TransportSearchItem(
            transport_id="train_shatabdi_5",
            type="train",
            operator="IRCTC Coimbatore Shatabdi (12243)",
            origin="Chennai Central",
            destination="Coimbatore",
            departure_time="07:10 AM",
            arrival_time="02:15 PM",
            duration="7h 05m",
            duration_minutes=425,
            price=1120.0,
            booking_url="https://irctc.co.in",
            source_type="demo"
        ),
        # FLIGHTS
        TransportSearchItem(
            transport_id="flight_indigo_6",
            type="flight",
            operator="IndiGo 6E-243",
            origin="Bangalore (BLR)",
            destination="Coimbatore (CJB)",
            departure_time="08:40 AM",
            arrival_time="09:40 AM",
            duration="1h 00m",
            duration_minutes=60,
            price=3200.0,
            booking_url="https://goindigo.in",
            source_type="demo"
        ),
        TransportSearchItem(
            transport_id="flight_airindia_7",
            type="flight",
            operator="Air India AI-582",
            origin="Mumbai (BOM)",
            destination="Goa (GOI)",
            departure_time="11:15 AM",
            arrival_time="12:30 PM",
            duration="1h 15m",
            duration_minutes=75,
            price=4100.0,
            booking_url="https://airindia.com",
            source_type="demo"
        ),
        # TAXIS
        TransportSearchItem(
            transport_id="taxi_outstation_8",
            type="taxi",
            operator="TripDirect Outstation Private Cab",
            origin="Coimbatore",
            destination="Ooty",
            departure_time="Flexible / On Demand",
            arrival_time="3 hours after pickup",
            duration="3h 00m",
            duration_minutes=180,
            price=2800.0,
            booking_url=None,
            source_type="demo"
        ),
        TransportSearchItem(
            transport_id="taxi_munnar_9",
            type="taxi",
            operator="Kochi Airport Pre-Paid Taxi",
            origin="Kochi Airport",
            destination="Munnar",
            departure_time="Flexible / On Demand",
            arrival_time="3.5 hours after pickup",
            duration="3h 30m",
            duration_minutes=210,
            price=3100.0,
            booking_url=None,
            source_type="demo"
        ),
        # CAR RENTALS
        TransportSearchItem(
            transport_id="car_zoomcar_10",
            type="car",
            operator="Zoomcar Self-Drive SUV",
            origin="Bangalore",
            destination="Ooty",
            departure_time="Self Pick-up 24/7",
            arrival_time="Flexible Drop-off",
            duration="6h 00m drive time",
            duration_minutes=360,
            price=2200.0,
            booking_url="https://zoomcar.com",
            source_type="demo"
        )
    ]

    def search_transport(
        self,
        origin: Optional[str] = None,
        destination: Optional[str] = None,
        travel_date: Optional[str] = None,
        passengers: int = 1,
        transport_type: Optional[str] = None,
        sort_by: str = "price"
    ) -> List[TransportSearchItem]:
        """
        Filter and sort demo transport dataset.
        """
        results: List[TransportSearchItem] = []

        for transport in self.DEMO_TRANSPORTS:
            item = transport.model_copy()

            # Adjust total price for requested passenger count
            if passengers > 1 and item.type in ["bus", "train", "flight"]:
                item.price = round(item.price * passengers, 2)

            # Filter 1: Origin
            if origin:
                orig_clean = origin.strip().lower()
                if orig_clean not in item.origin.lower():
                    continue

            # Filter 2: Destination
            if destination:
                dest_clean = destination.strip().lower()
                if dest_clean not in item.destination.lower():
                    continue

            # Filter 3: Transport Type ("bus", "train", "flight", "taxi", "car")
            if transport_type:
                req_types = [t.strip().lower() for t in transport_type.split(",")]
                if item.type.lower() not in req_types:
                    continue

            results.append(item)

        # Sorting logic
        sort_clean = (sort_by or "price").strip().lower()
        if sort_clean == "duration":
            results.sort(key=lambda t: t.duration_minutes)
        else:
            # Default: sort by price ascending
            results.sort(key=lambda t: t.price)

        return results


def get_transport_provider() -> AbstractTransportProvider:
    """
    Factory function for Transport Provider.
    Returns LocalDemoTransportProvider instance, easily swappable via config to a live GDS/transit provider.
    """
    return LocalDemoTransportProvider()
