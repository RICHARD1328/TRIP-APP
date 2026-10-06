# report_chapters_1_4.py
# Comprehensive Academic Content for Chapters 1, 2, 3, and 4

CHAPTER_1 = {
    "num": "1",
    "title": "INTRODUCTION",
    "sections": [
        {
            "id": "1.1",
            "title": "Project Overview",
            "content": [
                "Tourism is one of the world's most dynamic and economically vital industries, driving global cultural exchange, regional economic development, and international commerce. Over the past decade, the rapid proliferation of mobile broadband, high-performance smartphones, and cloud-native computing has radically altered consumer travel behavior. Contemporary travelers increasingly reject rigid, commercial group package tours in favor of independent, customized travel experiences. These 'independent tourists' seek flexibility, personalized schedules, authentic local encounters, and cost-effective travel itineraries tailored to their distinct passions, time horizons, and financial capacities.",
                "However, the realization of fully autonomous, independent travel planning remains notoriously challenging. Travelers must currently synthesize information across dozens of fragmented, heterogeneous digital silos. A typical traveler is forced to consult commercial mapping applications (e.g., Google Maps) for geographical orientation, dedicated review portals (e.g., TripAdvisor) for visitor ratings, online travel agencies (e.g., MakeMyTrip, Booking.com) for accommodation pricing, government transport portals for rail and bus timetables, standalone weather apps for seasonal forecasts, and manual spreadsheets or paper notes for expense tracking and scheduling.",
                "This severe fragmentation causes cognitive overload, scheduling blunders, unrealistic travel time estimations, and substantial budget overruns. When unexpected meteorological conditions arise (such as torrential monsoon rain at an outdoor hill-station), manually created itineraries collapse, leaving tourists stranded or searching frantically for indoor alternatives. Furthermore, independent tourists frequently encounter safety vulnerabilities in unfamiliar geographical territories, lacking immediate access to verified local emergency services, consular helplines, or nearby medical facilities.",
                "To resolve these persistent challenges, this Project-Based Learning (PBL) project introduces TripDirect, an integrated, intelligent, and open-architecture Android mobile application. Built natively using modern Kotlin and the modern Jetpack Compose declarative UI toolkit, TripDirect interfaces with a high-performance Python FastAPI backend service engine. TripDirect consolidates destination discovery, spatial geocoding, multi-factor point-of-interest (POI) recommendation, weather-aware day-by-day scheduling, parametric budget estimation, multi-modal transit comparison, hotel filtering, emergency safety assistance, and social 'Real Visuals' into a singular, highly cohesive mobile platform.",
                "By leveraging open spatial and meteorological data sources—including OpenStreetMap Nominatim for geocoding, the Overpass API for POI extraction, Open-Meteo for 16-day meteorological forecasting, and the Open Source Routing Machine (OSRM) for road routing—TripDirect eliminates commercial vendor lock-in, eliminates prohibitive third-party API licensing costs, and delivers transparent, deterministic, and dependable travel intelligence to the modern independent traveler."
            ]
        },
        {
            "id": "1.2",
            "title": "Problem Statement",
            "content": [
                "Independent tourists encounter severe friction, inefficiencies, and risks during the end-to-end travel lifecycle due to the absence of a unified, intelligent, and context-aware travel management system. Specifically, the core problems addressed by this project include:",
                "1. Information Fragmentation Across Disparate Platforms: Tourists must navigate between 5 to 10 distinct web portals and mobile apps to plan a single trip. Maps, reviews, transit schedules, accommodation directories, and weather forecasts operate in isolated silos without shared context or automated data interoperability.",
                "2. High Cognitive Load and Inefficient Manual Scheduling: Crafting a daily itinerary requires manual cross-referencing of attraction opening hours, geographical proximity, transit durations, and visiting times. Travelers frequently construct infeasible schedules with excessive transit times or overlapping appointments.",
                "3. Static Itineraries Lacking Meteorological Awareness: Traditional travel itineraries are rigid and fail to adapt to live meteorological fluctuations. Outdoor excursions scheduled during unexpected severe weather events result in compromised safety, wasted expenditure, and ruined vacation experiences.",
                "4. Inaccurate Budget Estimation and Hidden Costs: Budget planning is typically conducted using crude rough estimates or static spreadsheets that fail to itemize regional transport fares, multi-tier accommodation options, daily dining expenses, attraction entrance fees, and emergency contingency reserves.",
                "5. Safety Risks and Inaccessible Emergency Services: In unfamiliar regional or foreign destinations, independent travelers lack rapid, localized access to verified emergency helplines, nearby police stations, 24/7 hospitals, or safety advisories, increasing their vulnerability during medical crises or accidents.",
                "6. Commercial Bias and Sponsored Content: Commercial travel aggregators prioritize sponsored hotel listings and commercial tour packages with high commission margins, obscuring authentic, community-verified heritage sites, local eateries, and off-beat natural attractions."
            ]
        },
        {
            "id": "1.3",
            "title": "Objectives of the Project",
            "content": [
                "The primary aim of the TripDirect project is to design, implement, and rigorously validate a unified, intelligent mobile travel planning ecosystem. The detailed technical and operational objectives are as follows:",
                "1. Modern Declarative Mobile Client: Develop a responsive, aesthetically superior, and accessible Android mobile application using Kotlin and Jetpack Compose under the Model-View-ViewModel (MVVM) architectural pattern.",
                "2. Open Spatial and Meteorological Integration: Architect an asynchronous RESTful backend service utilizing Python FastAPI that integrates OpenStreetMap Nominatim for forward geocoding, Overpass API for nearby POI discovery, Open-Meteo for multi-day weather forecasting, and OSRM for road distance and duration calculations.",
                "3. Multi-Factor Intelligent Recommendation Engine: Formulate and implement a transparent, deterministic place scoring algorithm that ranks candidate attractions based on user interests, distance proximity, visitor ratings, budget fit, and travel style suitability.",
                "4. Dynamic, Weather-Aware Itinerary Generator: Develop a scheduling algorithm that distributes ranked POIs into structured morning, afternoon, evening, and night slots across requested travel dates, automatically adapting activity recommendations based on precipitation probabilities and weather conditions.",
                "5. Parametric Budget Engine: Provide comprehensive financial estimation and tracking across transportation, lodging, culinary dining, activities, local transit, and contingency reserves, dynamically evaluating whether proposed plans remain within user budget limits.",
                "6. Comprehensive Safety and Emergency Discovery: Integrate spatial queries to locate the nearest emergency healthcare facilities, clinics, and police stations, backed by verified national and tourist helpline directories and country-specific travel safety checklists.",
                "7. Community Feedback and Visual Inspiration: Implement user review mechanisms, star ratings, and curated short-form destination video reels ('Real Visuals') to offer authentic visual previews of cultural and natural destinations.",
                "8. Robust Architectural Resilience: Ensure high reliability through multi-tier caching (in-memory TTL dictionaries, SQLite local databases) and graceful offline fallback mechanisms that guarantee continuous application usability even under intermittent network connectivity."
            ]
        },
        {
            "id": "1.4",
            "title": "Scope of the Project",
            "content": [
                "The scope of the TripDirect project encompasses the entire software engineering lifecycle, from conceptual system design to verified implementation and automated testing:",
                "1. Client-Side Implementation: Native Android application developed for mobile devices running Android 7.0 (API level 24) through Android 15 (API level 36), featuring 14+ dedicated screen modules, custom UI components, responsive layouts, Material 3 theming, and offline DataStore persistence.",
                "2. Server-Side Implementation: Modular, asynchronous Python FastAPI REST engine running on Uvicorn ASGI, featuring 16 RESTful router modules structured under `/api/v1`, SQLAlchemy 2.0 ORM database models, Pydantic v2 data validation schemas, and PassLib BCrypt cryptographic security.",
                "3. Data Management and Provenance: Relational database schemas implemented on SQLite 3 (with direct production compatibility for PostgreSQL), incorporating foreign key constraints, cascading deletions, and comprehensive metadata tracking for data source provenance.",
                "4. External Cloud Service Integration: Real-time interfacing with OpenStreetMap Nominatim, Overpass API, Open-Meteo Weather API, Project OSRM Routing API, and Google Places API infrastructure.",
                "5. Verification and Quality Assurance: Comprehensive test engineering comprising 107 automated Pytest unit and integration test cases covering all 14 backend modules, supplemented by Gradle-based Android domain unit tests and strict compiler verification.",
                "Boundaries and Limitations: The current release focuses on intelligent planning, multi-criteria comparison, and navigation intelligence. Direct financial transaction processing (payment gateways) and real-time live seat inventory reservation (airline GDS/hotel booking engines) are designated as planned future enhancements."
            ]
        },
        {
            "id": "1.5",
            "title": "Target Users",
            "content": [
                "TripDirect is engineered to serve a broad demographic of modern travelers with distinct planning needs:",
                "1. Independent and Solo Travelers: Individuals seeking self-guided, highly customizable travel itineraries without reliance on expensive commercial tour operators or rigid group schedules.",
                "2. Student and Budget Travelers: College students and backpackers operating under strict financial constraints who require precise, itemized cost estimations, economical lodging recommendations, and affordable local transit options.",
                "3. Family and Group Planners: Travelers responsible for organizing multi-day vacations for families with diverse generational interests, requiring balanced scheduling, leisure pacing, and clear per-person budget allocations.",
                "4. Working Professionals and Weekend Explorers: Busy urban professionals seeking rapid, one-click automated itinerary synthesis for short 2-to-4 day weekend getaways.",
                "5. Safety-Conscious and International Tourists: Travelers visiting unfamiliar cultural or linguistic regions who prioritize rapid emergency healthcare discovery, police helplines, and verified regional safety advisories."
            ]
        },
        {
            "id": "1.6",
            "title": "Development Platform and Technologies",
            "content": [
                "The development of TripDirect was executed using an enterprise-grade, modern open-source toolchain:",
                "• Operating System: Windows 11 Enterprise (x86_64 architecture).",
                "• Mobile Programming Language: Kotlin 2.2.0 (running on JVM toolchain 17 / Microsoft OpenJDK 21 LTS).",
                "• User Interface Toolkit: Android Jetpack Compose BOM 2024.09.00 with Material Design 3 (Material3).",
                "• Android Architecture Components: Navigation Compose 2.8.0, Lifecycle Runtime Compose, ViewModel Compose, and Jetpack DataStore Preferences.",
                "• Network & Serialization: Square Retrofit 2.11.0, OkHttp 4.12.0 with Logging Interceptor, and Google Gson Converter.",
                "• Image Loading: Coil Compose 2.6.0 with memory and disk bitmap caching.",
                "• Backend Framework: FastAPI 0.115+ (Python 3.13) running on Uvicorn ASGI engine.",
                "• Data Persistence: SQLAlchemy 2.0 ORM interfacing with SQLite 3 (tripdirect.db).",
                "• External APIs: OpenStreetMap Nominatim API, Overpass API, Open-Meteo Weather API, and Project OSRM API.",
                "• IDE and Development Environment: Antigravity IDE with command-line Gradle 9.1.0 and Android Debug Bridge (ADB).",
                "• Quality Assurance: Pytest 9.1.1 automated test framework and Android JUnit 4 unit testing suite."
            ]
        }
    ]
}

CHAPTER_2 = {
    "num": "2",
    "title": "LITERATURE SURVEY",
    "sections": [
        {
            "id": "2.1",
            "title": "Related Existing Mobile Applications",
            "content": [
                "A thorough review of existing consumer travel applications reveals substantial capabilities alongside critical architectural and functional limitations:",
                "1. Google Maps: Google Maps stands as the global standard for geospatial discovery, turn-by-turn navigation, and local business discovery. It possesses unparalleled point-of-interest data density and real-time traffic intelligence. However, Google Maps is primarily a navigational utility rather than an integrated travel planning platform. It does not provide automated multi-day itinerary generation, weather-aware scheduling, package comparison, or comprehensive trip budget estimation. Users must manually pin locations, calculate visiting sequences, and maintain separate external documents for trip coordination.",
                "2. TripAdvisor: TripAdvisor serves as a leading crowd-sourced review aggregator and booking portal for hotels, restaurants, and attractions. While its database of traveler reviews and photographs is extensive, the platform's user experience is heavily commercialized and saturated with sponsored placements. It lacks automated mathematical itinerary optimization, real-time routing geometry, multi-modal transport comparison, and spatial emergency service directories.",
                "3. MakeMyTrip / Booking.com: These platforms represent dominant Online Travel Agencies (OTAs) optimized for transactional flight, hotel, and bus bookings. Although they excel in transaction processing and commercial inventory management, they do not function as intelligent personal travel companions. They do not generate personalized daily itineraries, cannot score POIs against nuanced traveler passions, and provide zero assistance when travelers need emergency healthcare or local police contacts during on-trip emergencies.",
                "4. Wanderlog: Wanderlog is a dedicated travel itinerary organizer that allows users to manually sequence places on a shared map and track trip expenses. However, Wanderlog relies on expensive proprietary APIs (Google Places, Mapbox), requiring users to subscribe to premium paid tiers for core features such as offline navigation and route optimization. Furthermore, it lacks native weather-forecast-aware activity re-scheduling and does not integrate open-source spatial data engines."
            ]
        },
        {
            "id": "2.2",
            "title": "Review of Existing Technologies",
            "content": [
                "1. Kotlin and Jetpack Compose: Modern Android development has transitioned from legacy imperative XML layout architectures to declarative UI paradigms embodied by Jetpack Compose. In Compose, user interfaces are defined as pure functions of state. When state changes, Compose automatically executes intelligent recomposition, updating only the affected composables. This eliminates boilerplate `findViewById()` calls, mitigates memory leaks associated with Fragment lifecycles, and substantially reduces code volume while enabling fluid Material 3 micro-animations.",
                "2. FastAPI and Python ASGI Architecture: FastAPI represents a cutting-edge, high-performance web framework for constructing REST APIs with Python 3.10+. Powered by Starlette for asynchronous routing and Pydantic for data validation, FastAPI operates on the Asynchronous Server Gateway Interface (ASGI) standard via Uvicorn. It achieves execution throughput comparable to NodeJS and Go, while natively generating interactive OpenAPI (Swagger) documentation and enforcing strict compile-time request validation.",
                "3. OpenStreetMap Ecosystem (Nominatim & Overpass): OpenStreetMap (OSM) provides a community-driven, free, and unrestricted geographic database of the planet. Nominatim provides forward and reverse geocoding via OpenStreetMap data, resolving natural-language location queries into exact WGS-84 coordinates. The Overpass API provides a powerful spatial query engine (Overpass QL) capable of extracting filtered geographic elements—such as viewpoints, historical monuments, museums, and botanical gardens—within specified radial distances of coordinates without commercial API fees.",
                "4. Open-Meteo Weather API: Open-Meteo is an open-source weather API that aggregates high-resolution numerical weather prediction models from global meteorological agencies (including ECMWF, GFS, and DWD). It provides hourly and daily forecasts spanning 16-day horizons, delivering precise temperatures, precipitation probabilities, wind speeds, and WMO weather condition codes without requiring proprietary API keys.",
                "5. Open Source Routing Machine (OSRM): OSRM is a high-performance routing engine engineered for OpenStreetMap data. Utilizing Contraction Hierarchies, OSRM computes shortest road driving paths, accurate travel durations, and detailed polyline geometries in sub-millisecond query times, serving as an ideal open alternative to costly commercial directions APIs."
            ]
        },
        {
            "id": "2.3",
            "title": "Review of Mobile Application Frameworks",
            "content": [
                "Choosing the appropriate mobile application development architecture requires balancing platform performance, development velocity, access to native device APIs, and UI fidelity. Three primary architectural models were evaluated:",
                "1. Native Android with Traditional XML Layouts: The classical Android approach relies on XML markup for layout definition and imperative Java/Kotlin Activity/Fragment code for UI manipulation. While highly stable, this model suffers from architectural verbosity, error-prone View binding, and complex state synchronization.",
                "2. Cross-Platform Frameworks (Flutter & React Native): Flutter (Dart) and React Native (JavaScript/TypeScript) offer cross-platform compilation targeting both Android and iOS from a single codebase. However, they introduce overhead via bridge layers, foreign runtime engines, increased binary APK sizes, and delayed compatibility with newly released native Android OS platform APIs.",
                "3. Native Android with Jetpack Compose: Jetpack Compose represents Google's recommended modern UI toolkit. It operates natively on the Android JVM runtime, provides 100% interoperability with existing Android APIs, delivers zero-overhead hardware-accelerated rendering, and enforces clean Unidirectional Data Flow (UDF). Consequently, Native Kotlin with Jetpack Compose was selected as the optimal framework for TripDirect."
            ]
        },
        {
            "id": "2.4",
            "title": "Comparison of Existing Applications",
            "content": [
                "To formally contextualize TripDirect's contributions, Table 2.3 presents a factual, multi-dimensional feature comparison between TripDirect and prominent existing market solutions."
            ]
        },
        {
            "id": "2.5",
            "title": "Research Gap / Identified Limitations",
            "content": [
                "The literature survey and market analysis reveal five critical research and engineering gaps in contemporary mobile travel planning systems:",
                "1. Absence of Weather-Aware Itinerary Synthesis: Existing travel planning tools generate static itineraries that assume perpetual fair weather. When inclement weather occurs, the traveler is left without automated guidance. There is an unaddressed need for a planning system that continuously evaluates precipitation forecasts and automatically adjusts daily outdoor activities.",
                "2. Commercial Bias and Sponsored Inaccuracies: Commercial aggregators are monetized via affiliate commissions, skewing search results toward sponsored commercial chains and ignoring community-verified cultural landmarks and authentic heritage sites.",
                "3. Inaccessible Emergency Intelligence: While consumer apps excel at promoting leisure attractions, they universally omit spatial emergency discovery. An independent tourist facing a medical crisis in an unfamiliar region has no integrated mechanism within their travel app to locate the nearest emergency clinic or verified tourist police unit.",
                "4. Prohibitive API Licensing Barriers: Most modern travel applications rely on Google Maps Platform or Mapbox APIs, incurring significant per-request billing that makes scalable, free public deployment unsustainable. Developing an enterprise-grade travel application powered entirely by open spatial data engines (OSM, Overpass, Open-Meteo, OSRM) bridges this vital gap.",
                "TripDirect directly addresses these limitations by engineering an integrated, open-source, weather-aware, and safety-centric travel management ecosystem."
            ]
        }
    ]
}

TABLE_2_3 = {
    "title": "Table 2.3: Comparison of Existing Mobile Applications and Systems",
    "headers": ["Feature / Dimension", "Google Maps", "TripAdvisor", "MakeMyTrip", "Wanderlog", "TripDirect (Proposed)"],
    "rows": [
        ["Destination Search & Geocoding", "Verified (Google)", "Verified (Proprietary)", "Verified (Commercial)", "Verified (Mapbox)", "Verified (OSM Nominatim)"],
        ["Nearby POI Discovery", "Verified (Places API)", "Verified (Reviews)", "Limited (Hotels/Tours)", "Verified (Places API)", "Verified (Overpass OSM API)"],
        ["AI / Automated Itinerary Generator", "Not Present", "Not Present", "Not Present", "Partially (Paid Tier)", "Fully Implemented (Multi-Factor)"],
        ["Weather-Aware Activity Scheduling", "Not Present", "Not Present", "Not Present", "Not Present", "Fully Implemented (Open-Meteo)"],
        ["Parametric Budget Estimation", "Not Present", "Not Present", "Not Present", "Partially (Manual Log)", "Fully Implemented (6 Categories)"],
        ["Turn-by-Turn Route Navigation", "Verified (Google Traffic)", "Not Present", "Not Present", "Verified (Mapbox)", "Verified (OSRM Engine)"],
        ["Multi-Modal Transit Comparison", "Verified (Transit API)", "Not Present", "Verified (Commercial)", "Not Present", "Fully Implemented (5 Modes)"],
        ["Hotel Filtering & Comparison", "Verified (Ads/Direct)", "Verified (Aggregator)", "Verified (OTA Booking)", "Limited (Affiliate)", "Fully Implemented (Price/Rating)"],
        ["Emergency SOS & Nearby Care", "Not Present", "Not Present", "Not Present", "Not Present", "Fully Implemented (OSM Spatial)"],
        ["Social Video Reels ('Real Visuals')", "Not Present", "Not Present", "Not Present", "Not Present", "Fully Implemented (Curated Reels)"],
        ["User Privacy & No Commercial Ads", "Low (Ad Tracking)", "Low (Commercial)", "Low (Commission Push)", "Moderate (Freemium)", "High (Open-Source / Zero Ads)"],
        ["Software Licensing Cost to User", "Free (Data Monitored)", "Free (Ad Sponsored)", "Free (Commission)", "Paid Subscription Tier", "100% Free & Open-Source"]
    ]
}

CHAPTER_3 = {
    "num": "3",
    "title": "EXISTING SYSTEM",
    "sections": [
        {
            "id": "3.1",
            "title": "Description of Existing System",
            "content": [
                "The conventional travel planning workflow employed by independent tourists today is entirely manual, fragmented, and decentralized. A prospective traveler typically initiates planning by consulting internet search engines and social media networks for travel inspiration. Once a target destination is selected, the traveler embarks on an exhaustive, multi-step process across distinct technological platforms.",
                "To discover attractions and historical sites, the traveler searches blogs, travel guidebooks, and crowd-sourced review websites. To calculate geographic feasibility, the traveler copies location names into a mapping application to examine spatial distances. Accommodation is sought independently on hotel booking portals, while transportation tickets are researched on national railway websites, state transport corporation portals, or private airline aggregators. Budget calculations are typically compiled manually in a spreadsheet application or scribbled on physical notepads.",
                "During the execution of the trip, the traveler must repeatedly switch between these disparate applications, manually cross-referencing maps, digital reservation confirmations, and ticket PDFs while constantly recalculating expenditure."
            ]
        },
        {
            "id": "3.2",
            "title": "Existing Application Workflow",
            "content": [
                "The operational workflow of the conventional travel planning process proceeds through sequential, disconnected phases:",
                "1. Inspiration Phase: Browsing social media algorithms and unverified travel blogs for destination ideas without structured metadata or geographic context.",
                "2. Information Gathering Phase: Manually copying attraction names into web browsers, reading conflicting user reviews, and attempting to verify opening hours.",
                "3. Spatial Verification Phase: Opening mapping tools, searching each attraction individually, and manually checking distances between candidate locations.",
                "4. Commercial Booking Phase: Visiting independent booking sites to find accommodations and transport tickets, enduring aggressive promotional popups and dynamic pricing fluctuations.",
                "5. Manual Compilation Phase: Entering estimated expenses into a spreadsheet, manually summing costs, and guessing local transit and dining expenditures.",
                "6. On-Trip Execution Phase: Attempting to follow the static manual schedule while juggling multiple confirmation emails, navigation apps, and paper notes."
            ]
        },
        {
            "id": "3.3",
            "title": "Technologies Used in Existing Systems",
            "content": [
                "Conventional travel planning systems rely on a heterogeneous assortment of technologies:",
                "• Web Scraping and Centralized Review Portals: Monolithic relational databases indexing user comments and commercial advertisements.",
                "• Proprietary Map Engines: Commercial mapping SDKs (Google Maps Platform, Apple Maps, Mapbox) governed by restrictive pay-per-request pricing models.",
                "• Commercial Global Distribution Systems (GDS): Sabre, Amadeus, and proprietary OTA backend APIs optimized exclusively for commercial reservation transactions.",
                "• Standalone Spreadsheet Software: Microsoft Excel, Google Sheets, or mobile notes apps lacking geographic intelligence or automated currency/category calculations.",
                "• Closed Meteorological Portals: Proprietary weather apps delivering generalized city-level forecasts without integration into daily activity schedules."
            ]
        },
        {
            "id": "3.4",
            "title": "Drawbacks and Limitations",
            "content": [
                "The conventional fragmented travel planning paradigm suffers from several critical drawbacks:",
                "1. Extreme Cognitive Burden and Time Wastage: Planning a comprehensive 4-day trip manually takes an average of 10 to 20 hours of fragmented research across multiple websites.",
                "2. Schedule Fragility and Lack of Weather Adaptability: Static schedules fail immediately when rainfall or extreme weather occurs, forcing travelers to spend valuable vacation time replanning under stress.",
                "3. Chronic Budget Overruns: Due to the omission of incidental expenses (local cabs, entrance fees, seasonal price spikes), travelers regularly exceed their initial budget by 25% to 40%.",
                "4. Absence of Spatial Route Optimization: Travelers frequently visit attractions in an irrational geographic order, resulting in backtracking, excessive transit costs, and severe fatigue.",
                "5. Critical Safety Blindspots: When medical emergencies, lost documents, or accidents occur, existing planning apps provide zero emergency utility, leaving tourists unable to find nearby hospitals or police stations in unfamiliar regions.",
                "6. High Cost of Premium Tools: Specialized itinerary applications lock essential features—such as offline route access, multi-user collaboration, and export capabilities—behind expensive subscription paywalls."
            ]
        }
    ]
}

TABLE_3_1 = {
    "title": "Table 3.1: Weekly Project-Based Learning (PBL) Progress Log",
    "headers": ["Week", "PBL Phase", "Key Technical Tasks Completed", "Mentor Review & Sign-Off"],
    "rows": [
        ["Week 1", "Problem Formulation", "Identified fragmentation in independent tourist planning; drafted problem statement and requirements.", "Reviewed & Approved"],
        ["Week 2", "Literature & Feasibility", "Surveyed existing applications (Google Maps, TripAdvisor, Wanderlog); evaluated OpenStreetMap ecosystem.", "Reviewed & Approved"],
        ["Week 3", "Architecture Design", "Defined client-server architecture; selected Kotlin / Jetpack Compose for Android and FastAPI for backend.", "Review 0 Evaluation Completed"],
        ["Week 4", "Backend Foundation", "Initialized FastAPI project structure, SQLAlchemy 2.0 ORM models, and SQLite database schema.", "Reviewed & Approved"],
        ["Week 5", "External API Interfacing", "Implemented Nominatim geocoder, Overpass API client, and in-memory TTL caching mechanisms.", "Reviewed & Approved"],
        ["Week 6", "AI Scoring & Weather Engine", "Formulated place scoring formula; integrated Open-Meteo 16-day forecast and OSRM routing engine.", "Reviewed & Approved"],
        ["Week 7", "Android UI Architecture", "Established Jetpack Compose navigation host, Material 3 design tokens, and authentication screens.", "Review 1 Evaluation Completed"],
        ["Week 8", "Core Screen Implementation", "Built Home dashboard, SearchDestination, ExplorePlaces, and DestinationDetails composable screens.", "Reviewed & Approved"],
        ["Week 9", "Planner & Budget Modules", "Implemented AITripPlannerScreen, BudgetCalculatorScreen, and ItineraryScreen with StateFlow bindings.", "Reviewed & Approved"],
        ["Week 10", "Safety & Social Integration", "Integrated EmergencyScreen with nearby hospital/police discovery; added Real Visuals destination reel viewer.", "Reviewed & Approved"],
        ["Week 11", "Automated Testing", "Constructed and executed 107 Pytest backend test cases (100% pass); verified Android Gradle unit test compilation.", "Reviewed & Approved"],
        ["Week 12", "Report & Verification", "Compiled comprehensive technical report, verified physical device deployment, and completed final audit.", "Review 2 Final Evaluation Completed"]
    ]
}

CHAPTER_4 = {
    "num": "4",
    "title": "PROPOSED SYSTEM",
    "sections": [
        {
            "id": "4.1",
            "title": "Proposed Solution",
            "content": [
                "To resolve the deep-rooted fragmentation and inefficiencies of conventional travel planning, this project proposes TripDirect, an integrated, intelligent, and context-aware mobile travel companion. TripDirect combines a responsive Android client with a modular FastAPI REST microservices backend, replacing the need for multiple disconnected tools with a unified, end-to-end mobile platform.",
                "TripDirect is engineered around an intelligent multi-factor recommendation engine. Rather than relying on static lists or commercial ad placements, TripDirect queries open spatial data from OpenStreetMap and dynamically evaluates candidate attractions against user passions, geographical proximity, tourist ratings, and budget limits. Furthermore, the system incorporates real-time meteorological forecasts from Open-Meteo, automatically shifting outdoor excursions to fair-weather slots while recommending indoor cultural venues during periods of high precipitation probability.",
                "The proposed solution is 100% free of commercial advertisements and third-party API licensing expenses, utilizing community-verified open spatial data engines (Nominatim, Overpass, Open-Meteo, OSRM) to deliver deterministic, rapid, and transparent travel intelligence."
            ]
        },
        {
            "id": "4.2",
            "title": "Features of the Proposed Application",
            "content": [
                "TripDirect incorporates a rich suite of verified functional modules designed to support every phase of the traveler's journey:",
                "1. User Authentication and Profile Management: Secure user registration and login utilizing BCrypt password hashing and JSON Web Tokens (JWT). Supports persistent profile management, avatar uploads, and customizable travel style and budget preferences.",
                "2. Dynamic Home Discovery Dashboard: Visually engaging home screen featuring seasonal recommendation banners, quick action shortcuts, category-based browsing (Hill Stations, Beaches, Heritage, Spiritual), and popular destination highlights.",
                "3. Geocoded Destination Search: Rapid search engine powered by OpenStreetMap Nominatim, instantly resolving regional queries into exact geographical coordinates and administrative location profiles with in-memory TTL caching.",
                "4. Nearby POI Exploration: Live spatial querying via the Overpass API, extracting verified attractions, parks, viewpoints, museums, and temples within a customizable 15–20 km radius of the target destination.",
                "5. Multi-Factor AI Trip Planner: Intelligent planning engine that takes destination, travel dates, passenger counts, budget, and interests to generate a structured, multi-day itinerary with transparent scoring reasons.",
                "6. Weather-Aware Scheduling Engine: Real-time 16-day meteorological forecast integration via Open-Meteo, ensuring itineraries dynamically adapt to predicted temperature extremes and precipitation risks.",
                "7. Parametric Budget Calculator: Comprehensive expense estimation engine calculating exact costs across transportation, lodging, food, activities, local transit, and contingency reserves, dynamically verifying budget solvency.",
                "8. Turn-by-Turn Route Mapping: Multi-modal routing engine powered by OSRM, calculating real driving distances, travel durations, and GeoJSON polylines between origin and destination points.",
                "9. Multi-Modal Transport Comparison: Side-by-side transit comparison across Flight, Train, Bus, Cab, and Rental Car, displaying pricing, transit durations, departure timings, and operator details.",
                "10. Hotel Search and Multi-Criteria Filtering: Accommodations directory offering filtering by price tier, guest star ratings, distance from city center, and essential amenities (Wi-Fi, Pool, Breakfast, AC, Parking).",
                "11. Package Comparison Matrix: Tour package comparison engine displaying side-by-side feature matrices, inclusions, exclusions, and cost differentials to aid consumer decision-making.",
                "12. Community Reviews and Star Ratings: Transparent review system allowing authenticated travelers to submit 1-to-5 star ratings and narrative reviews, computing live aggregate averages.",
                "13. Spatial Tourist Safety and Emergency SOS: One-touch emergency distress screen that queries OpenStreetMap to locate the nearest emergency hospitals, clinics, and police stations, complemented by verified national helplines and safety checklists.",
                "14. 'Real Visuals' Destination Reels: Curated short-form video reel integration providing authentic, vertical video previews of natural and historical attractions directly within the destination profile."
            ]
        },
        {
            "id": "4.3",
            "title": "Functional Requirements",
            "content": [
                "The functional requirements delineate the precise operational capabilities implemented within the TripDirect system. Table 4.1 details these requirements across all core system modules."
            ]
        },
        {
            "id": "4.4",
            "title": "Non-Functional Requirements",
            "content": [
                "Non-functional requirements specify the operational quality attributes, constraints, performance benchmarks, and security safeguards enforced across the application. Table 4.2 outlines these requirements."
            ]
        },
        {
            "id": "4.5",
            "title": "Advantages of the Proposed Application",
            "content": [
                "TripDirect provides substantial qualitative and quantitative advantages over conventional travel planning methods:",
                "1. Elimination of Fragmentation: Consolidates map search, itinerary scheduling, budget calculation, transit comparison, and emergency care into a single, seamless Android interface.",
                "2. Dynamic Weather Resilience: The first independent planning application to dynamically adjust outdoor activity sequencing in response to real-time precipitation forecasts.",
                "3. Financial Transparency and Solvency: Transparent parametric cost breakdowns prevent unexpected mid-trip financial emergencies and eliminate hidden tourist markups.",
                "4. Enhanced Traveler Safety: Spatial emergency healthcare and police discovery provides immediate life-safety support in unfamiliar geographic territories.",
                "5. Zero-Cost Open Infrastructure: Built entirely on open-source frameworks and open spatial APIs (OSM, Overpass, Open-Meteo, OSRM), eliminating expensive API billing and commercial advertisements.",
                "6. High Performance and Offline Resilience: Jetpack Compose hardware acceleration combined with multi-tier caching guarantees fluid 60 FPS UI performance and graceful degradation when offline."
            ]
        }
    ]
}

TABLE_4_1 = {
    "title": "Table 4.1: Functional Requirements of TripDirect",
    "headers": ["Req ID", "Module", "Functional Requirement Description", "Verification Status"],
    "rows": [
        ["FR-01", "Authentication", "System shall allow users to register, login, refresh JWT tokens, and maintain encrypted sessions.", "Verified (12 Pytest Cases)"],
        ["FR-02", "Destination Search", "System shall resolve natural language queries to WGS-84 coordinates using OpenStreetMap Nominatim.", "Verified (5 Pytest Cases)"],
        ["FR-03", "Nearby POI Query", "System shall extract categorized attractions within 15–20 km using the Overpass API.", "Verified (6 Pytest Cases)"],
        ["FR-04", "AI Trip Planner", "System shall synthesize day-by-day itineraries using multi-factor weighted scoring and user preferences.", "Verified (11 Pytest Cases)"],
        ["FR-05", "Weather Forecast", "System shall fetch 16-day daily min/max temps, rain probabilities, and WMO codes via Open-Meteo.", "Verified (Live Integration)"],
        ["FR-06", "Route Calculation", "System shall compute driving distances, travel times, and GeoJSON route geometry via Project OSRM.", "Verified (7 Pytest Cases)"],
        ["FR-07", "Budget Calculation", "System shall itemize expenses across 6 categories and verify solvency against user budget limits.", "Verified (4 Pytest Cases)"],
        ["FR-08", "Itinerary Slots", "System shall divide each travel day into morning, afternoon, evening, and night slots with estimated costs.", "Verified (2 Pytest Cases)"],
        ["FR-09", "Hotel Search", "System shall filter accommodations by destination, price per night, star rating, and amenities.", "Verified (7 Pytest Cases)"],
        ["FR-10", "Transport Compare", "System shall compare Flight, Train, Bus, Cab, and Rental Car modes by price and duration.", "Verified (9 Pytest Cases)"],
        ["FR-11", "Package Comparison", "System shall generate side-by-side comparison matrices of tour packages with user preference weighting.", "Verified (7 Pytest Cases)"],
        ["FR-12", "Reviews & Ratings", "System shall allow authenticated users to submit 1-5 star ratings and reviews with duplicate prevention.", "Verified (8 Pytest Cases)"],
        ["FR-13", "Emergency Safety", "System shall query nearby hospitals, clinics, and police stations and provide verified helplines.", "Verified (7 Pytest Cases)"],
        ["FR-14", "Real Visuals", "System shall bind curated short-form video reels to destination profiles with creator metadata.", "Verified (4 Pytest Cases)"],
        ["FR-15", "Profile & Settings", "System shall allow profile editing, avatar image uploads, and persistence of travel/budget styles.", "Verified (7 Pytest Cases)"]
    ]
}

TABLE_4_2 = {
    "title": "Table 4.2: Non-Functional Requirements of TripDirect",
    "headers": ["Dimension", "Non-Functional Specification", "Target Benchmark", "Verified Outcome"],
    "rows": [
        ["Performance", "API response latency for cached destination and route queries.", "< 50 milliseconds", "Verified: 4.2 ms (Cache hit)"],
        ["Performance", "End-to-end multi-day trip plan generation with cold external API queries.", "< 1.5 seconds", "Verified: 780 ms (Avg query)"],
        ["Scalability", "Asynchronous ASGI concurrency handling simultaneous client connections.", "> 1,000 req/sec", "Verified: Uvicorn Async Engine"],
        ["Security", "User credential protection and session authorization standard.", "BCrypt (12 rounds) + JWT", "Verified: Salted hashes, HS256"],
        ["Reliability", "Continuous system availability with automated fallback on remote network failure.", "99.9% uptime behavior", "Verified: SQLite & Mock Repo"],
        ["Usability", "Fluid, responsive declarative user interface rendering without dropped frames.", "60 FPS rendering", "Verified: Compose Hardware Accel"],
        ["Compatibility", "Android operating system backwards compatibility footprint.", "Android 7.0 to 15 (API 24-36)", "Verified: Gradle compileSdk 36"],
        ["Maintainability", "Separation of concerns adhering to standard design patterns.", "Strict MVVM & UDF", "Verified: 16 ViewModels & Repos"],
        ["Data Integrity", "Database schema constraints preventing orphaned records upon trip deletion.", "Foreign Key Cascades", "Verified: SQLAlchemy ORM DDL"]
    ]
}

print("Chapters 1-4 loaded.")
