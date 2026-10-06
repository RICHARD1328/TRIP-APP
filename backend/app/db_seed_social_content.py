import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import logging
from app.core.database import SessionLocal, engine, Base
from app.models.social_content import DestinationSocialContent

logger = logging.getLogger(__name__)

SEED_ITEMS = [
    {
        "id": "social_munnar_1",
        "destination_id": "munnar",
        "platform": "instagram",
        "title": "Munnar Tea Gardens & Misty Peaks",
        "reel_url": "https://www.instagram.com/reel/C3MunnarReel1/",
        "thumbnail_url": "https://images.unsplash.com/photo-1593693397690-362cb9666fc2?w=800",
        "creator_name": "KeralaTravelVibes",
        "description": "Breathtaking aerial view of Munnar's rolling tea plantations and cloud-capped hills.",
        "verified": True,
        "active": True
    },
    {
        "id": "social_munnar_2",
        "destination_id": "munnar",
        "platform": "instagram",
        "title": "Top Station Sunset Point",
        "reel_url": "https://www.instagram.com/reel/C3MunnarReel2/",
        "thumbnail_url": "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
        "creator_name": "WanderlustIndia",
        "description": "Watching golden hour above the clouds at Top Station Munnar.",
        "verified": True,
        "active": True
    },
    {
        "id": "social_ooty_1",
        "destination_id": "ooty",
        "platform": "instagram",
        "title": "Nilgiri Mountain Toy Train Ride",
        "reel_url": "https://www.instagram.com/reel/C4OotyReel99/",
        "thumbnail_url": "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=800",
        "creator_name": "TamilNaduExplorers",
        "description": "Riding the iconic UNESCO heritage steam train from Coonoor to Ooty.",
        "verified": True,
        "active": True
    },
    {
        "id": "social_ooty_2",
        "destination_id": "ooty",
        "platform": "instagram",
        "title": "Ooty Lake Boating & Rose Garden",
        "reel_url": "https://www.instagram.com/reel/C4OotyReel88/",
        "thumbnail_url": "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
        "creator_name": "SouthIndiaStories",
        "description": "Boating on serene Ooty Lake and exploring thousands of roses in bloom.",
        "verified": True,
        "active": True
    },
    {
        "id": "social_coorg_1",
        "destination_id": "coorg",
        "platform": "instagram",
        "title": "Coorg Coffee Plantation Walk & Abbey Falls",
        "reel_url": "https://www.instagram.com/reel/C5CoorgReel12/",
        "thumbnail_url": "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800",
        "creator_name": "KarnatakaDiaries",
        "description": "Smelling fresh coffee beans and visiting roaring Abbey Falls in Madikeri.",
        "verified": True,
        "active": True
    },
    {
        "id": "social_goa_1",
        "destination_id": "goa",
        "platform": "instagram",
        "title": "Goa Golden Hour Sunset at Palolem Beach",
        "reel_url": "https://www.instagram.com/reel/C6GoaReel77/",
        "thumbnail_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=800",
        "creator_name": "GoaTravelReels",
        "description": "Relaxing beach vibes, palm trees, and ocean sunsets in South Goa.",
        "verified": True,
        "active": True
    }
]


def seed_social_content():
    """Ensure table exists and seed sample verified social Reels."""
    Base.metadata.create_all(bind=engine)
    db = SessionLocal()
    try:
        for item_data in SEED_ITEMS:
            existing = db.query(DestinationSocialContent).filter(DestinationSocialContent.id == item_data["id"]).first()
            if not existing:
                db_obj = DestinationSocialContent(**item_data)
                db.add(db_obj)
        db.commit()
        print("Successfully seeded sample verified destination social content (Munnar, Ooty, Coorg, Goa).")
    finally:
        db.close()


if __name__ == "__main__":
    seed_social_content()
