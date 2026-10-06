# report_chapters_5_8.py
# Comprehensive Academic Content for Chapters 5, 6, 7, and 8

CHAPTER_5 = {
    "num": "5",
    "title": "SYSTEM ANALYSIS AND DESIGN",
    "sections": [
        {
            "id": "5.1",
            "title": "System Architecture / Block Diagram",
            "content": [
                "The system architecture of TripDirect is engineered as a decoupled, multi-tiered client-server system designed for high concurrency, minimal network overhead, and maximum fault tolerance. Figure 5.1 illustrates the comprehensive block diagram of the system.",
                "The architecture comprises three core operational tiers:",
                "1. Client Presentation Tier: Running natively on the Android OS runtime, this tier is implemented in Kotlin using Jetpack Compose and Material Design 3. The client enforces the Model-View-ViewModel (MVVM) architecture with Unidirectional Data Flow (UDF). UI composables observe immutable StateFlow objects exposed by ViewModels, ensuring a strict separation between rendering logic and business state.",
                "2. Application Server Tier: Powered by Python 3.13 and the FastAPI framework running on a Uvicorn ASGI engine, this tier processes client requests via modular REST endpoints under `/api/v1`. It orchestrates business logic including multi-factor place scoring, dynamic itinerary generation, parametric budget calculation, and routing calculations. Cryptographic security is enforced using PassLib BCrypt hashing and JSON Web Tokens (JWT).",
                "3. External Services & Persistence Tier: Data persistence is maintained via SQLAlchemy 2.0 ORM interfacing with an embedded SQLite 3 database (`tripdirect.db`) with production compatibility for PostgreSQL. For live geospatial and weather intelligence, the server tier communicates asynchronously with OpenStreetMap Nominatim (geocoding), Overpass API (spatial POI queries), Open-Meteo (16-day weather forecasting), and Project OSRM (road routing)."
            ],
            "figure": {
                "id": "Figure 5.1",
                "title": "System Architecture / Block Diagram of TripDirect",
                "file": "fig_5_1_system_architecture.png"
            }
        },
        {
            "id": "5.1.1",
            "title": "Physical Design",
            "content": [
                "The physical design of TripDirect defines the actual hardware components, networking topologies, communication protocols, and execution nodes utilized during deployment. Figure 5.1.1 depicts the physical deployment design.",
                "The physical topology consists of:",
                "• Client Hardware Node: Android physical smartphone or virtual Android Emulator (API level 24 through 36) executing the TripDirect APK. The device communicates over Wi-Fi, 4G, or 5G cellular data using standard HTTP/HTTPS protocols.",
                "• Host Server Node: A development workstation or cloud computing instance hosting the Python 3.13 runtime, Uvicorn ASGI server listening on TCP port 8000, and local filesystem storage for user uploaded profile avatars (`/uploads/profile_images`).",
                "• Database Storage: An optimized, local relational SQLite 3 database file (`tripdirect.db`) residing directly on the host server filesystem, accessed via SQLAlchemy connection pooling.",
                "• Remote Cloud Infrastructure: External open-access RESTful cloud APIs accessed over outbound HTTPS connections (TCP port 443), including OpenStreetMap servers, Open-Meteo infrastructure, and public OSRM routing clusters."
            ],
            "figure": {
                "id": "Figure 5.1.1",
                "title": "Physical Architecture / Deployment Design",
                "file": "fig_5_1_1_physical_architecture.png"
            }
        },
        {
            "id": "5.1.2",
            "title": "Logical Design",
            "content": [
                "The logical design of TripDirect organizes the software into distinct functional layers with clearly defined boundaries of responsibility. Figure 5.1.2 illustrates the logical layered architecture.",
                "1. Presentation Layer: Composed of Jetpack Compose declarative UI screens, reusable composable components, Material 3 theming tokens, and navigation graph controllers (`AppNavigation.kt`).",
                "2. State & ViewModel Layer: 16 specialized ViewModels encapsulating UI state machines, managing Kotlin Coroutine scopes (`viewModelScope`), and exposing read-only `StateFlow` streams to the UI.",
                "3. Repository & Data Layer: Abstract repository interfaces and concrete implementations (`NetworkTripPlannerRepository`, `DestinationRepository`, `EmergencyRepository`, etc.) managing data retrieval, local caching, and offline fallback failover.",
                "4. Backend Service Engine: Core algorithmic modules (`trip_planner_service.py`, `place_scoring_service.py`, `itinerary_generator.py`, `budget_engine.py`) implementing deterministic planning algorithms.",
                "5. Data Access & External Provider Layer: SQLAlchemy ORM models, Pydantic schemas, and external HTTP clients (`external_services.py`) handling network serialization and database transactions."
            ],
            "figure": {
                "id": "Figure 5.1.2",
                "title": "Logical Architecture / Application Module Design",
                "file": "fig_5_1_2_logical_architecture.png"
            }
        },
        {
            "id": "5.2",
            "title": "Working Principle",
            "content": [
                "The working principle of TripDirect governs the step-by-step lifecycle of a travel planning session:",
                "1. User Initialization: The application initializes on the `SplashScreen`, which queries Jetpack DataStore Preferences to verify whether a valid JWT session token exists. If authenticated, the user is transitioned to the `HomeScreen`; otherwise, the `LoginScreen` is presented.",
                "2. Destination Exploration & Input: On the Home dashboard, the user inputs a target travel destination (e.g., 'Ooty'), selecting trip dates, passenger counts, total financial budget, travel style ('relaxed', 'balanced', 'fast-paced'), and personal interests (e.g., Nature, Heritage, Dining).",
                "3. Server Processing & Spatial Resolution: The mobile client issues a `POST /api/v1/ai-planner/plan` request. The FastAPI backend queries Nominatim to obtain precise latitude and longitude coordinates. If Nominatim is unreachable, the system automatically falls back to curated internal coordinates.",
                "4. POI Discovery & Multi-Factor Scoring: Using resolved coordinates, the backend issues an Overpass QL query to discover nearby attractions within a 20 km radius. The `place_scoring_service` computes a multi-factor score (0.0 to 1.0) for each POI based on interest alignment (30%), user ratings (20%), proximity to city center (20%), budget feasibility (15%), and travel pace suitability (15%).",
                "5. Meteorological Adaptation & Scheduling: Concurrently, Open-Meteo weather forecasts are fetched. The `itinerary_generator` dynamically constructs day-by-day itineraries, scheduling outdoor natural sights during periods of low rain probability and relocating visits to museums or indoor markets when precipitation risk exceeds 40%.",
                "6. Budget Calculation & Route Geometry: The `budget_engine` itemizes expenses into 6 discrete categories, validating total costs against the user's budget. OSRM computes driving route geometry and transit durations.",
                "7. Output Delivery & UI Rendering: The synthesized JSON payload is transmitted to the client, parsed via Gson, and rendered into interactive, scrollable itinerary cards, expense charts, and destination reel visualizers."
            ]
        },
        {
            "id": "5.3",
            "title": "UML / Use Case Diagram",
            "content": [
                "The UML Use Case Diagram documents the primary functional interactions between system actors and the TripDirect application boundary. Figure 5.2 illustrates this diagram.",
                "Primary Actors:",
                "• Tourist / Mobile User: The primary human actor interacting with the Android mobile application.",
                "• External Spatial & Weather Services: Automated cloud actors (Nominatim, Overpass API, Open-Meteo, OSRM) fulfilling data requests.",
                "• System Administrator: Privileged actor managing curated social reels and destination records via admin endpoints.",
                "Key Use Cases Include: Register / Login / JWT Authentication, Search Destination & Forward Geocode, Explore Nearby POIs by Category, Run Multi-Factor AI Trip Planner, Calculate Parametric Budget & Solvency, View OSRM Route Navigation & Geometry, Manage Day-by-Day Itinerary Slots, Search & Compare Accommodations, Compare Multi-Modal Transit Options, Access Emergency SOS & Spatial Healthcare, Write Reviews & Rate Places, Watch Destination Real Visuals, and Manage Profile & Custom Travel Styles."
            ],
            "figure": {
                "id": "Figure 5.2",
                "title": "TripDirect UML Use Case Diagram",
                "file": "fig_5_2_use_case_diagram.png"
            }
        },
        {
            "id": "5.4",
            "title": "Activity Diagram",
            "content": [
                "The Activity Diagram specifies the sequential and conditional execution flow of an end-to-end trip planning session. Figure 5.3 depicts this activity flow.",
                "The activity begins at the initial start node when the application is launched. The system validates stored credentials. If unauthenticated, the user proceeds through registration or login. Upon reaching the dashboard, the user inputs travel parameters. The system initiates parallel asynchronous sub-activities: geocoding the destination, querying nearby POIs, fetching 16-day weather forecasts, and resolving transit routes.",
                "The activities converge at the place scoring and scheduling join node, where the weather-aware itinerary is constructed and budget solvency is verified. If the generated plan exceeds budget, a notification advisory is attached. Finally, the generated plan is presented on the client device, allowing the user to inspect, modify, or save the itinerary to local storage before terminating at the end node."
            ],
            "figure": {
                "id": "Figure 5.3",
                "title": "TripDirect End-to-End Activity Diagram",
                "file": "fig_5_3_activity_diagram.png"
            }
        },
        {
            "id": "5.5",
            "title": "Class Diagram",
            "content": [
                "The UML Class Diagram illustrates the object-oriented structure of TripDirect's core client-side architecture, highlighting the relationships between UI ViewModels, Repositories, Retrofit API interfaces, and domain data models. Figure 5.5 presents the class diagram.",
                "Core Classes and Interfaces:",
                "• `TripPlannerViewModel`: Manages UI state via `StateFlow<TripPlannerUiState>`, coordinating user input, trip generation requests, and saving routines.",
                "• `TripPlannerRepository`: Interface declaring core contract methods: `generateTripPlan()`, `optimizeTrip()`, and `modifyTrip()`.",
                "• `NetworkTripPlannerRepository`: Concrete implementation of `TripPlannerRepository` that invokes `AiPlannerApi` via Retrofit, featuring automated failover to `MockTripPlannerRepository` upon network error.",
                "• `AiPlannerApi`: Retrofit interface defining asynchronous HTTP POST/GET endpoints.",
                "• `TripPlan`, `TripDay`, `ItineraryItem`, `TripBudget`: Domain data classes encapsulating trip schedules, daily activity slots, category costs, and routing coordinates."
            ],
            "figure": {
                "id": "Figure 5.5",
                "title": "TripDirect Architecture Class Diagram",
                "file": "fig_5_5_class_diagram.png"
            }
        },
        {
            "id": "5.6",
            "title": "Sequence Diagram",
            "content": [
                "The Sequence Diagram details the chronological message exchange between system objects during the execution of the AI Trip Planner workflow. Figure 5.6 illustrates this interaction sequence.",
                "The sequence begins when the user triggers 'Generate Trip Plan' on `AITripPlannerScreen`. The event invokes `generateTripPlan()` on `TripPlannerViewModel`, which dispatches an asynchronous coroutine via `NetworkTripPlannerRepository`.",
                "The repository issues a `POST /api/v1/ai-planner/plan` request to the FastAPI backend. The backend executes sequential service calls: querying Nominatim for coordinates, Overpass API for POIs, Open-Meteo for weather forecasts, and OSRM for driving routes. The `place_scoring_service` scores POIs, `itinerary_generator` builds day slots, and `social_content_repository` queries the SQLite database for destination reels. A structured 200 OK JSON response is returned to Retrofit, mapped to domain entities, and emitted via `StateFlow` to update the Compose UI."
            ],
            "figure": {
                "id": "Figure 5.6",
                "title": "Sequence Diagram of AI Trip Planning Workflow",
                "file": "fig_5_6_sequence_diagram.png"
            }
        },
        {
            "id": "5.7",
            "title": "Data Flow Diagram",
            "content": [
                "The Data Flow Diagram (DFD) documents how data moves through the TripDirect system. Figure 5.7 illustrates both the DFD Level 0 (Context Diagram) and DFD Level 1 (Functional Decomposition).",
                "• Level 0 Context Diagram: Models the entire TripDirect system as a single central process interacting with two external entities: the Tourist (Mobile User) and External Cloud Services (Nominatim, Overpass, Open-Meteo, OSRM). The user provides trip preferences, credentials, and search queries, receiving structured itineraries, budget analyses, and route geometries.",
                "• Level 1 Decomposition: Deconstructs the system into seven primary sub-processes: 1.0 User Authentication & Token Handler, 2.0 Destination & Nearby POI Discovery, 3.0 Multi-Factor Place Scoring Engine, 4.0 Weather-Aware Dynamic Itinerary Generation, 5.0 Parametric Budget Estimation, 6.0 Multi-Modal Route & OSRM Geometry, and 7.0 Emergency Spatial Safety & Helpline Directory."
            ],
            "figure": {
                "id": "Figure 5.7",
                "title": "Data Flow Diagram (DFD Context Level 0 & Level 1 Decomposition)",
                "file": "fig_5_7_dfd.png"
            }
        },
        {
            "id": "5.8",
            "title": "Entity Relationship Diagram",
            "content": [
                "The Entity Relationship (ER) Diagram defines the relational schema implemented in the SQLite database via SQLAlchemy 2.0 ORM models. Figure 5.8 illustrates the database entities, attributes, primary keys (PK), foreign keys (FK), and cardinality relationships.",
                "Core Relational Entities:",
                "• `users`: Stores user identity, unique email, BCrypt password hash, full name, role, and account status.",
                "• `trips`: Records generated and saved trips, linking to `users.id` via a 1-to-Many foreign key relationship.",
                "• `trip_days`: Represents individual days within a trip, linked to `trips.id` via 1-to-Many cascade delete constraints.",
                "• `itinerary_slots`: Individual scheduled activities (morning/afternoon/evening) within a trip day, linked to `trip_days.id`.",
                "• `saved_budgets`: Itemized financial breakdown records linked to `trips.id` in a 1-to-1 relationship.",
                "• `reviews`: User reviews and star ratings (1 to 5), linked to `users.id` with unique compound constraints preventing duplicate reviews per destination.",
                "• `packages` & `destination_social_content`: Catalog records for tour package comparisons and curated destination video reels."
            ],
            "figure": {
                "id": "Figure 5.8",
                "title": "Entity Relationship (ER) Diagram of Database Architecture",
                "file": "fig_5_8_er_diagram.png"
            }
        },
        {
            "id": "5.9",
            "title": "Application Flowchart",
            "content": [
                "The Application Flowchart outlines the low-level logic, conditional branches, and operational decisions executed by the mobile client from launch to termination. Figure 5.4 displays this flowchart.",
                "The process initiates at the splash entry point, validating local token integrity. If invalid, the user is routed to authentication. Upon successful login, the main dashboard renders. When a feature is selected, the application validates input constraints (e.g., destination non-empty, dates valid, budget positive). If validation passes, background coroutines query APIs or local caches. Upon response receipt, the UI recomposes smoothly; otherwise, informative error snackbars and offline fallback modes are engaged."
            ],
            "figure": {
                "id": "Figure 5.4",
                "title": "Mobile Application Operational Flowchart",
                "file": "fig_5_4_application_flowchart.png"
            }
        },
        {
            "id": "5.10",
            "title": "Navigation Flow / Screen Flow",
            "content": [
                "The Screen Flow Diagram visualizes the navigation graph of TripDirect, mapping route transitions between all 14 application modules managed by `AppNavigation.kt`. Figure 5.9 illustrates the complete navigation architecture.",
                "Navigation is centered around a persistent Material 3 `TripDirectBottomNavigation` bar hosting five primary top-level tabs: Home, Explore, Planner, Trips, and Profile. Deep navigation flows branch outward to specialized screens, including DestinationDetails, PlaceDetailsScreen, BudgetBreakdownScreen, NavigationScreen, HotelCompareScreen, TransportSearchScreen, WriteReviewScreen, and SOSActiveScreen, supporting predictable back-stack traversal via the Android NavController."
            ],
            "figure": {
                "id": "Figure 5.9",
                "title": "Application Screen Navigation Flow",
                "file": "fig_5_9_screen_flow.png"
            }
        }
    ]
}

CHAPTER_6 = {
    "num": "6",
    "title": "DEVELOPMENT TOOLS AND TECHNOLOGIES",
    "sections": [
        {
            "id": "6.1",
            "title": "Development Environment",
            "content": [
                "The development of TripDirect was conducted in a modern, command-line-driven software engineering environment. Development operations utilized the Google Antigravity IDE paired with the Windows 11 operating system. Build processes, dependency resolutions, and automated testing suites were executed directly via command-line tools without proprietary IDE locking, guaranteeing complete project reproducibility.",
                "The environment integrates the Microsoft OpenJDK 21 LTS runtime for Gradle orchestration, Kotlin 2.2.0 compiler targeting Java 17 bytecode, Python 3.13.13 for backend microservices, and the Android SDK command-line platform tools (API Level 36)."
            ],
            "figure": {
                "id": "Figure 6.1",
                "title": "Mobile Application Development Platform & Toolchain",
                "file": "fig_6_1_dev_environment.png"
            }
        },
        {
            "id": "6.2",
            "title": "Android Studio / Development IDE",
            "content": [
                "The project was primarily developed using the Antigravity IDE and command-line Gradle automation (`cmd /c gradlew.bat`), with Android SDK platform-tools 36. Android Studio and the Android SDK command-line utilities were employed for SDK management, AVD (Android Virtual Device) emulator provisioning, and device debugging via ADB (Android Debug Bridge).",
                "Gradle 9.1.0 serves as the primary build automation engine, configured via Kotlin DSL (`build.gradle.kts` and `settings.gradle.kts`) with the Android Gradle Plugin (AGP) version 8.9. Build caching and configuration cache reuse were utilized to achieve sub-minute incremental compilation times."
            ]
        },
        {
            "id": "6.3",
            "title": "Programming Language – Kotlin",
            "content": [
                "Kotlin 2.2.0 is the official programming language used for the TripDirect Android mobile application. Kotlin provides distinct modern language capabilities that make it superior for mobile development:",
                "• Null Safety: First-class nullability types (`String?` vs `String`) eliminate `NullPointerException` crashes at compile-time.",
                "• Coroutines & Asynchronous Flow: Kotlin Coroutines provide lightweight, non-blocking asynchronous execution. Network calls and database transactions execute off the main thread using `Dispatchers.IO`, while UI updates remain on `Dispatchers.Main`.",
                "• Extension Functions & Data Classes: Clean data modeling using concise `data class` declarations and domain extension helpers.",
                "• Sealed Interfaces for UI State: Complete representation of UI state machines using `sealed interface UiState` (Loading, Success, Error)."
            ]
        },
        {
            "id": "6.4",
            "title": "XML / Jetpack Compose for UI Design",
            "content": [
                "TripDirect is built 100% using Jetpack Compose, completely replacing legacy imperative XML layouts. Jetpack Compose provides a modern, declarative paradigm where UI elements are expressed as reusable, pure Kotlin composable functions.",
                "The application utilizes Material Design 3 (Material3), incorporating customized typography styles, rounded cards, elevation shadows, fluid lazy lists, and responsive scaffold structures. Composable state is managed using `remember`, `mutableStateOf`, and `collectAsStateWithLifecycle()`, delivering smooth 60 FPS performance without unnecessary View hierarchy overhead."
            ]
        },
        {
            "id": "6.5",
            "title": "Backend Technology – FastAPI",
            "content": [
                "The backend service is built using FastAPI (Python 3.13) running on the Uvicorn ASGI server. FastAPI was chosen for its outstanding asynchronous performance, automated OpenAPI / Swagger documentation generation, and strict compile-time request validation powered by Pydantic v2.",
                "The backend architecture is structured under `/api/v1` across 16 modular routers, encapsulating authentication, destinations, POIs, AI trip planning, budget calculations, routing, hotels, transport, package comparison, reviews, and emergency safety."
            ]
        },
        {
            "id": "6.6",
            "title": "Database Technology",
            "content": [
                "Data persistence is implemented using SQLAlchemy 2.0 ORM interfacing with a local SQLite 3 database (`tripdirect.db`). SQLite provides a zero-configuration, serverless, self-contained SQL database engine embedded directly within the application filesystem, ideal for local development and edge deployments.",
                "The database layer is architecturally decoupled, allowing seamless production migration to PostgreSQL simply by updating the `DATABASE_URL` environment variable in the `.env` configuration file without code alterations."
            ]
        },
        {
            "id": "6.7",
            "title": "APIs and Web Services",
            "content": [
                "TripDirect integrates four external open-access RESTful web services:",
                "1. OpenStreetMap Nominatim API: Provides forward geocoding, converting location text into latitude and longitude coordinates and structured address profiles.",
                "2. OpenStreetMap Overpass API: Spatial query engine executing Overpass QL scripts to discover nearby points of interest within 15–20 km radius boundaries.",
                "3. Open-Meteo Weather API: High-resolution weather forecast service supplying 16-day daily min/max temperatures, precipitation probabilities, wind speeds, and WMO codes.",
                "4. Project OSRM Routing API: High-performance routing service computing road driving distances, transit durations, and GeoJSON route geometries.",
                "5. Google Places API: Integrated as a supplementary high-resolution photo and place verification provider via Android BuildConfig credentials."
            ]
        },
        {
            "id": "6.8",
            "title": "Version Control – Git / GitHub",
            "content": [
                "Git 2.40+ was employed for distributed version control throughout the project lifecycle. Feature branches were maintained for discrete modules, and commits followed conventional commit standards. Comprehensive `.gitignore` configurations ensured build artifacts, Gradle caches, API keys, and virtual environments were excluded from version history."
            ]
        },
        {
            "id": "6.9",
            "title": "Emulator / Physical Device Testing",
            "content": [
                "Verification was conducted across both Android Virtual Devices (AVD) and physical Android smartphones:",
                "• Android Emulator: Google Pixel 8 virtual device running Android 14 (API level 34) with Google Play Services, connecting to host server endpoints via the special alias `http://10.0.2.2:8000/api/v1/`.",
                "• Physical Smartphone: Android physical mobile phone connected over local Wi-Fi, verifying network discovery, camera permissions, touch responsiveness, and cleartext HTTP traffic across local subnets (`http://192.168.x.x:8000/api/v1/`)."
            ]
        },
        {
            "id": "6.10",
            "title": "Other Development Tools and Libraries",
            "content": [
                "Figure 6.2 illustrates the complete multi-tiered development technology stack. Table 6.1 lists the software tools and technologies utilized, while Table 6.2 summarizes the minimum hardware and software engineering requirements."
            ],
            "figure": {
                "id": "Figure 6.2",
                "title": "TripDirect Full-Stack Technology Architecture",
                "file": "fig_6_2_tech_stack.png"
            }
        }
    ]
}

TABLE_6_1 = {
    "title": "Table 6.1: Software Development Tools and Technologies",
    "headers": ["Tool / Technology", "Version / Release", "Category / Layer", "Purpose in TripDirect"],
    "rows": [
        ["Kotlin", "2.2.0", "Programming Language", "Core Android mobile application business logic and UI"],
        ["Jetpack Compose", "BOM 2024.09.00", "UI Framework", "Modern declarative UI rendering with Material Design 3"],
        ["Android SDK", "API Level 36", "Mobile Platform SDK", "Target compilation platform (compileSdk 36, minSdk 24)"],
        ["Gradle", "9.1.0", "Build Automation", "Dependency management, compilation, and unit test execution"],
        ["FastAPI", "0.115+", "Backend Framework", "Asynchronous Python REST API service engine"],
        ["Python", "3.13.13", "Backend Language", "Server-side microservices, scoring engine, and test suite"],
        ["Uvicorn", "0.30+", "ASGI Web Server", "High-performance asynchronous HTTP server hosting FastAPI"],
        ["SQLAlchemy", "2.0+", "ORM Database Layer", "Relational database modeling, session management, and queries"],
        ["SQLite", "3.x", "Relational Database", "Local persistent data storage for users, trips, budgets, and reviews"],
        ["Retrofit", "2.11.0", "Networking Client", "Type-safe HTTP REST client for Android with Gson serialization"],
        ["OkHttp", "4.12.0", "HTTP Engine", "Underlying HTTP transport, logging interceptor, and timeouts"],
        ["Coil", "2.6.0", "Image Loader", "Asynchronous image downloading and memory caching in Compose"],
        ["Pytest", "9.1.1", "Testing Framework", "Automated backend unit and integration test runner (107 tests)"],
        ["Antigravity IDE", "2.0", "Integrated Dev Env", "Primary code authoring, terminal execution, and project design"],
        ["Git", "2.40+", "Version Control", "Source code management and version tracking"]
    ]
}

TABLE_6_2 = {
    "title": "Table 6.2: Hardware and Software Requirements for Application Development",
    "headers": ["Component Category", "Minimum Requirement", "Recommended Specification", "Verified Development Environment"],
    "rows": [
        ["Processor (CPU)", "Intel Core i5 / AMD Ryzen 5", "Intel Core i7 / AMD Ryzen 7 (8+ Cores)", "AMD Ryzen 7 / Intel Core i7 (x86_64)"],
        ["System Memory (RAM)", "8 GB DDR4", "16 GB DDR4/DDR5 or higher", "16 GB High-Speed RAM"],
        ["Storage Space", "20 GB available SSD space", "50 GB NVMe Solid State Drive", "512 GB High-Speed NVMe SSD"],
        ["Host Operating System", "Windows 10 / Ubuntu 20.04 LTS", "Windows 11 64-bit / macOS Sonoma", "Microsoft Windows 11 64-bit"],
        ["Mobile Target Device", "Android 7.0 (Nougat, API 24)", "Android 14 / 15 (API 34-36)", "Android 14 (Pixel 8 Emulator & Physical Phone)"],
        ["Internet Connectivity", "2 Mbps stable broadband", "10 Mbps high-speed Wi-Fi", "100 Mbps Low-Latency Fiber Broadband"],
        ["Java Development Kit", "OpenJDK 17 LTS", "Microsoft OpenJDK 21 LTS", "Microsoft OpenJDK 21.0.11+10 LTS"],
        ["Python Runtime", "Python 3.10+", "Python 3.12 / 3.13 64-bit", "Python 3.13.13 (Global & Virtualenv)"]
    ]
}

CHAPTER_7 = {
    "num": "7",
    "title": "MOBILE APPLICATION IMPLEMENTATION",
    "sections": [
        {
            "id": "7.1",
            "title": "Application Setup and Configuration",
            "content": [
                "The configuration of TripDirect strictly follows modern Android development standards. The project is organized into two primary root workspaces: the native Android project in `TripDirect/app` and the FastAPI microservices service in `TripDirect/backend`.",
                "Android Gradle configuration (`app/build.gradle.kts`) specifies `compileSdk = 36`, `minSdk = 24`, and `targetSdk = 36`, with Java 17 bytecode compatibility. Network cleartext HTTP traffic is explicitly enabled in `AndroidManifest.xml` via `android:usesCleartextTraffic=\"true\"` to permit local development communication between the Android emulator and the local backend server (`10.0.2.2:8000`). Google Places API keys are securely loaded from `local.properties` into `BuildConfig`."
            ]
        },
        {
            "id": "7.2",
            "title": "Project Structure",
            "content": [
                "The project architecture enforces modularity and clean architectural layering across both mobile client and backend server. Figure 7.1 visualizes the complete directory and package hierarchy.",
                "The Android mobile application contains 146 Kotlin source files structured under `com.tripdirect` into `data/`, `domain/`, `navigation/`, `theme/`, `ui/`, and `viewmodel/`. The backend service is organized into `core/`, `models/`, `repositories/`, `routers/`, `schemas/`, `services/`, and `tests/`."
            ],
            "figure": {
                "id": "Figure 7.1",
                "title": "Application Package and Source Code Hierarchy",
                "file": "fig_7_1_project_structure.png"
            }
        },
        {
            "id": "7.3",
            "title": "User Interface Design",
            "content": [
                "The user interface is constructed using Jetpack Compose declarative composables. Figure 7.2 depicts the UI wireframe and composable hierarchy.",
                "Screen layouts utilize `Scaffold` containers providing top app bars, bottom navigation bars, and floating action buttons. Scrollable views utilize `LazyColumn` and `LazyRow` with unique key identifiers to optimize recomposition performance. Color palettes, typography styles, and card elevations adhere to Material Design 3 tokens."
            ],
            "figure": {
                "id": "Figure 7.2",
                "title": "Jetpack Compose Declarative UI Architecture",
                "file": "fig_7_2_ui_wireframe.png"
            }
        },
        {
            "id": "7.4",
            "title": "User Registration and Login",
            "content": [
                "User authentication is implemented in `LoginScreen.kt`, `RegisterScreen.kt`, and `ForgotPasswordScreen.kt`, backed by `AuthViewModel` and `AuthRepository`. On the server, `routers/auth.py` exposes `/api/v1/auth/register`, `/login`, `/me`, `/refresh`, and `/logout`.",
                "Passwords are encrypted using PassLib BCrypt with 12 salt rounds. Upon successful verification, the server issues a signed JSON Web Token (JWT) with HS256 encryption and a 24-hour expiration horizon. The token is transmitted via the HTTP `Authorization: Bearer <token>` header."
            ]
        },
        {
            "id": "7.5",
            "title": "Home Screen / Dashboard",
            "content": [
                "The Home Dashboard (`HomeScreen.kt`) serves as the central hub of TripDirect. It features a top search bar, dynamic promotional banners (`Banners.kt`), a horizontal category selection carousel, and featured destination cards (`DestinationCard.kt`) displaying ratings and photographic previews. Tapping any card opens the detailed destination profile."
            ]
        },
        {
            "id": "7.6",
            "title": "Application Modules",
            "content": [
                "TripDirect implements 14+ cohesive functional modules. Table 7.1 details each mobile application module, its primary responsibilities, and its verified implementation status."
            ]
        },
        {
            "id": "7.7",
            "title": "Database Implementation",
            "content": [
                "The database is implemented using SQLAlchemy 2.0 ORM interfacing with SQLite 3 (`tripdirect.db`). Figure 7.3 illustrates the database configuration, and Table 7.2 enumerates the data entities and relational schema definitions."
            ],
            "figure": {
                "id": "Figure 7.3",
                "title": "Application Database Configuration & SQLAlchemy Mapping",
                "file": "fig_7_3_database_config.png"
            }
        },
        {
            "id": "7.8",
            "title": "API Integration",
            "content": [
                "The mobile application interfaces with the backend REST engine through Retrofit 2.11 and OkHttp 4.12. Figure 7.4 details the network serialization pipeline, and Table 7.3 documents all verified REST API endpoints."
            ],
            "figure": {
                "id": "Figure 7.4",
                "title": "REST API Integration and Network Serialization Pipeline",
                "file": "fig_7_4_api_integration.png"
            }
        },
        {
            "id": "7.9",
            "title": "Authentication and Authorization",
            "content": [
                "Authorization employs role-based access control (RBAC). Protected endpoints—such as saving itineraries, uploading profile avatars, and deleting reviews—require a valid Bearer token. Unauthenticated requests are rejected with HTTP 401 Unauthorized status codes."
            ]
        },
        {
            "id": "7.10",
            "title": "Notifications and Alerts",
            "content": [
                "TripDirect incorporates contextual alert dialogs, Material 3 Snackbars, and SOS alert confirmations (`SOSActiveScreen.kt`). Weather alert banners automatically display when severe rain or storm conditions are forecasted for scheduled travel dates."
            ]
        },
        {
            "id": "7.11",
            "title": "Data Validation and Error Handling",
            "content": [
                "Client-side forms enforce real-time regex validation for email formats, non-empty text fields, and numerical budget limits. The server enforces strict Pydantic v2 schema validations. If remote spatial APIs timeout, the backend seamlessly degrades to cached data or local SQLite fallbacks, ensuring zero application crashes."
            ]
        },
        {
            "id": "7.12",
            "title": "Source Code Implementation",
            "content": [
                "Figure 7.5 details the complete request processing lifecycle. Selected algorithmic source code excerpts—including the multi-factor place scoring formula (`place_scoring_service.py`), parametric budget calculation (`budget_engine.py`), and domain route optimization (`RouteOptimizer.kt`)—are documented below.",
                "Listing 7.1: Multi-Factor Place Scoring Formula (Python)",
                "```python\n# From backend/app/services/place_scoring_service.py\nscore = (\n    0.30 * interest_match +\n    0.20 * rating_score +\n    0.20 * distance_score +\n    0.15 * budget_score +\n    0.15 * suitability_score\n)\n```",
                "Listing 7.2: Domain Route Optimization Heuristic (Kotlin)",
                "```kotlin\n// From app/src/main/java/com/tripdirect/domain/RouteOptimizer.kt\nvar current = unvisited.first()\nresult.add(current)\nunvisited.remove(current)\nwhile (unvisited.isNotEmpty()) {\n    val nearest = unvisited.minByOrNull { calculateEuclideanDistance(current, it) }\n    if (nearest != null) {\n        result.add(nearest)\n        unvisited.remove(nearest)\n        current = nearest\n    } else break\n}\n```"
            ],
            "figure": {
                "id": "Figure 7.5",
                "title": "Application Implementation and Processing Workflow",
                "file": "fig_7_5_implementation_workflow.png"
            }
        }
    ]
}

TABLE_7_1 = {
    "title": "Table 7.1: Mobile Application Modules and Their Functionalities",
    "headers": ["Module Name", "Primary Android Screen", "Backend Endpoint", "Key Technical Functionality", "Implementation Status"],
    "rows": [
        ["Authentication", "LoginScreen, RegisterScreen", "/api/v1/auth/*", "User registration, BCrypt password hashing, JWT session tokens", "Verified Implemented"],
        ["Home Dashboard", "HomeScreen", "/api/v1/home/*", "Destination discovery, promotional banners, featured places carousel", "Verified Implemented"],
        ["Destination Search", "SearchDestinationScreen", "/api/v1/destinations/*", "Natural language search, OpenStreetMap Nominatim geocoding", "Verified Implemented"],
        ["Explore Places", "ExplorePlacesScreen, PlaceDetails", "/api/v1/places/*", "Overpass API POI queries, category filtering, photo attributions", "Verified Implemented"],
        ["AI Trip Planner", "AITripPlannerScreen, GeneratedTrip", "/api/v1/trips/plan", "Multi-factor POI scoring, weather-aware scheduling, day slots", "Verified Implemented"],
        ["Weather Forecast", "TripDayDetailsScreen", "/api/v1/services/external", "Open-Meteo 16-day forecast, min/max temps, rain probabilities", "Verified Implemented"],
        ["Budget Calculator", "BudgetCalculatorScreen, Breakdown", "/api/v1/budget/*", "Parametric cost breakdown across 6 categories, solvency checks", "Verified Implemented"],
        ["Route Navigation", "RouteMapScreen, NavigationScreen", "/api/v1/routes/*", "Project OSRM road routing, travel times, GeoJSON polyline mapping", "Verified Implemented"],
        ["Itinerary Scheduling", "ItineraryScreen, DayDetailsScreen", "/api/v1/itineraries/*", "Daily schedule slots, custom activity additions, cost tracking", "Verified Implemented"],
        ["Hotels & Lodging", "HotelSearchScreen, HotelCompare", "/api/v1/hotels/*", "Filter by price, star rating, distance, and verified amenities", "Verified Implemented"],
        ["Transport Transit", "TransportSearchScreen, Compare", "/api/v1/transport/*", "Multi-modal comparison (Flight, Train, Bus, Cab, Rental Car)", "Verified Implemented"],
        ["Package Compare", "PackageComparisonScreen", "/api/v1/packages/*", "Side-by-side package matrix, inclusions, user preference weighting", "Verified Implemented"],
        ["Reviews & Ratings", "ReviewsScreen, WriteReviewScreen", "/api/v1/reviews/*", "1-5 star ratings, text comments, duplicate review prevention", "Verified Implemented"],
        ["Emergency Safety", "EmergencyScreen, SOSActiveScreen", "/api/v1/safety/*", "Spatial discovery of nearby hospitals/police, verified helplines", "Verified Implemented"],
        ["Real Visuals Reels", "DestinationDetailScreen", "/api/v1/destinations/{id}", "Curated vertical short-form video reels from verified creators", "Verified Implemented"],
        ["User Profile", "ProfileScreen, EditProfileScreen", "/api/v1/profile/*", "User settings, avatar image uploads, travel style preferences", "Verified Implemented"]
    ]
}

TABLE_7_2 = {
    "title": "Table 7.2: Database Tables and Data Entities",
    "headers": ["Entity / Table Name", "Primary Key", "Foreign Keys", "Key Fields / Attributes", "Purpose in System"],
    "rows": [
        ["users", "id (Integer)", "None", "email, hashed_password, full_name, role, is_active, created_at", "User credentials, identity, and role-based permissions"],
        ["trips", "id (Integer)", "user_id -> users.id", "destination, start_date, end_date, travelers, budget, created_at", "Generated and saved user travel itineraries"],
        ["trip_days", "id (Integer)", "trip_id -> trips.id", "day_number, title, date, weather_summary, total_cost", "Daily chronological segments within a multi-day trip"],
        ["itinerary_slots", "id (Integer)", "day_id -> trip_days.id", "slot_id, time_of_day, activity, location, category, estimated_cost", "Individual scheduled activities (morning/afternoon/evening)"],
        ["saved_budgets", "id (Integer)", "trip_id -> trips.id", "transportation, accommodation, food, activities, local_travel, total", "Itemized financial expense estimates linked to trips"],
        ["reviews", "id (Integer)", "user_id -> users.id", "target_id, target_type, rating (1-5), comment, created_at", "Crowd-sourced visitor feedback and star ratings"],
        ["packages", "id (Integer)", "None", "name, destination, duration_days, price, rating, inclusions", "Catalog of pre-configured tour packages for comparison"],
        ["places", "id (Integer)", "None", "name, category, location, latitude, longitude, rating, image_url", "Local cache of verified destination attractions and POIs"],
        ["social_content", "id (String)", "None", "destination_id, platform, title, reel_url, thumbnail_url, creator", "Curated destination video reels ('Real Visuals')"]
    ]
}

TABLE_7_3 = {
    "title": "Table 7.3: REST API and Web Service Endpoint Details",
    "headers": ["HTTP Method", "Endpoint Path", "Parameters / Body", "Response Schema", "Authentication Required"],
    "rows": [
        ["POST", "/api/v1/auth/register", "UserCreate (email, password, full_name)", "UserResponse (id, email, name)", "No (Public)"],
        ["POST", "/api/v1/auth/login", "OAuth2PasswordRequestForm (username, password)", "Token (access_token, token_type)", "No (Public)"],
        ["GET", "/api/v1/auth/me", "None (Bearer Token in Header)", "UserResponse (id, email, name, role)", "Yes (Bearer JWT)"],
        ["GET", "/api/v1/home/dashboard", "None", "HomeDashboardResponse (banners, featured)", "No (Public)"],
        ["GET", "/api/v1/destinations/search", "query: str, limit: int", "List[DestinationSearchItem]", "No (Public)"],
        ["GET", "/api/v1/places/nearby", "latitude: float, longitude: float, radius: int", "List[PlaceItemSchema]", "No (Public)"],
        ["POST", "/api/v1/ai-planner/plan", "AiPlannerRequest (dest, dates, budget, style)", "AiPlannerNetworkResponse (plan, weather, budget)", "Optional (Guest/Auth)"],
        ["POST", "/api/v1/budget/calculate", "BudgetCalculateRequest (budget, days, travelers)", "BudgetSummarySchema (categories, solvency)", "No (Public)"],
        ["POST", "/api/v1/routes/calculate", "RouteCalculateRequest (origin, destination)", "RouteItem (distance_km, duration_min, geometry)", "No (Public)"],
        ["GET", "/api/v1/hotels/search", "destination: str, max_price: float, min_rating: float", "List[HotelSchema]", "No (Public)"],
        ["GET", "/api/v1/transport/search", "origin: str, destination: str, mode: str", "List[TransportSchema]", "No (Public)"],
        ["GET", "/api/v1/packages/compare", "package_ids: str, preference: str", "PackageComparisonResponse", "No (Public)"],
        ["POST", "/api/v1/reviews", "ReviewCreate (target_id, rating, comment)", "ReviewResponse", "Yes (Bearer JWT)"],
        ["GET", "/api/v1/safety/nearby", "latitude: float, longitude: float", "SafetyServicesResponse (hospitals, police)", "No (Public)"],
        ["POST", "/api/v1/profile/upload-image", "UploadFile (multipart/form-data image)", "ProfileImageResponse (image_url)", "Yes (Bearer JWT)"]
    ]
}

CHAPTER_8 = {
    "num": "8",
    "title": "USER INTERFACE AND USER EXPERIENCE",
    "sections": [
        {
            "id": "8.1",
            "title": "UI/UX Design Principles",
            "content": [
                "The user interface of TripDirect is engineered to deliver a seamless, intuitive, and accessible experience across diverse mobile screen dimensions. Key design principles enforced throughout the application include:",
                "1. Clarity and Visual Hierarchy: Clean typography, prominent headings, and distinct content cards guide the user's attention naturally through planning stages.",
                "2. Consistency Across Modules: Universal color tokens, typography scales, iconographies, and button styles ensure intuitive familiarity across all 14 screens.",
                "3. Minimal Interaction Friction: One-click planning shortcuts, sensible default selections (e.g., origin defaulting to 'Chennai'), and instant autocomplete reduce input effort.",
                "4. Feedback and State Visibility: Loading indicators, progress animations, and confirmation snackbars provide continuous feedback during asynchronous operations.",
                "5. Graceful Degradation: Informative offline banners and fallback data ensure the application remains functional even when internet connectivity drops."
            ]
        },
        {
            "id": "8.2",
            "title": "Application Theme and Layout",
            "content": [
                "TripDirect implements Material Design 3 (Material3) with a curated, professional color scheme:",
                "• Primary Brand Color: Deep Academic Blue (`#2563EB`) communicating trustworthiness, authority, and clarity.",
                "• Secondary Accent Color: Emerald Green (`#10B981`) highlighting budgetary solvency, positive ratings, and confirmations.",
                "• Warning & SOS Accent: Crimson Red (`#EF4444`) reserved for emergency SOS triggers, cancellations, and critical error alerts.",
                "• Surface & Background: Light Slate (`#F8FAFC`) paired with Pure White (`#FFFFFF`) card containers, optimizing contrast and readability under outdoor sunlight."
            ]
        },
        {
            "id": "8.3",
            "title": "Screen Design",
            "content": [
                "Each screen in TripDirect is implemented as a dedicated composable function. Table 8.1 lists the application screens, their composable files, and their functional UI elements."
            ]
        },
        {
            "id": "8.4",
            "title": "Navigation Design",
            "content": [
                "Navigation utilizes Android Jetpack Navigation Compose (`AppNavigation.kt`). Figure 8.4 visualizes the bottom navigation bar and multi-stack screen transitions.",
                "The bottom navigation bar persists across primary destinations, maintaining independent back-stacks for each tab to prevent unexpected navigation state loss when switching between planning, exploration, and profile management."
            ],
            "figure": {
                "id": "Figure 8.4",
                "title": "Application Bottom Navigation and Multi-Stack Screen Flow",
                "file": "fig_8_4_navigation_screens.png"
            }
        },
        {
            "id": "8.5",
            "title": "Input Forms and Validation",
            "content": [
                "Input forms utilize custom `AuthTextField.kt` components featuring floating labels, leading icons, clear buttons, and dynamic error state indicators. Validation occurs on-the-fly, giving users immediate visual feedback before form submission."
            ]
        },
        {
            "id": "8.6",
            "title": "Responsive Design",
            "content": [
                "All UI layouts leverage Compose's flexible sizing modifiers (`fillMaxWidth`, `weight`, `wrapContentHeight`), ensuring seamless scaling across compact smartphones, standard handsets, and larger tablet displays."
            ]
        },
        {
            "id": "8.7",
            "title": "Accessibility Considerations",
            "content": [
                "TripDirect incorporates Android accessibility best practices: touch targets adhere to Google's 48x48 dp minimum sizing guideline, all interactive icons provide descriptive `contentDescription` text for screen readers (TalkBack), and text color combinations meet WCAG 2.1 AA contrast ratios (4.5:1 minimum)."
            ]
        },
        {
            "id": "8.8",
            "title": "Screenshots of Application Interfaces",
            "content": [
                "The developed user interface screens are documented across Figures 8.1, 8.2, 8.3, and 8.5:",
                "• Figure 8.1 illustrates the Authentication screens: Login, Registration, and Password Recovery.",
                "• Figure 8.2 illustrates the Home Discovery Dashboard, Search Destination screen, and Destination Detail screen.",
                "• Figure 8.3 illustrates Prototype Development: Explore Places, Real Visuals video feed, and Hotel Comparison.",
                "• Figure 8.5 illustrates Core Functional Feature Interfaces: AI Trip Planner, Budget Calculator, and Tourist Emergency SOS."
            ],
            "figures": [
                {
                    "id": "Figure 8.1",
                    "title": "TripDirect User Authentication and Onboarding Screens",
                    "file": "fig_8_1_auth_screens.png"
                },
                {
                    "id": "Figure 8.2",
                    "title": "TripDirect Home Screen and Destination Discovery Dashboard",
                    "file": "fig_8_2_home_dashboard.png"
                },
                {
                    "id": "Figure 8.3",
                    "title": "TripDirect Prototype Development and Component Engineering",
                    "file": "fig_8_3_prototype_development.png"
                },
                {
                    "id": "Figure 8.5",
                    "title": "TripDirect Feature Interfaces: AI Planner, Budget Calculator, and SOS Safety",
                    "file": "fig_8_5_developed_application_screens.png"
                }
            ]
        }
    ]
}

TABLE_8_1 = {
    "title": "Table 8.1: Application Screens and Their Functionalities",
    "headers": ["Screen Name", "Composable File Path", "ViewModel Attached", "UI Elements & Components"],
    "rows": [
        ["SplashScreen", "ui/screens/splash/SplashScreen.kt", "AuthViewModel", "App branding logo, animated progress loader, auth check"],
        ["LoginScreen", "ui/screens/auth/LoginScreen.kt", "AuthViewModel", "Email field, password visibility toggle, sign-in button, Google OAuth"],
        ["RegisterScreen", "ui/screens/auth/RegisterScreen.kt", "AuthViewModel", "Full name, email, password strength meter, terms checkbox"],
        ["HomeScreen", "ui/screens/home/HomeScreen.kt", "HomeViewModel", "Search bar, category chips, banner slider, featured destination cards"],
        ["SearchScreen", "ui/screens/search/SearchDestinationScreen.kt", "SearchViewModel", "Search text input, recent queries list, geocoded result items"],
        ["DestinationDetail", "ui/screens/explore/DestinationDetailScreen.kt", "DestinationDetailViewModel", "Photo header, weather chip, description, reel player, POI row"],
        ["ExplorePlaces", "ui/screens/explore/ExplorePlacesScreen.kt", "ExplorePlacesViewModel", "Category filter bar, distance slider, POI cards with ratings"],
        ["AITripPlanner", "ui/screens/planner/AITripPlannerScreen.kt", "TripPlannerViewModel", "Dates picker, budget field, style selector, interests chips"],
        ["GeneratedTrip", "ui/screens/planner/GeneratedTripScreen.kt", "TripPlannerViewModel", "Day-by-day itinerary cards, time slots, weather tags, route link"],
        ["BudgetCalculator", "ui/screens/budget/BudgetCalculatorScreen.kt", "BudgetCalculatorViewModel", "Travelers counter, budget slider, category cost fields, solvency card"],
        ["RouteMapScreen", "ui/screens/route/RouteMapScreen.kt", "RouteMapViewModel", "Map container, driving duration, distance (km), step-by-step turns"],
        ["ItineraryScreen", "ui/screens/itinerary/ItineraryScreen.kt", "ItineraryViewModel", "Timeline view, morning/afternoon slots, cost totals, slot editor"],
        ["EmergencyScreen", "ui/screens/safety/EmergencyScreen.kt", "EmergencyViewModel", "Prominent SOS alert button, nearest hospitals list, police dialers"],
        ["ProfileScreen", "ui/screens/profile/ProfileScreen.kt", "ProfileViewModel", "Avatar preview, travel stats, preference toggles, logout action"]
    ]
}

print("Chapters 5-8 loaded.")
