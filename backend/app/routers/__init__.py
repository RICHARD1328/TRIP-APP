from fastapi import APIRouter
from app.routers import (
    health, auth, home, destination_router, place_router, hotel_router,
    transport_router, trip_router, budget_router, route_router, itinerary_router,
    package_router, review_router, safety_router, profile_router, ai_planner_router,
    admin_router
)

api_router = APIRouter()
api_router.include_router(health.router, tags=["Health"])
api_router.include_router(auth.router, prefix="/auth", tags=["Authentication"])
api_router.include_router(home.router, prefix="/home", tags=["Home"])
api_router.include_router(destination_router.router, prefix="/destinations", tags=["Destinations"])
api_router.include_router(place_router.router, prefix="/places", tags=["Places"])
api_router.include_router(hotel_router.router, prefix="/hotels", tags=["Hotels"])
api_router.include_router(transport_router.router, prefix="/transport", tags=["Transport"])
api_router.include_router(trip_router.router, prefix="/trips", tags=["Trips"])
api_router.include_router(ai_planner_router.router, prefix="/ai-planner", tags=["AI Planner"])
api_router.include_router(budget_router.router, prefix="/budget", tags=["Budget"])
api_router.include_router(route_router.router, prefix="/routes", tags=["Routes"])
api_router.include_router(itinerary_router.router, prefix="/itineraries", tags=["Itineraries"])
api_router.include_router(package_router.router, prefix="/packages", tags=["Packages"])
api_router.include_router(review_router.router, prefix="/reviews", tags=["Reviews"])
api_router.include_router(safety_router.router, prefix="/safety", tags=["Safety"])
api_router.include_router(profile_router.router, prefix="/profile", tags=["Profile"])
api_router.include_router(admin_router.router, prefix="/admin", tags=["Admin"])
