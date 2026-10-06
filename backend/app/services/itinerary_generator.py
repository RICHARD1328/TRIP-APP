from datetime import datetime, timedelta
from typing import List, Dict, Any, Optional


def is_indoor_category(category: str) -> bool:
    cat = category.lower()
    return cat in ["museum", "cafe", "restaurant", "shopping", "historic", "temple", "hospital"]


def build_dynamic_itinerary(
    destination_name: str,
    start_date_str: str,
    duration_days: int,
    ranked_places: List[Dict[str, Any]],
    weather_forecast: List[Dict[str, Any]]
) -> List[Dict[str, Any]]:
    """
    Generate a dynamic day-by-day itinerary considering place rankings, weather, travel time, and opening hours.
    """
    try:
        start_dt = datetime.strptime(start_date_str, "%Y-%m-%d")
    except ValueError:
        start_dt = datetime.now()

    # Weather map by date YYYY-MM-DD
    weather_map = {w["date"]: w for w in weather_forecast} if weather_forecast else {}

    # Separate places into outdoor and indoor pools
    outdoor_places = [p for p in ranked_places if not is_indoor_category(p.get("category", ""))]
    indoor_places = [p for p in ranked_places if is_indoor_category(p.get("category", ""))]
    all_places = ranked_places.copy()

    used_place_ids = set()

    def pick_next_place(is_rainy: bool) -> Dict[str, Any]:
        """Pick best available place favoring indoor if rainy, outdoor if clear."""
        primary_pool = indoor_places if is_rainy else outdoor_places
        secondary_pool = outdoor_places if is_rainy else indoor_places

        for p in primary_pool:
            if p["place_id"] not in used_place_ids:
                used_place_ids.add(p["place_id"])
                return p

        for p in secondary_pool:
            if p["place_id"] not in used_place_ids:
                used_place_ids.add(p["place_id"])
                return p

        for p in all_places:
            if p["place_id"] not in used_place_ids:
                used_place_ids.add(p["place_id"])
                return p

        # If ran out of unique places, cycle through all_places safely
        fallback = all_places[len(used_place_ids) % max(1, len(all_places))]
        return fallback

    daily_itinerary = []

    for day in range(1, duration_days + 1):
        current_dt = start_dt + timedelta(days=day - 1)
        date_str = current_dt.strftime("%Y-%m-%d")
        w_data = weather_map.get(date_str, {})

        is_rainy = False
        weather_summary = "Clear weather expected"
        if w_data and w_data.get("forecast_available"):
            precip = w_data.get("precipitation_probability", 0)
            cond = w_data.get("weather_condition", "Clear")
            is_rainy = precip >= 50 or any(kw in cond.lower() for kw in ["rain", "drizzle", "thunderstorm", "shower"])
            weather_summary = f"{cond}, Max: {w_data['temperature']['max']}°C, Rain prob: {precip}%"

        # Day Title
        if day == 1:
            title = f"Day 1: Arrival & Initial Exploration of {destination_name}"
        elif day == duration_days:
            title = f"Day {day}: Highlight Sightseeing & Departure"
        else:
            title = f"Day {day}: Deep Dive into {destination_name}"

        slots = []

        # 1. Morning Slot (09:00 AM)
        m_place = pick_next_place(is_rainy)
        opening = f" (Open: {m_place['opening_hours']})" if m_place.get("opening_hours") else ""
        slots.append({
            "slot_id": f"day_{day}_slot_1",
            "time_of_day": "09:00 AM - 11:30 AM",
            "activity": f"Visit {m_place['name']}",
            "place_id": m_place["place_id"],
            "description": f"Explore {m_place['name']} ({m_place['category'].capitalize()}).{opening}",
            "location": m_place.get("address") or destination_name,
            "category": m_place["category"],
            "image_url": m_place.get("image_url"),
            "estimated_cost": 150.0,
            "weather_note": "Indoor activity prioritized due to rain forecast" if is_rainy and is_indoor_category(m_place["category"]) else None
        })

        # 2. Lunch Slot (01:00 PM)
        restaurant_place = [p for p in indoor_places if p.get("category") == "restaurant" and p["place_id"] not in used_place_ids]
        lunch_name = restaurant_place[0]["name"] if restaurant_place else "Local Specialty Restaurant"
        if restaurant_place:
            used_place_ids.add(restaurant_place[0]["place_id"])

        slots.append({
            "slot_id": f"day_{day}_slot_2",
            "time_of_day": "01:00 PM - 02:30 PM",
            "activity": f"Lunch at {lunch_name}",
            "place_id": restaurant_place[0]["place_id"] if restaurant_place else None,
            "description": f"Authentic regional dining experience in {destination_name}.",
            "location": destination_name,
            "category": "restaurant",
            "image_url": restaurant_place[0].get("image_url") if restaurant_place else None,
            "estimated_cost": 300.0,
            "weather_note": None
        })

        # 3. Afternoon Slot (03:00 PM)
        a_place = pick_next_place(is_rainy)
        opening = f" (Open: {a_place['opening_hours']})" if a_place.get("opening_hours") else ""
        slots.append({
            "slot_id": f"day_{day}_slot_3",
            "time_of_day": "03:00 PM - 05:30 PM",
            "activity": f"Sightseeing at {a_place['name']}",
            "place_id": a_place["place_id"],
            "description": f"Visit {a_place['name']} - popular {a_place['category']} spot.{opening}",
            "location": a_place.get("address") or destination_name,
            "category": a_place["category"],
            "image_url": a_place.get("image_url"),
            "estimated_cost": 200.0,
            "weather_note": "Indoor activity prioritized due to rain forecast" if is_rainy and is_indoor_category(a_place["category"]) else None
        })

        # 4. Evening Slot (06:30 PM)
        e_place = pick_next_place(is_rainy=False)  # Evenings can include relaxed walking / shopping
        slots.append({
            "slot_id": f"day_{day}_slot_4",
            "time_of_day": "06:30 PM - 08:30 PM",
            "activity": f"Evening Leisure at {e_place['name']}",
            "place_id": e_place["place_id"],
            "description": f"Evening walk, local photography, and relaxation at {e_place['name']}.",
            "location": e_place.get("address") or destination_name,
            "category": e_place["category"],
            "image_url": e_place.get("image_url"),
            "estimated_cost": 150.0,
            "weather_note": None
        })

        daily_itinerary.append({
            "day_number": day,
            "date": date_str,
            "title": title,
            "weather_summary": weather_summary,
            "slots": slots
        })

    return daily_itinerary
