# report_content.py
# Contains all structured academic text, table specifications, and data models for TripDirect PBL Report.

FRONT_MATTER = {
    "institution": "CHENNAI INSTITUTE OF TECHNOLOGY",
    "institution_sub": "(An Autonomous Institution, Affiliated to Anna University, Chennai)\nSarathy Nagar, Kundrathur, Chennai – 600069",
    "department": "DEPARTMENT OF COMPUTER SCIENCE AND ENGINEERING",
    "report_type": "PROJECT-BASED LEARNING (PBL) REPORT",
    "course": "MOBILE APPLICATION DEVELOPMENT (CS4504)",
    "title": "TRIPDIRECT – AN AI-POWERED INDEPENDENT TOURIST TRAVEL PLANNING AND MANAGEMENT APPLICATION",
    "degree": "Submitted in partial fulfilment of the requirements for the\nProject-Based Learning component of Mobile Application Development",
    "submitted_by": [
        "[Student Name 1 (Register No.)]",
        "[Student Name 2 (Register No.)]"
    ],
    "guidance": {
        "mentor": "[Faculty Name]",
        "designation": "[Designation, Department of CSE]"
    },
    "date_place": "[Month, Year]",
    
    "certificate": {
        "text": "This is to certify that the Project–Based Learning report titled “TRIPDIRECT – AN AI-POWERED INDEPENDENT TOURIST TRAVEL PLANNING AND MANAGEMENT APPLICATION” is a Bonafide record of work carried out by [Student Name(s), Register No(s).] of the Department of Computer Science and Engineering, Chennai Institute of Technology, as part of the continuous, mentor–guided Project-Based Learning (PBL) component of the Mobile Application Development course during the academic year [2026–2027]. This report reflects the team's work across the review cycles listed in Section 1 below, not a single end-of-term submission.",
        "mentor_title": "Faculty Mentor",
        "hod_title": "Head of Department",
        "eval_text": "Submitted for the PBL evaluation held on [date].",
        "mentor_sign": "Faculty Mentor: ________________",
        "incharge_sign": "PBL In–charge: _______________"
    },
    
    "declaration": {
        "text": "We declare that this Project-Based Learning report titled “TRIPDIRECT – AN AI-POWERED INDEPENDENT TOURIST TRAVEL PLANNING AND MANAGEMENT APPLICATION” reflects our own work carried out under the mentorship of [Faculty Name] across the PBL review cycle. All sources of information used, third-party software components, open-source libraries, and external web APIs have been duly acknowledged and cited in accordance with academic standards.",
        "members": [
            "[Name 1, Register No., Signature]",
            "[Name 2, Register No., Signature]"
        ]
    },
    
    "acknowledgement": "We would like to express our heartfelt gratitude to our faculty mentor, [Faculty Name], [Designation], Department of Computer Science and Engineering, Chennai Institute of Technology, for continuous guidance, technical insights, and constructive feedback across every review cycle of this Project-Based Learning course. The mentor's encouragement in adopting modern Android Jetpack Compose paradigms and open spatial data architectures significantly elevated the practical engineering value of the project.\n\nWe extend our sincere thanks to Dr. [HOD Name], Head of the Department of Computer Science and Engineering, for providing an inspiring academic atmosphere, state-of-the-art laboratory facilities, and supporting the continuous Project-Based Learning curriculum. We also express our profound gratitude to the Management of Chennai Institute of Technology for fostering an environment of innovation, practical engineering excellence, and technological research.",
    
    "abstract": "Modern independent tourism often suffers from severe information fragmentation, requiring travelers to juggle multiple disconnected applications for map navigation, attraction discovery, hotel reservations, transport scheduling, budget calculation, and tourist safety. This fragmentation leads to cognitive fatigue, inaccurate budget projections, scheduling conflicts, and suboptimal travel experiences. To solve this challenge, this project presents TripDirect, an integrated, modern Android mobile application built using Kotlin and Jetpack Compose, backed by a high-performance Python FastAPI REST backend service.\n\nTripDirect incorporates a multi-factor intelligent trip planning engine that dynamically scores and ranks Points of Interest (POIs) based on user interests, distance proximity, traveler reviews, and budget constraints. The system seamlessly integrates open spatial services including OpenStreetMap Nominatim for forward geocoding, the Overpass API for nearby POI extraction, Open-Meteo for real-time 16-day weather forecasting, and the Open Source Routing Machine (OSRM) for turn-by-turn driving distance and duration calculations. The application features parametric budget estimation, drag-and-drop daily itinerary scheduling, multi-modal transit comparison, hotel filtering, verified emergency helpline directories, spatial healthcare/police discovery, and curated short-form destination video reels ('Real Visuals').\n\nThe software architecture strictly enforces the Model-View-ViewModel (MVVM) pattern with Unidirectional Data Flow (UDF), Kotlin Coroutines, StateFlow, and Retrofit network serialization. The implementation has been thoroughly validated through an automated test suite comprising 107 Pytest backend test cases (100% pass rate in 82.67s), Android domain unit testing, and compilation verification on Android SDK 36 (targetSdk 36, minSdk 24). The resulting application provides independent tourists with a robust, zero-cost, and unified travel planning companion.",
    
    "keywords": "TripDirect, Android Jetpack Compose, FastAPI REST Engine, OpenStreetMap Overpass, AI Travel Planner"
}

PBL_TABLE_0 = {
    "title": "Table 0.1: PBL Course and Team Details",
    "headers": ["Field", "Details"],
    "rows": [
        ["Course", "Mobile Application Development — Project-Based Learning (CS4504)"],
        ["Faculty Mentor", "[Faculty Name, Designation]"],
        ["Section / Team No.", "[CSE—I, Team 6]"],
        ["PBL Duration", "12 Weeks [Week 1 – Week 12]"],
        ["Review Cycles Completed", "Review 0: Problem Formulation | Review 1: Architecture & UI | Review 2: Final Implementation & Verification"],
        ["Project Track / Domain", "Mobile Application Development / Intelligent Tourism Systems / Spatial Cloud Computing"]
    ]
}

PBL_TABLE_1 = {
    "title": "Table 0.2: Team Roles and Responsibilities",
    "headers": ["Name", "Register No.", "Role", "Primary Responsibilities"],
    "rows": [
        ["[Student Name 1]", "[Register No. 1]", "Mobile Application & UI/UX Lead", "Jetpack Compose declarative screen architecture, Material 3 design system, Navigation graphs, ViewModels, and DataStore caching."],
        ["[Student Name 2]", "[Register No. 2]", "Backend & Service Integration Lead", "FastAPI REST API development, SQLAlchemy ORM schema, OSM/Overpass/Meteo integrations, Pytest test suite, and technical documentation."]
    ]
}

ABBREVIATIONS = [
    ("ADB", "Android Debug Bridge"),
    ("AGP", "Android Gradle Plugin"),
    ("API", "Application Programming Interface"),
    ("APK", "Android Package Kit"),
    ("ASGI", "Asynchronous Server Gateway Interface"),
    ("BCrypt", "Blowfish-based Cryptographic Hashing Algorithm"),
    ("CIT", "Chennai Institute of Technology"),
    ("CPU", "Central Processing Unit"),
    ("CRUD", "Create, Read, Update, Delete"),
    ("CSE", "Computer Science and Engineering"),
    ("DAO", "Data Access Object"),
    ("DFD", "Data Flow Diagram"),
    ("DPI", "Dots Per Inch"),
    ("ER", "Entity Relationship"),
    ("GPS", "Global Positioning System"),
    ("HTTP", "Hypertext Transfer Protocol"),
    ("HTTPS", "Hypertext Transfer Protocol Secure"),
    ("IDE", "Integrated Development Environment"),
    ("IEEE", "Institute of Electrical and Electronics Engineers"),
    ("JDK", "Java Development Kit"),
    ("JSON", "JavaScript Object Notation"),
    ("JVM", "Java Virtual Machine"),
    ("JWT", "JSON Web Token"),
    ("KTX", "Kotlin Extensions"),
    ("MVVM", "Model-View-ViewModel"),
    ("ORM", "Object-Relational Mapping"),
    ("OSM", "OpenStreetMap"),
    ("OSRM", "Open Source Routing Machine"),
    ("PBL", "Project-Based Learning"),
    ("POI", "Point of Interest"),
    ("RAM", "Random Access Memory"),
    ("REST", "Representational State Transfer"),
    ("SDK", "Software Development Kit"),
    ("SOS", "Save Our Souls (Emergency Distress Signal)"),
    ("SQL", "Structured Query Language"),
    ("TTL", "Time To Live (Cache Expiration)"),
    ("UDF", "Unidirectional Data Flow"),
    ("UI", "User Interface"),
    ("UML", "Unified Modeling Language"),
    ("URI", "Uniform Resource Identifier"),
    ("URL", "Uniform Resource Locator"),
    ("UX", "User Experience"),
    ("WMO", "World Meteorological Organization")
]

print("Front matter structures loaded.")
