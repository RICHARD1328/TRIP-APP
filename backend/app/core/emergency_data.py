"""
Verified country-specific emergency contacts and safety data repository.
Emergency telephone numbers are strictly sourced from official government/telecom standards.
"""

from typing import Dict, List, Any

# Country code and name normalization dictionary
COUNTRY_ALIAS_MAP = {
    "in": "India",
    "india": "India",
    "us": "United States",
    "usa": "United States",
    "united states": "United States",
    "united states of america": "United States",
    "uk": "United Kingdom",
    "gb": "United Kingdom",
    "great britain": "United Kingdom",
    "united kingdom": "United Kingdom",
    "ca": "Canada",
    "canada": "Canada",
    "au": "Australia",
    "australia": "Australia",
    "jp": "Japan",
    "japan": "Japan",
    "fr": "France",
    "france": "France",
    "de": "Germany",
    "germany": "Germany",
    "th": "Thailand",
    "thailand": "Thailand",
    "ae": "United Arab Emirates",
    "uae": "United Arab Emirates",
    "united arab emirates": "United Arab Emirates",
}

VERIFIED_EMERGENCY_DATA: Dict[str, Dict[str, Any]] = {
    "India": {
        "emergency_contacts": [
            {
                "title": "National Emergency Number",
                "number": "112",
                "description": "Unified single emergency response number for Police, Fire, and Medical assistance.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "Police Assistance",
                "number": "100",
                "description": "Direct police emergency hotline.",
                "is_dialable": True,
                "category": "police"
            },
            {
                "title": "Ambulance & Emergency Medical",
                "number": "108",
                "description": "24/7 National Health Mission emergency ambulance service.",
                "is_dialable": True,
                "category": "medical"
            },
            {
                "title": "Fire Brigade",
                "number": "101",
                "description": "Fire service dispatch.",
                "is_dialable": True,
                "category": "fire"
            },
            {
                "title": "Incredible India Tourist Helpline",
                "number": "1363",
                "description": "24x7 multi-lingual tourist helpline managed by Ministry of Tourism.",
                "is_dialable": True,
                "category": "tourist"
            },
            {
                "title": "Women Safety Helpline",
                "number": "1091",
                "description": "Dedicated safety helpline for female travelers and residents.",
                "is_dialable": True,
                "category": "police"
            }
        ],
        "local_safety_recommendations": [
            "Keep emergency contact numbers saved offline and readily accessible.",
            "Use registered prepaid taxis or verified ride-hailing apps (Uber, Ola) rather than unmetered street cabs.",
            "Avoid displaying large sums of cash, expensive jewelry, or unlocked electronic devices in crowded bazaars.",
            "Stay aware of touts around railway stations, airports, and major heritage sites offering unverified packages or lodging.",
            "Consume bottled or purified water and prefer freshly cooked food at reputed dining venues."
        ]
    },
    "United States": {
        "emergency_contacts": [
            {
                "title": "Emergency Dispatch (Police, Fire, Medical)",
                "number": "911",
                "description": "Universal emergency number for life-threatening or immediate police/fire assistance.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "Poison Help Hotline",
                "number": "1-800-222-1222",
                "description": "Free 24/7 medical guidance for toxic substance exposure or accidental ingestion.",
                "is_dialable": True,
                "category": "medical"
            },
            {
                "title": "Municipal Services & Non-Emergency Information",
                "number": "311",
                "description": "Non-emergency local government services and municipal reporting.",
                "is_dialable": True,
                "category": "general"
            }
        ],
        "local_safety_recommendations": [
            "In case of extreme weather (tornadoes, hurricanes, blizzards), monitor local NOAA radio or weather app alerts.",
            "Lock vehicle doors and keep valuables out of plain view when parked.",
            "Know your healthcare insurance coverage; US emergency room visits can incur substantial out-of-pocket costs without international coverage."
        ]
    },
    "United Kingdom": {
        "emergency_contacts": [
            {
                "title": "Emergency Services",
                "number": "999",
                "description": "Primary UK emergency call number for Police, Ambulance, Fire, and Coastguard.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "European Universal Emergency Number",
                "number": "112",
                "description": "Alternative emergency number routed directly to emergency dispatch.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "NHS Medical Advice (Non-Emergency)",
                "number": "111",
                "description": "24/7 advice for non-life-threatening medical conditions.",
                "is_dialable": True,
                "category": "medical"
            },
            {
                "title": "Police Non-Emergency",
                "number": "101",
                "description": "For reporting crimes that do not require immediate emergency response.",
                "is_dialable": True,
                "category": "police"
            }
        ],
        "local_safety_recommendations": [
            "Mind your belongings in crowded tourist spots like London Underground stations and Oxford Street.",
            "Remember traffic drives on the left side of the road; look right before crossing streets.",
            "Keep emergency contact details saved on your device."
        ]
    },
    "Canada": {
        "emergency_contacts": [
            {
                "title": "Emergency Dispatch",
                "number": "911",
                "description": "Universal emergency service for Police, Fire, and Ambulance.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "Community Information & Non-Emergency",
                "number": "211",
                "description": "Information on social, health, and government services.",
                "is_dialable": True,
                "category": "general"
            }
        ],
        "local_safety_recommendations": [
            "Dress appropriately in winter months to protect against extreme frostbite risks.",
            "Observe wildlife warnings in national parks; maintain safe distances from bears and large mammals."
        ]
    },
    "Australia": {
        "emergency_contacts": [
            {
                "title": "Triple Zero (000) Emergency",
                "number": "000",
                "description": "Primary emergency number for Police, Fire, and Ambulance in Australia.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "Police Assistance Line (Non-Emergency)",
                "number": "131 444",
                "description": "For non-urgent police reporting and assistance.",
                "is_dialable": True,
                "category": "police"
            },
            {
                "title": "Healthdirect Australia",
                "number": "1800 022 222",
                "description": "24-hour health advice line staffed by registered nurses.",
                "is_dialable": True,
                "category": "medical"
            }
        ],
        "local_safety_recommendations": [
            "Swim only at patrolled beaches between the red and yellow flags.",
            "Use SPF 50+ sunscreen, wear protective headwear, and stay hydrated due to high UV indices.",
            "Check local bushfire alerts before traveling through remote or forest areas."
        ]
    },
    "Japan": {
        "emergency_contacts": [
            {
                "title": "Police Emergency",
                "number": "110",
                "description": "Direct police emergency response line.",
                "is_dialable": True,
                "category": "police"
            },
            {
                "title": "Ambulance & Fire Service",
                "number": "119",
                "description": "Fire fighting and emergency medical ambulance dispatch.",
                "is_dialable": True,
                "category": "medical"
            },
            {
                "title": "Japan Visitor Hotline (JNTO)",
                "number": "+81 50 3816 2720",
                "description": "24/7 English, Chinese, Korean, and Japanese tourist assistance hotline.",
                "is_dialable": True,
                "category": "tourist"
            }
        ],
        "local_safety_recommendations": [
            "Keep emergency Earthquake / Tsunami warning apps active (e.g. Safety Tips by Japan Tourism Agency).",
            "Carry adequate cash as smaller establishments and public transit charging may require physical yen.",
            "Know your hotel address written in Japanese script to assist taxi drivers or emergency responders."
        ]
    },
    "France": {
        "emergency_contacts": [
            {
                "title": "European Universal Emergency",
                "number": "112",
                "description": "Universal emergency number available from any mobile phone.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "SAMU (Medical Emergency)",
                "number": "15",
                "description": "Medical emergency service dispatch.",
                "is_dialable": True,
                "category": "medical"
            },
            {
                "title": "Police Secours",
                "number": "17",
                "description": "Direct police emergency response.",
                "is_dialable": True,
                "category": "police"
            },
            {
                "title": "Sapeurs-Pompiers (Fire Brigade)",
                "number": "18",
                "description": "Fire service and accident rescue.",
                "is_dialable": True,
                "category": "fire"
            }
        ],
        "local_safety_recommendations": [
            "Be vigilant against pickpockets near major Parisian landmarks and metro stations.",
            "Do not accept unsolicited petitions or street game offers."
        ]
    },
    "Germany": {
        "emergency_contacts": [
            {
                "title": "Emergency Call (Police)",
                "number": "110",
                "description": "Police emergency line.",
                "is_dialable": True,
                "category": "police"
            },
            {
                "title": "Fire Brigade & Medical Rescue",
                "number": "112",
                "description": "Universal emergency and ambulance dispatch.",
                "is_dialable": True,
                "category": "general"
            },
            {
                "title": "Medical On-Call Service",
                "number": "116117",
                "description": "Non-emergency medical advice for out-of-hours doctor visits.",
                "is_dialable": True,
                "category": "medical"
            }
        ],
        "local_safety_recommendations": [
            "Always follow pedestrian crossing signals ('Ampelmännchen'); jaywalking carries strict fines.",
            "Ensure health insurance documents are easily available when seeking medical care."
        ]
    },
    "Thailand": {
        "emergency_contacts": [
            {
                "title": "Tourist Police (English Speaking)",
                "number": "1155",
                "description": "Dedicated multi-lingual helpline for tourist safety and law enforcement.",
                "is_dialable": True,
                "category": "tourist"
            },
            {
                "title": "Emergency Medical Response",
                "number": "1669",
                "description": "National emergency ambulance dispatch service.",
                "is_dialable": True,
                "category": "medical"
            },
            {
                "title": "General Police",
                "number": "191",
                "description": "Royal Thai Police emergency dispatch.",
                "is_dialable": True,
                "category": "police"
            },
            {
                "title": "Fire Department",
                "number": "199",
                "description": "Fire fighting and rescue service.",
                "is_dialable": True,
                "category": "fire"
            }
        ],
        "local_safety_recommendations": [
            "Negotiate tuk-tuk fares in advance or insist on metered taxicabs.",
            "Wear protective helmets if riding scooters and ensure international driving permits are valid.",
            "Stay hydrated and purchase travel insurance covering scooter or marine recreational activities."
        ]
    },
    "United Arab Emirates": {
        "emergency_contacts": [
            {
                "title": "Police Emergency",
                "number": "999",
                "description": "Police assistance for emergency situations.",
                "is_dialable": True,
                "category": "police"
            },
            {
                "title": "Ambulance Service",
                "number": "998",
                "description": "Emergency medical ambulance response.",
                "is_dialable": True,
                "category": "medical"
            },
            {
                "title": "Civil Defence (Fire)",
                "number": "997",
                "description": "Fire emergency and fire safety dispatch.",
                "is_dialable": True,
                "category": "fire"
            },
            {
                "title": "Tourist Police (Non-Emergency)",
                "number": "901",
                "description": "General tourist inquiries and non-emergency support.",
                "is_dialable": True,
                "category": "tourist"
            }
        ],
        "local_safety_recommendations": [
            "Respect local cultural norms, dress codes, and public decency guidelines.",
            "Keep hydrated during intense summer temperatures.",
            "Strictly observe zero-tolerance laws regarding driving under the influence."
        ]
    }
}

DEFAULT_GLOBAL_EMERGENCY_DATA = {
    "emergency_contacts": [
        {
            "title": "International Emergency Standard (GSM)",
            "number": "112",
            "description": "Universal emergency number accessible on standard mobile networks worldwide.",
            "is_dialable": True,
            "category": "general"
        },
        {
            "title": "Americas / Secondary Standard",
            "number": "911",
            "description": "Standard emergency dispatch number across the Americas and many global regions.",
            "is_dialable": True,
            "category": "general"
        }
    ],
    "local_safety_recommendations": [
        "Store your embassy or consulate telephone number and address prior to international departures.",
        "Maintain digital and physical copies of passport identification and entry visas.",
        "Purchase comprehensive international travel and health insurance.",
        "Stay alert in unfamiliar environments and avoid poorly lit isolated zones at night."
    ]
}

STANDARD_IMPORTANT_DOCUMENTS = [
    "Passport (valid for at least 6 months beyond intended departure date)",
    "Travel Visa / eVisa approval documentation",
    "Comprehensive Travel Insurance Policy & emergency contact card",
    "Digital & physical hard copies of Government Issued ID / Driver's License",
    "International Driving Permit (IDP) if operating motor vehicles",
    "Vaccination Certificates & official medical prescriptions for prescribed medications",
    "Flight return tickets and hotel reservation vouchers"
]

STANDARD_MEDICAL_PREPARATION = [
    "Personal Travel First Aid Kit (bandages, antiseptics, sterile gauze, burn gel)",
    "Adequate supply of essential prescription medications in original labeled containers",
    "Doctor's letter explaining necessity for specialized prescription medicines",
    "Over-the-counter essentials (pain relievers, antihistamines, antidiarrheals, rehydration salts)",
    "Insect repellent (containing DEET or Picaridin) & SPF 50+ broad-spectrum sunscreen",
    "Digital medical ID card listing blood type, severe allergies, and pre-existing conditions"
]

STANDARD_TRAVEL_PRECAUTIONS = [
    "Share detailed itinerary and live location access with trusted family members or friends",
    "Carry secondary payment methods (separate emergency credit card & small physical cash)",
    "Use secure luggage locks and RFID-blocking pouches for passports and credit cards",
    "Avoid public unencrypted Wi-Fi networks when accessing banking apps (use VPN)",
    "Verify official taxi licenses or use trusted ride-hailing applications",
    "Keep device batteries charged and carry a certified power bank"
]


def resolve_country_name(query: str) -> str:
    """
    Map input string (ISO code or country name) to standard verified country key.
    """
    if not query:
        return "Global"
    cleaned = query.strip().lower()
    return COUNTRY_ALIAS_MAP.get(cleaned, query.strip().title())
