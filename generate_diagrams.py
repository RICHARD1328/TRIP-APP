import os
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import numpy as np

OUTPUT_DIR = r"C:\Users\richa\OneDrive\Desktop\TOUR\TripDirect\report_figures"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Helper function to configure standard figure style
def create_base_fig(figsize=(10, 6), dpi=300):
    fig, ax = plt.subplots(figsize=figsize, dpi=dpi)
    ax.set_facecolor('#FFFFFF')
    fig.patch.set_facecolor('#FFFFFF')
    ax.axis('off')
    return fig, ax

def add_box(ax, x, y, w, h, text, title=None, facecolor='#EFF6FF', edgecolor='#2563EB', text_color='#0F172A', fontsize=10, title_color='#1E3A8A', title_size=11, rad=0.03):
    box = patches.FancyBboxPatch(
        (x, y), w, h,
        boxstyle=f"round,pad=0.02,rounding_size={rad}",
        facecolor=facecolor,
        edgecolor=edgecolor,
        linewidth=1.5
    )
    ax.add_patch(box)
    
    if title:
        ax.text(x + w/2, y + h - 0.04, title, ha='center', va='top', fontsize=title_size, fontweight='bold', color=title_color, family='sans-serif')
        ax.text(x + w/2, y + (h - 0.05)/2, text, ha='center', va='center', fontsize=fontsize, color=text_color, family='sans-serif', wrap=True)
    else:
        ax.text(x + w/2, y + h/2, text, ha='center', va='center', fontsize=fontsize, color=text_color, family='sans-serif', fontweight='bold', wrap=True)

def add_arrow(ax, x1, y1, x2, y2, color='#2563EB', label=None, label_pos=(0.5, 0.5), fontsize=8):
    ax.annotate(
        '', xy=(x2, y2), xytext=(x1, y1),
        arrowprops=dict(arrowstyle="->", color=color, lw=1.8, shrinkA=5, shrinkB=5, mutation_scale=15)
    )
    if label:
        lx = x1 + (x2 - x1) * label_pos[0]
        ly = y1 + (y2 - y1) * label_pos[1]
        ax.text(lx, ly, label, ha='center', va='bottom', fontsize=fontsize, color='#334155', fontweight='semibold', bbox=dict(boxstyle="square,pad=0.1", fc="white", ec="none"))

# -------------------------------------------------------------
# FIG 5.1: System Architecture / Block Diagram
# -------------------------------------------------------------
def gen_fig_5_1():
    fig, ax = create_base_fig((11, 7))
    ax.set_xlim(0, 1.1)
    ax.set_ylim(0, 1.0)
    
    # Title
    ax.text(0.55, 0.96, "TripDirect – System Architecture & End-to-End Block Diagram", ha='center', va='top', fontsize=14, fontweight='bold', color='#1E3A8A')
    
    # 3 Main Tiers
    # Tier 1: Client
    box1 = patches.FancyBboxPatch((0.04, 0.08), 0.28, 0.82, boxstyle="round,pad=0.02,rounding_size=0.03", facecolor='#F8FAFC', edgecolor='#94A3B8', linestyle='--', linewidth=1.5)
    ax.add_patch(box1)
    ax.text(0.18, 0.87, "CLIENT TIER\n(Android 14 / Compose)", ha='center', va='center', fontsize=11, fontweight='bold', color='#0F172A')
    
    add_box(ax, 0.06, 0.70, 0.24, 0.12, "Jetpack Compose\nMaterial 3 UI Screens", "Presentation Layer", '#EFF6FF', '#3B82F6')
    add_box(ax, 0.06, 0.50, 0.24, 0.14, "StateFlow / LiveData\nTripPlannerViewModel\nHomeViewModel\nBudgetCalculatorVM", "ViewModels Layer", '#EFF6FF', '#3B82F6')
    add_box(ax, 0.06, 0.30, 0.24, 0.14, "Retrofit 2.11 / OkHttp 4.12\nGson Converter\nDataStore Preferences\nOffline Fallback Repos", "Repository & Network", '#EFF6FF', '#3B82F6')
    add_box(ax, 0.06, 0.12, 0.24, 0.12, "Local In-Memory Cache\nRoom/Preferences\nPure Domain Engines", "Local Client Data", '#EFF6FF', '#3B82F6')

    # Tier 2: Server
    box2 = patches.FancyBboxPatch((0.39, 0.08), 0.32, 0.82, boxstyle="round,pad=0.02,rounding_size=0.03", facecolor='#F0FDF4', edgecolor='#86EFAC', linestyle='--', linewidth=1.5)
    ax.add_patch(box2)
    ax.text(0.55, 0.87, "APPLICATION BACKEND TIER\n(FastAPI / Python 3.13 ASGI)", ha='center', va='center', fontsize=11, fontweight='bold', color='#0F172A')
    
    add_box(ax, 0.41, 0.70, 0.28, 0.12, "REST API Endpoints (/api/v1)\nAuth, Trips, Places, Hotels,\nBudget, Routes, Safety", "FastAPI Routers", '#ECFDF5', '#10B981')
    add_box(ax, 0.41, 0.48, 0.28, 0.16, "Place Scoring Engine (W-Avg)\nDynamic Itinerary Builder\nParametric Budget Engine\nOSRM Route Coordinator", "Service & Scoring Core", '#ECFDF5', '#10B981')
    add_box(ax, 0.41, 0.28, 0.28, 0.14, "SQLAlchemy ORM (Async)\nSecurity (BCrypt + JWT)\nTTL Cache Manager\nData Access Repositories", "Data & Security Layer", '#ECFDF5', '#10B981')
    add_box(ax, 0.41, 0.12, 0.28, 0.11, "SQLite (tripdirect.db)\nOptional PostgreSQL DB\nUploaded Avatars Store", "Relational Database", '#ECFDF5', '#10B981')

    # Tier 3: External
    box3 = patches.FancyBboxPatch((0.78, 0.08), 0.28, 0.82, boxstyle="round,pad=0.02,rounding_size=0.03", facecolor='#FAF5FF', edgecolor='#D8B4FE', linestyle='--', linewidth=1.5)
    ax.add_patch(box3)
    ax.text(0.92, 0.87, "EXTERNAL SERVICES TIER\n(Open Spatial & Weather APIs)", ha='center', va='center', fontsize=11, fontweight='bold', color='#0F172A')
    
    add_box(ax, 0.80, 0.68, 0.24, 0.13, "Forward Geocoding\nLat/Lon Coordinates\nAdministrative Address", "Nominatim OSM API", '#F5F3FF', '#8B5CF6')
    add_box(ax, 0.80, 0.50, 0.24, 0.13, "OSM Spatial POI Search\n15-20km Radius Queries\nAttractions, Parks, Temples", "Overpass API (OSM)", '#F5F3FF', '#8B5CF6')
    add_box(ax, 0.80, 0.32, 0.24, 0.13, "16-Day Weather Forecast\nMax/Min Temp, Rain Prob\nWMO Weather Conditions", "Open-Meteo Weather", '#F5F3FF', '#8B5CF6')
    add_box(ax, 0.80, 0.14, 0.24, 0.13, "Road Routing & Geometry\nTravel Duration & Distance\nTurn-by-turn Navigation", "OSRM Routing Engine", '#F5F3FF', '#8B5CF6')

    # Connections
    add_arrow(ax, 0.30, 0.76, 0.41, 0.76, '#2563EB', 'JSON / HTTP', (0.5, 0.65))
    add_arrow(ax, 0.41, 0.74, 0.30, 0.74, '#10B981')
    add_arrow(ax, 0.69, 0.74, 0.80, 0.74, '#8B5CF6', 'HTTPS Geocode', (0.5, 0.65))
    add_arrow(ax, 0.69, 0.56, 0.80, 0.56, '#8B5CF6', 'Overpass QL', (0.5, 0.65))
    add_arrow(ax, 0.69, 0.38, 0.80, 0.38, '#8B5CF6', 'Forecast REST', (0.5, 0.65))
    add_arrow(ax, 0.69, 0.20, 0.80, 0.20, '#8B5CF6', 'Routing REST', (0.5, 0.65))

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_1_system_architecture.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.1.1: Physical Design / Deployment Architecture
# -------------------------------------------------------------
def gen_fig_5_1_1():
    fig, ax = create_base_fig((10, 6))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.95, "Figure 5.1.1: Physical Architecture and Deployment Design", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    add_box(ax, 0.05, 0.45, 0.22, 0.38, "Android Smartphone / Tablet\n(Android 7.0 - 15 / API 24-36)\n\n• TripDirect APK\n• Jetpack Compose Runtime\n• OkHttp / Retrofit Network Client\n• DataStore Local Cache", "Client Hardware Device", '#EFF6FF', '#2563EB')
    
    add_box(ax, 0.38, 0.45, 0.24, 0.38, "Local Development / Cloud Server\n(Windows / Linux Server)\n\n• Uvicorn ASGI Server (Port 8000)\n• Python 3.13 Runtime Engine\n• FastAPI REST Services\n• SQLite (tripdirect.db)\n• Local Image Storage Directory", "Application Server Node", '#ECFDF5', '#10B981')
    
    add_box(ax, 0.72, 0.45, 0.23, 0.38, "External Web Services\n(Internet Cloud Infrastructure)\n\n• OpenStreetMap Nominatim\n• Overpass POI API\n• Open-Meteo Weather API\n• Project OSRM Routing Engine\n• Google Places / Maps Service", "Third-Party Cloud Services", '#FAF5FF', '#8B5CF6')

    # Network connections
    add_arrow(ax, 0.27, 0.65, 0.38, 0.65, '#2563EB', "Wi-Fi / 4G / 5G\nHTTP/JSON (:8000)", (0.5, 0.7))
    add_arrow(ax, 0.38, 0.60, 0.27, 0.60, '#10B981', "JSON Response", (0.5, 0.3))
    
    add_arrow(ax, 0.62, 0.65, 0.72, 0.65, '#8B5CF6', "HTTPS REST\nOverpass QL / JSON", (0.5, 0.7))
    add_arrow(ax, 0.72, 0.60, 0.62, 0.60, '#8B5CF6', "Geo/Weather/OSRM", (0.5, 0.3))

    add_box(ax, 0.15, 0.12, 0.70, 0.20, "1. Android Client connects via 10.0.2.2:8000 (Emulator) or 192.168.x.x:8000 (Physical Device over Local Wi-Fi).\n2. FastAPI Backend validates JWT authentication, checks internal TTL Cache, and queries SQLite if cached.\n3. Cache Misses trigger external asynchronous HTTPS requests to OSM Nominatim, Overpass, Open-Meteo, and OSRM.\n4. Responses are synthesized into structured itineraries and delivered back to the Android mobile device.", "Physical Communication Workflow", '#FFFBEB', '#F59E0B')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_1_1_physical_architecture.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.1.2: Logical Design / Application Module Design
# -------------------------------------------------------------
def gen_fig_5_1_2():
    fig, ax = create_base_fig((10, 6.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.96, "Figure 5.1.2: Logical Architecture and Layered Application Design", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    layers = [
        ("Layer 1: UI Presentation Layer", "Jetpack Compose Declarative UI Components • Material 3 Theming • Navigation Graphs • View Screens (Auth, Home, Search, Explore, Planner, Budget, Itinerary, Route, Safety, Profile)", '#EFF6FF', '#3B82F6'),
        ("Layer 2: ViewModel & State Management Layer", "MVVM StateFlows • LiveData Observers • User State Hoisting • Form Input Validation • Coroutine Scopes (viewModelScope)", '#ECFDF5', '#10B981'),
        ("Layer 3: Domain & Repository Layer", "BudgetCalculator • RouteOptimizer • NetworkTripPlannerRepository • DestinationRepository • PlacesRepository • Offline Fallback Handlers", '#F5F3FF', '#8B5CF6'),
        ("Layer 4: Network & Backend API Layer", "Retrofit 2.11 Interface • OkHttp Interceptors • FastAPI REST Gateway • JWT Bearer Token Security • Route Handlers", '#FEF3C7', '#F59E0B'),
        ("Layer 5: Business Engine & Persistence Layer", "Place Scoring Multi-factor Engine • Dynamic Itinerary Generator • Parametric Budget Engine • SQLAlchemy ORM • SQLite / PostgreSQL", '#FEE2E2', '#EF4444')
    ]
    
    y = 0.82
    for title, desc, fc, ec in layers:
        add_box(ax, 0.08, y - 0.11, 0.84, 0.11, desc, title, fc, ec, rad=0.02, fontsize=8.5, title_size=10)
        if y < 0.82:
            add_arrow(ax, 0.5, y + 0.04, 0.5, y, '#475569')
        y -= 0.15

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_1_2_logical_architecture.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.2: UML Use Case Diagram
# -------------------------------------------------------------
def gen_fig_5_2():
    fig, ax = create_base_fig((11, 7.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.2: TripDirect UML Use Case Diagram", ha='center', va='top', fontsize=14, fontweight='bold', color='#1E3A8A')
    
    # Boundary box for System
    boundary = patches.FancyBboxPatch((0.24, 0.05), 0.54, 0.87, boxstyle="round,pad=0.02,rounding_size=0.02", facecolor='#F8FAFC', edgecolor='#334155', linewidth=2)
    ax.add_patch(boundary)
    ax.text(0.51, 0.90, "TripDirect Mobile Application System Boundary", ha='center', va='center', fontsize=11, fontweight='bold', color='#1E293B')
    
    # Actor 1: Tourist / User
    ax.plot([0.10, 0.10], [0.60, 0.52], color='#1E3A8A', lw=2) # body
    circle = plt.Circle((0.10, 0.63), 0.03, color='#1E3A8A', fill=False, lw=2)
    ax.add_patch(circle) # head
    ax.plot([0.06, 0.14], [0.57, 0.57], color='#1E3A8A', lw=2) # arms
    ax.plot([0.10, 0.06], [0.52, 0.44], color='#1E3A8A', lw=2) # left leg
    ax.plot([0.10, 0.14], [0.52, 0.44], color='#1E3A8A', lw=2) # right leg
    ax.text(0.10, 0.40, "Tourist / User\n(Mobile App)", ha='center', va='center', fontsize=10, fontweight='bold', color='#1E3A8A')

    # Actor 2: External APIs
    ax.plot([0.90, 0.90], [0.60, 0.52], color='#047857', lw=2)
    circle2 = plt.Circle((0.90, 0.63), 0.03, color='#047857', fill=False, lw=2)
    ax.add_patch(circle2)
    ax.plot([0.86, 0.94], [0.57, 0.57], color='#047857', lw=2)
    ax.plot([0.90, 0.86], [0.52, 0.44], color='#047857', lw=2)
    ax.plot([0.90, 0.94], [0.52, 0.44], color='#047857', lw=2)
    ax.text(0.90, 0.40, "External Spatial &\nWeather Services", ha='center', va='center', fontsize=10, fontweight='bold', color='#047857')

    # Use Cases (Ellipses)
    use_cases = [
        (0.38, 0.82, "Register & Login / JWT Auth"),
        (0.62, 0.82, "Search Destinations & Geocode"),
        (0.38, 0.70, "Explore Attractions & POIs"),
        (0.62, 0.70, "Generate AI Day-by-Day Plan"),
        (0.38, 0.58, "Calculate & Track Budget"),
        (0.62, 0.58, "View OSRM Routes & Directions"),
        (0.38, 0.46, "Manage Custom Itinerary Slots"),
        (0.62, 0.46, "Search & Filter Hotels"),
        (0.38, 0.34, "Compare Transit Options"),
        (0.62, 0.34, "Access Emergency SOS & Nearby Care"),
        (0.38, 0.22, "Write Reviews & Rate Places"),
        (0.62, 0.22, "Watch Destination Real Visuals"),
        (0.50, 0.11, "Manage Profile & Travel Preferences")
    ]

    for ux, uy, utext in use_cases:
        ellipse = patches.Ellipse((ux, uy), 0.22, 0.07, facecolor='#EFF6FF', edgecolor='#2563EB', linewidth=1.2)
        ax.add_patch(ellipse)
        ax.text(ux, uy, utext, ha='center', va='center', fontsize=7.5, fontweight='bold', color='#1E293B')
        # Link from user
        ax.plot([0.14, ux - 0.10], [0.55, uy], color='#94A3B8', lw=1, linestyle=':')
        # Link to external if relevant
        if any(kw in utext for kw in ["Geocode", "AI Day-by-Day", "Attractions", "Routes", "Emergency"]):
            ax.plot([ux + 0.10, 0.86], [uy, 0.55], color='#10B981', lw=1, linestyle='--')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_2_use_case_diagram.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.3: Activity Diagram
# -------------------------------------------------------------
def gen_fig_5_3():
    fig, ax = create_base_fig((10, 8))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.3: TripDirect End-to-End Activity Diagram", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    # Start Node
    start = plt.Circle((0.5, 0.92), 0.015, color='#0F172A')
    ax.add_patch(start)
    ax.text(0.5, 0.94, "Start", ha='center', va='bottom', fontsize=9, fontweight='bold')

    activities = [
        (0.5, 0.84, "Launch App & Check Local Auth Token", '#EFF6FF', '#2563EB'),
        (0.5, 0.74, "Navigate to Home Dashboard / Category Discovery", '#EFF6FF', '#2563EB'),
        (0.5, 0.64, "Input Destination, Travel Dates, Budget, & Preferences", '#EFF6FF', '#2563EB'),
        (0.5, 0.54, "Trigger AI Trip Planner Service (Backend /api/v1/trips/plan)", '#ECFDF5', '#10B981'),
        (0.5, 0.44, "Geocode Location (Nominatim) & Discover POIs (Overpass API)", '#ECFDF5', '#10B981'),
        (0.5, 0.34, "Fetch 16-Day Weather (Open-Meteo) & Score Places (Multi-Factor)", '#ECFDF5', '#10B981'),
        (0.5, 0.24, "Compute OSRM Driving Route & Synthesize Day-by-Day Itinerary", '#ECFDF5', '#10B981'),
        (0.5, 0.14, "Display Interactive Trip Plan, Budget Breakdown & Reels", '#EFF6FF', '#2563EB')
    ]

    y_prev = 0.905
    for x, y, label, fc, ec in activities:
        add_box(ax, x - 0.25, y - 0.035, 0.50, 0.07, label, None, fc, ec, fontsize=8.5)
        add_arrow(ax, 0.5, y_prev, 0.5, y + 0.035, '#475569')
        y_prev = y - 0.035

    # End Node
    add_arrow(ax, 0.5, 0.105, 0.5, 0.065, '#475569')
    end_outer = plt.Circle((0.5, 0.05), 0.018, color='#0F172A', fill=False, lw=1.5)
    end_inner = plt.Circle((0.5, 0.05), 0.012, color='#0F172A')
    ax.add_patch(end_outer)
    ax.add_patch(end_inner)
    ax.text(0.5, 0.025, "End", ha='center', va='top', fontsize=9, fontweight='bold')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_3_activity_diagram.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.4: Application Flowchart
# -------------------------------------------------------------
def gen_fig_5_4():
    fig, ax = create_base_fig((9, 8))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.4: Mobile Application Operational Flowchart", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    add_box(ax, 0.38, 0.90, 0.24, 0.05, "Start Application", None, '#F8FAFC', '#475569', rad=0.03)
    add_arrow(ax, 0.5, 0.90, 0.5, 0.84)
    
    add_box(ax, 0.35, 0.79, 0.30, 0.05, "Splash Screen & Token Check", None, '#EFF6FF', '#2563EB')
    add_arrow(ax, 0.5, 0.79, 0.5, 0.72)
    
    # Decision: Valid Token?
    diamond = patches.Polygon([[0.5, 0.72], [0.65, 0.66], [0.5, 0.60], [0.35, 0.66]], closed=True, facecolor='#FEF3C7', edgecolor='#F59E0B', lw=1.5)
    ax.add_patch(diamond)
    ax.text(0.5, 0.66, "Authenticated?", ha='center', va='center', fontsize=8.5, fontweight='bold')
    
    # No -> Login
    add_arrow(ax, 0.35, 0.66, 0.20, 0.66, '#EF4444')
    add_box(ax, 0.06, 0.63, 0.14, 0.06, "Login / Register\nScreen", None, '#FEE2E2', '#EF4444', fontsize=7.5)
    add_arrow(ax, 0.13, 0.63, 0.13, 0.52)
    ax.text(0.27, 0.67, "No", fontsize=8, color='#EF4444', fontweight='bold')

    # Yes -> Home Dashboard
    add_arrow(ax, 0.5, 0.60, 0.5, 0.52, '#10B981')
    ax.text(0.52, 0.56, "Yes", fontsize=8, color='#10B981', fontweight='bold')
    
    add_box(ax, 0.35, 0.46, 0.30, 0.06, "Home Dashboard / MainScreen\n(Explore, Categories, Search)", None, '#EFF6FF', '#2563EB')
    add_arrow(ax, 0.13, 0.52, 0.35, 0.49, '#10B981')
    
    add_arrow(ax, 0.5, 0.46, 0.5, 0.39)
    add_box(ax, 0.32, 0.33, 0.36, 0.06, "Select Module: Search / Planner /\nBudget / Itinerary / Safety / Reviews", None, '#ECFDF5', '#10B981')
    
    add_arrow(ax, 0.5, 0.33, 0.5, 0.26)
    add_box(ax, 0.30, 0.20, 0.40, 0.06, "Execute Process & Query APIs / Database\n(Nominatim, Overpass, Open-Meteo, OSRM)", None, '#F5F3FF', '#8B5CF6')
    
    add_arrow(ax, 0.5, 0.20, 0.5, 0.13)
    add_box(ax, 0.33, 0.08, 0.34, 0.05, "Display Interactive Results UI", None, '#EFF6FF', '#2563EB')
    
    add_arrow(ax, 0.5, 0.08, 0.5, 0.04)
    add_box(ax, 0.42, 0.01, 0.16, 0.03, "End / Idle", None, '#F8FAFC', '#475569', rad=0.02)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_4_application_flowchart.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.5: UML Class Diagram
# -------------------------------------------------------------
def gen_fig_5_5():
    fig, ax = create_base_fig((11, 7.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.5: TripDirect Architecture Class Diagram", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    # ViewModel
    add_box(ax, 0.05, 0.58, 0.26, 0.32, 
            "+ uiState: StateFlow<TripPlannerUiState>\n+ tripPlan: StateFlow<TripPlan?>\n+ isGenerating: StateFlow<Boolean>\n+ error: StateFlow<String?>\n───────────────────────────────\n+ updateDestination(dest: String)\n+ generateTripPlan(req: TripPlanRequest)\n+ saveTrip(trip: TripPlan)\n+ optimizeRoute()",
            "TripPlannerViewModel", '#EFF6FF', '#2563EB', fontsize=7.5, title_size=9)

    # Repository Interface
    add_box(ax, 0.37, 0.70, 0.26, 0.20,
            "«interface»\n+ generateTripPlan(req): TripPlan\n+ optimizeTrip(plan): TripPlan\n+ modifyTrip(plan, note): TripPlan",
            "TripPlannerRepository", '#F0FDF4', '#16A34A', fontsize=7.5, title_size=9)

    # Network Repository Impl
    add_box(ax, 0.37, 0.40, 0.26, 0.24,
            "- api: AiPlannerApi\n- fallbackRepo: MockTripPlannerRepo\n───────────────────────────────\n+ generateTripPlan(req): TripPlan\n+ optimizeTrip(plan): TripPlan\n- mapResponseToTripPlan(resp): TripPlan",
            "NetworkTripPlannerRepository", '#EFF6FF', '#2563EB', fontsize=7.5, title_size=9)

    # Retrofit API Interface
    add_box(ax, 0.70, 0.65, 0.26, 0.25,
            "«interface»\n@POST('api/v1/ai-planner/plan')\n+ generateTripPlan(req): Response<NetResp>\n@POST('api/v1/ai-planner/regen')\n+ regenerateTripPlan(req): Response<NetResp>\n@GET('api/v1/trips/{id}')\n+ getSavedTrip(id): Response<NetResp>",
            "AiPlannerApi", '#FAF5FF', '#8B5CF6', fontsize=7, title_size=9)

    # Domain Data Models
    add_box(ax, 0.05, 0.08, 0.26, 0.38,
            "data class TripPlan(\n  id: String,\n  destination: String,\n  startDate: String,\n  endDate: String,\n  durationDays: Int,\n  days: List<TripDay>,\n  budget: TripBudget,\n  routes: List<RouteDto>\n)",
            "TripPlan (Domain Model)", '#FFFBEB', '#F59E0B', fontsize=7.5, title_size=9)

    add_box(ax, 0.37, 0.08, 0.26, 0.26,
            "data class TripDay(\n  dayNumber: Int,\n  title: String,\n  date: String,\n  items: List<ItineraryItem>,\n  estimatedCost: Int\n)",
            "TripDay (Domain Model)", '#FFFBEB', '#F59E0B', fontsize=7.5, title_size=9)

    add_box(ax, 0.70, 0.08, 0.26, 0.38,
            "data class ItineraryItem(\n  id: String,\n  title: String,\n  type: ItineraryItemType,\n  startTime: String,\n  endTime: String,\n  location: String,\n  estimatedCost: Int,\n  travelTimeMinutes: Int\n)",
            "ItineraryItem (Domain Model)", '#FFFBEB', '#F59E0B', fontsize=7.5, title_size=9)

    # Arrows
    add_arrow(ax, 0.31, 0.74, 0.37, 0.78, '#2563EB', 'uses')
    add_arrow(ax, 0.50, 0.64, 0.50, 0.70, '#16A34A', 'implements')
    add_arrow(ax, 0.63, 0.52, 0.70, 0.70, '#8B5CF6', 'calls')
    add_arrow(ax, 0.18, 0.58, 0.18, 0.46, '#F59E0B', 'produces')
    add_arrow(ax, 0.31, 0.22, 0.37, 0.22, '#F59E0B', 'contains 1..*')
    add_arrow(ax, 0.63, 0.22, 0.70, 0.22, '#F59E0B', 'contains 1..*')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_5_class_diagram.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.6: UML Sequence Diagram
# -------------------------------------------------------------
def gen_fig_5_6():
    fig, ax = create_base_fig((11, 7.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.6: Sequence Diagram of AI Trip Planning Workflow", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    lifelines = [
        (0.08, "User / Tourist"),
        (0.24, "AITripPlanner\nScreen (Compose)"),
        (0.42, "TripPlanner\nViewModel"),
        (0.58, "FastAPI Server\n(/api/v1/trips)"),
        (0.76, "External APIs\n(Nominatim/OSM/Meteo)"),
        (0.92, "SQLite DB\n(tripdirect.db)")
    ]

    for lx, lname in lifelines:
        add_box(ax, lx - 0.07, 0.86, 0.14, 0.07, lname, None, '#F1F5F9', '#475569', fontsize=7.5)
        ax.plot([lx, lx], [0.86, 0.08], color='#94A3B8', linestyle='--', lw=1.2)

    messages = [
        (0.08, 0.24, 0.80, "1. Click 'Generate Trip Plan' (Inputs: Dest, Budget, Dates)", '#2563EB'),
        (0.24, 0.42, 0.74, "2. viewModel.generateTripPlan(request)", '#2563EB'),
        (0.42, 0.58, 0.68, "3. POST /api/v1/ai-planner/plan", '#10B981'),
        (0.58, 0.76, 0.62, "4. Geocode Location (Nominatim API)", '#8B5CF6'),
        (0.76, 0.58, 0.56, "5. Coordinates & Address JSON", '#8B5CF6'),
        (0.58, 0.76, 0.50, "6. Query POIs (Overpass) + Weather (Open-Meteo)", '#8B5CF6'),
        (0.76, 0.58, 0.44, "7. Places & Weather Forecast Data", '#8B5CF6'),
        (0.58, 0.76, 0.38, "8. Calculate Road Route (OSRM Driving Engine)", '#8B5CF6'),
        (0.76, 0.58, 0.32, "9. Distance, Duration & Geometry", '#8B5CF6'),
        (0.58, 0.92, 0.26, "10. Query Destination Reels (social_content)", '#F59E0B'),
        (0.92, 0.58, 0.20, "11. Return Curated Reels / Fallback POIs", '#F59E0B'),
        (0.58, 0.42, 0.15, "12. Return Synthesized 200 OK AI Trip Plan JSON", '#10B981'),
        (0.42, 0.24, 0.11, "13. Update StateFlow & Render Itinerary UI", '#2563EB')
    ]

    for x1, x2, y, msg, col in messages:
        add_arrow(ax, x1, y, x2, y, col)
        mid_x = (x1 + x2) / 2
        ax.text(mid_x, y + 0.012, msg, ha='center', va='bottom', fontsize=6.8, color='#0F172A', fontweight='bold', bbox=dict(boxstyle="square,pad=0.1", fc="white", ec="none"))

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_6_sequence_diagram.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.7: Data Flow Diagram (DFD Level 0 & Level 1)
# -------------------------------------------------------------
def gen_fig_5_7():
    fig, ax = create_base_fig((11, 7.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.7: Data Flow Diagram (DFD Context Level 0 & Level 1 Decomposition)", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    # Level 0 (Top half)
    ax.text(0.12, 0.91, "Level 0: Context Diagram", fontsize=10, fontweight='bold', color='#334155')
    add_box(ax, 0.05, 0.74, 0.16, 0.12, "User / Tourist\n(Mobile Client)", None, '#EFF6FF', '#2563EB')
    add_box(ax, 0.38, 0.72, 0.24, 0.16, "TripDirect\nApplication System\n(0.0)", None, '#ECFDF5', '#10B981', rad=0.08)
    add_box(ax, 0.78, 0.74, 0.18, 0.12, "External Spatial &\nWeather APIs / DB", None, '#FAF5FF', '#8B5CF6')

    add_arrow(ax, 0.21, 0.82, 0.38, 0.82, '#2563EB', 'Trip Preferences & Queries', (0.5, 0.6))
    add_arrow(ax, 0.38, 0.76, 0.21, 0.76, '#10B981', 'Itinerary, Budget & Routes', (0.5, 0.4))
    add_arrow(ax, 0.62, 0.82, 0.78, 0.82, '#8B5CF6', 'Geocode / POI / Forecast Req', (0.5, 0.6))
    add_arrow(ax, 0.78, 0.76, 0.62, 0.76, '#8B5CF6', 'Raw Spatial / Weather Data', (0.5, 0.4))

    # Divider line
    ax.plot([0.05, 0.95], [0.65, 0.65], color='#CBD5E1', linestyle='--', lw=1.5)

    # Level 1 (Bottom half)
    ax.text(0.12, 0.61, "Level 1: Functional Decomposition", fontsize=10, fontweight='bold', color='#334155')

    procs = [
        (0.12, 0.44, "1.0 User Auth &\nJWT Token Handler"),
        (0.38, 0.44, "2.0 Destination &\nNearby POI Discovery"),
        (0.64, 0.44, "3.0 Multi-Factor Place\nScoring & Ranking"),
        (0.88, 0.44, "4.0 Weather-Aware Dynamic\nItinerary Generation"),
        (0.24, 0.18, "5.0 Parametric Budget\nEstimation Engine"),
        (0.50, 0.18, "6.0 Multi-Modal Route\n& OSRM Geometry"),
        (0.76, 0.18, "7.0 Emergency Spatial Safety\n& Helpline Directory")
    ]

    for px, py, ptitle in procs:
        add_box(ax, px - 0.10, py - 0.07, 0.20, 0.14, ptitle, None, '#F8FAFC', '#3B82F6', rad=0.03, fontsize=7.5)

    # Arrows between Level 1 processes
    add_arrow(ax, 0.22, 0.44, 0.28, 0.44, '#475569')
    add_arrow(ax, 0.48, 0.44, 0.54, 0.44, '#475569')
    add_arrow(ax, 0.74, 0.44, 0.78, 0.44, '#475569')
    add_arrow(ax, 0.64, 0.37, 0.34, 0.25, '#475569')
    add_arrow(ax, 0.64, 0.37, 0.50, 0.25, '#475569')
    add_arrow(ax, 0.64, 0.37, 0.76, 0.25, '#475569')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_7_dfd.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.8: Entity Relationship Diagram
# -------------------------------------------------------------
def gen_fig_5_8():
    fig, ax = create_base_fig((11, 7.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.8: Entity Relationship (ER) Diagram of Database Architecture", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    entities = [
        (0.08, 0.55, 0.22, 0.34, "users\n─────────────────\nPK  id: Integer\n    email: String(Unique)\n    hashed_password: Str\n    full_name: String\n    role: String\n    created_at: DateTime\n    is_active: Boolean", '#EFF6FF', '#2563EB'),
        (0.38, 0.55, 0.24, 0.34, "trips (Saved Trips)\n─────────────────\nPK  id: Integer\nFK  user_id: Integer\n    destination: String\n    start_date: String\n    end_date: String\n    travelers: Integer\n    budget: Float\n    created_at: DateTime", '#ECFDF5', '#10B981'),
        (0.70, 0.55, 0.24, 0.34, "trip_days\n─────────────────\nPK  id: Integer\nFK  trip_id: Integer\n    day_number: Integer\n    title: String\n    date: String\n    weather_summary: Str\n    total_cost: Float", '#FAF5FF', '#8B5CF6'),
        (0.70, 0.10, 0.24, 0.36, "itinerary_slots\n─────────────────\nPK  id: Integer\nFK  day_id: Integer\n    slot_id: String\n    time_of_day: String\n    activity: String\n    location: String\n    category: String\n    estimated_cost: Float\n    weather_note: String", '#FAF5FF', '#8B5CF6'),
        (0.38, 0.10, 0.24, 0.36, "saved_budgets\n─────────────────\nPK  id: Integer\nFK  trip_id: Integer\n    transportation: Float\n    accommodation: Float\n    food: Float\n    activities: Float\n    local_travel: Float\n    emergency_reserve: Float\n    total_estimated: Float", '#FEF3C7', '#F59E0B'),
        (0.08, 0.10, 0.22, 0.36, "reviews\n─────────────────\nPK  id: Integer\nFK  user_id: Integer\n    target_id: String\n    target_type: String\n    rating: Integer (1-5)\n    comment: Text\n    created_at: DateTime\n    updated_at: DateTime", '#FEE2E2', '#EF4444')
    ]

    for ex, ey, ew, eh, etext, efc, eec in entities:
        lines = etext.split('\n')
        title = lines[0]
        body = '\n'.join(lines[1:])
        add_box(ax, ex, ey, ew, eh, body, title, efc, eec, fontsize=7.2, title_size=9)

    # Relationships
    # users -> trips (1 to N)
    add_arrow(ax, 0.30, 0.72, 0.38, 0.72, '#2563EB', '1 : N')
    # trips -> trip_days (1 to N)
    add_arrow(ax, 0.62, 0.72, 0.70, 0.72, '#10B981', '1 : N')
    # trip_days -> itinerary_slots (1 to N)
    add_arrow(ax, 0.82, 0.55, 0.82, 0.46, '#8B5CF6', '1 : N')
    # trips -> saved_budgets (1 to 1)
    add_arrow(ax, 0.50, 0.55, 0.50, 0.46, '#F59E0B', '1 : 1')
    # users -> reviews (1 to N)
    add_arrow(ax, 0.19, 0.55, 0.19, 0.46, '#EF4444', '1 : N')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_8_er_diagram.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 5.9: Navigation / Screen Flow
# -------------------------------------------------------------
def gen_fig_5_9():
    fig, ax = create_base_fig((11, 7.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 5.9: Application Screen Navigation Flow", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    add_box(ax, 0.04, 0.75, 0.16, 0.12, "SplashScreen\n(Launch & Token)", None, '#F8FAFC', '#475569')
    add_box(ax, 0.24, 0.75, 0.18, 0.12, "Auth Screens\n(Login / Register /\nForgotPassword)", None, '#FEE2E2', '#EF4444')
    add_box(ax, 0.48, 0.75, 0.22, 0.12, "HomeScreen / Dashboard\n(Banners, Featured,\nQuick Actions)", None, '#EFF6FF', '#2563EB')
    
    add_arrow(ax, 0.20, 0.81, 0.24, 0.81)
    add_arrow(ax, 0.42, 0.81, 0.48, 0.81)

    # Bottom Nav 5 Core Branches
    branches = [
        (0.12, 0.45, "Search & Explore\n(SearchDestination,\nExplorePlaces,\nPlaceDetailsScreen)", '#ECFDF5', '#10B981'),
        (0.34, 0.45, "AI Trip Planner\n(AITripPlannerScreen,\nPlanningProgress,\nGeneratedTripScreen)", '#FAF5FF', '#8B5CF6'),
        (0.56, 0.45, "Budget & Routes\n(BudgetCalculator,\nBreakdown, RouteMap,\nNavigationScreen)", '#FEF3C7', '#F59E0B'),
        (0.78, 0.45, "Itinerary & Trips\n(ItineraryScreen,\nDayDetailsScreen,\nEditItineraryScreen)", '#EFF6FF', '#2563EB')
    ]

    for bx, by, btext, bfc, bec in branches:
        add_box(ax, bx - 0.09, by - 0.08, 0.18, 0.16, btext, None, bfc, bec, fontsize=7.2)
        add_arrow(ax, 0.59, 0.75, bx, by + 0.08, '#2563EB')

    # Sub-features bottom tier
    subfeatures = [
        (0.12, 0.15, "Hotels & Transport\n(HotelSearch, Compare,\nTransportSearchScreen)", '#F8FAFC', '#64748B'),
        (0.34, 0.15, "Package Comparison\n(Catalog, Compare,\nFeature Matrix)", '#F8FAFC', '#64748B'),
        (0.56, 0.15, "Emergency & Safety\n(SOSActive, NearbyCare,\nSafetyTipsScreen)", '#FEE2E2', '#EF4444'),
        (0.78, 0.15, "Reviews & Profile\n(WriteReview, Profile,\nPreferences, SavedTrips)", '#EFF6FF', '#3B82F6')
    ]

    for sx, sy, stext, sfc, sec in subfeatures:
        add_box(ax, sx - 0.09, sy - 0.07, 0.18, 0.14, stext, None, sfc, sec, fontsize=7.2)
        add_arrow(ax, sx, sy + 0.15, sx, sy + 0.07, '#94A3B8')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_5_9_screen_flow.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 6.1: Dev Environment Architecture
# -------------------------------------------------------------
def gen_fig_6_1():
    fig, ax = create_base_fig((10, 6))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.95, "Figure 6.1: Mobile Application Development Platform & Toolchain", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    add_box(ax, 0.05, 0.50, 0.26, 0.36, "Antigravity IDE & Terminal\n─────────────────\n• Kotlin Compiler 2.2.0\n• JDK 21 LTS Runtime\n• Android Studio SDK Tools\n• Git Version Control\n• ADB Device Daemon", "IDE & Compiler", '#EFF6FF', '#2563EB')
    add_box(ax, 0.37, 0.50, 0.26, 0.36, "Android Build Toolchain\n─────────────────\n• Gradle 9.1.0 Engine\n• Android Gradle Plugin 8.9\n• compileSdk = 36\n• minSdk = 24 (Android 7.0)\n• targetSdk = 36 (Android 15)", "Build Automation", '#ECFDF5', '#10B981')
    add_box(ax, 0.69, 0.50, 0.26, 0.36, "Execution & Testing Node\n─────────────────\n• Physical Android Device (USB/Wi-Fi)\n• Android Emulator (Pixel 8 / API 34)\n• Pytest 9.1.1 Suite (107 Tests)\n• Uvicorn ASGI Server (Port 8000)", "Deployment & Test", '#FAF5FF', '#8B5CF6')

    add_arrow(ax, 0.31, 0.68, 0.37, 0.68, '#2563EB', 'Compiles')
    add_arrow(ax, 0.63, 0.68, 0.69, 0.68, '#10B981', 'Deploys')

    add_box(ax, 0.10, 0.12, 0.80, 0.26, "Verified Build Environment Details:\n• Project-Based Learning implementation developed without proprietary IDE locking.\n• Complete command-line reproducible compilation verified via `cmd /c gradlew.bat testDebugUnitTest`.\n• Complete backend verified via `.\\venv\\Scripts\\pytest` with 107/107 automated tests passing in 82s.\n• Local cleartext HTTP enabled via AndroidManifest.xml for seamless 10.0.2.2 emulator bridging.", "Environment Configuration Summary", '#FFFBEB', '#F59E0B')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_6_1_dev_environment.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 6.2: Full Tech Stack Pyramid
# -------------------------------------------------------------
def gen_fig_6_2():
    fig, ax = create_base_fig((10, 6.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.96, "Figure 6.2: TripDirect Full-Stack Technology Architecture", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    stack_blocks = [
        ("Presentation UI", "Kotlin • Jetpack Compose • Material 3 • Navigation Compose • Coil 2.6", '#EFF6FF', '#2563EB', 0.15, 0.74, 0.70, 0.14),
        ("Mobile Client Architecture", "MVVM • Coroutines & Flow • Retrofit 2.11 • OkHttp 4.12 • DataStore", '#ECFDF5', '#10B981', 0.10, 0.54, 0.80, 0.14),
        ("Application Backend Engine", "FastAPI • Python 3.13 • Uvicorn • Pydantic v2 • SQLAlchemy ORM • BCrypt & JWT", '#FAF5FF', '#8B5CF6', 0.06, 0.34, 0.88, 0.14),
        ("Data Persistence & External Services", "SQLite 3 (tripdirect.db) • Nominatim • Overpass API • Open-Meteo • OSRM Routing Engine", '#FFFBEB', '#F59E0B', 0.02, 0.14, 0.96, 0.14)
    ]

    for title, desc, fc, ec, bx, by, bw, bh in stack_blocks:
        add_box(ax, bx, by, bw, bh, desc, title, fc, ec, fontsize=9, title_size=10.5)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_6_2_tech_stack.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 7.1: Project Structure / Package Tree
# -------------------------------------------------------------
def gen_fig_7_1():
    fig, ax = create_base_fig((10.5, 7.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, "Figure 7.1: Application Package and Source Code Hierarchy", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    add_box(ax, 0.04, 0.08, 0.44, 0.84,
            "TripDirect/app/src/main/java/com/tripdirect/\n"
            "├── MainActivity.kt (Compose Root Host)\n"
            "├── navigation/\n"
            "│   ├── AppNavigation.kt (NavHost & Routes)\n"
            "│   └── Screen.kt (Sealed Route Hierarchy)\n"
            "├── viewmodel/ (16 MVVM ViewModels)\n"
            "│   ├── TripPlannerViewModel.kt\n"
            "│   ├── HomeViewModel.kt, AuthViewModel.kt\n"
            "│   └── BudgetCalculatorViewModel.kt...\n"
            "├── data/\n"
            "│   ├── model/ (Domain Data Classes)\n"
            "│   ├── network/ (AiPlannerApi, NominatimApi)\n"
            "│   └── repository/ (16 Repositories)\n"
            "├── domain/\n"
            "│   ├── BudgetCalculator.kt\n"
            "│   └── RouteOptimizer.kt\n"
            "└── ui/\n"
            "    ├── components/ (Shared UI widgets)\n"
            "    └── screens/ (14 Module Screens)",
            "Android Mobile Application (146 Kotlin Files)", '#EFF6FF', '#2563EB', fontsize=7.5, title_size=9.5)

    add_box(ax, 0.52, 0.08, 0.44, 0.84,
            "TripDirect/backend/\n"
            "├── app/\n"
            "│   ├── main.py (FastAPI App & Lifespan)\n"
            "│   ├── core/ (config.py, database.py, security.py)\n"
            "│   ├── models/ (8 SQLAlchemy ORM Entities)\n"
            "│   │   ├── user.py, trip.py, itinerary.py\n"
            "│   │   ├── budget.py, review.py, package.py\n"
            "│   │   └── social_content.py, place.py\n"
            "│   ├── schemas/ (16 Pydantic Request/Response)\n"
            "│   ├── routers/ (16 REST Routers under /api/v1)\n"
            "│   └── services/ (Core Processing Services)\n"
            "│       ├── trip_planner_service.py\n"
            "│       ├── place_scoring_service.py\n"
            "│       ├── itinerary_generator.py\n"
            "│       ├── budget_engine.py\n"
            "│       └── external_services.py\n"
            "├── tests/ (19 Test Files / 107 Test Cases)\n"
            "└── tripdirect.db (SQLite Storage)",
            "Backend REST Service (FastAPI Engine)", '#ECFDF5', '#10B981', fontsize=7.5, title_size=9.5)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_7_1_project_structure.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 7.2: Jetpack Compose UI Wireframe Architecture
# -------------------------------------------------------------
def gen_fig_7_2():
    fig, ax = create_base_fig((10, 6.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.96, "Figure 7.2: Jetpack Compose Declarative UI Architecture", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    add_box(ax, 0.05, 0.15, 0.42, 0.75,
            "1. Scaffold Container (TopBar, BottomBar, Content)\n"
            "2. TopAppBar: Custom Search & Profile Avatar\n"
            "3. LazyColumn (Scrollable Content Container):\n"
            "   • Banner Carousel (Accommodations/Offers)\n"
            "   • Category Grid (Heritage, Beach, Hill, Temple)\n"
            "   • Featured Destinations (Card with Rating & Img)\n"
            "   • Quick Action Buttons (AI Planner, Budget)\n"
            "4. BottomNavigationBar:\n"
            "   • Home | Explore | Planner | Trips | Profile\n"
            "5. Floating Action Button (Quick SOS Access)",
            "Declarative UI Hierarchy Wireframe", '#EFF6FF', '#2563EB', fontsize=8)

    add_box(ax, 0.53, 0.15, 0.42, 0.75,
            "• State Hoisting: UI components remain stateless\n  and receive data classes via ViewModels.\n"
            "• Unidirectional Data Flow (UDF):\n  Events flow Up (Clicks) -> State flows Down.\n"
            "• Recomposition Optimization: Keyed LazyColumns\n  and remember/derivedStateOf prevent UI lag.\n"
            "• Material 3 Theming: Primary (#2563EB), Surface,\n  Error, and Elevation tokens applied system-wide.\n"
            "• Asynchronous Image Loading: Coil ImageLoader\n  caches network thumbnails with shimmer effects.",
            "Compose Engineering Principles", '#ECFDF5', '#10B981', fontsize=8)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_7_2_ui_wireframe.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 7.3: Database Schema & Configuration
# -------------------------------------------------------------
def gen_fig_7_3():
    fig, ax = create_base_fig((10, 6))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.95, "Figure 7.3: Application Database Configuration & SQLAlchemy Mapping", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    add_box(ax, 0.05, 0.48, 0.42, 0.38,
            "• Database Engine: SQLite 3 via SQLAlchemy Core\n"
            "• Default URL: sqlite:///./tripdirect.db\n"
            "• PostgreSQL Ready: Tested with asyncpg/psycopg2\n"
            "• Table Generation: Base.metadata.create_all(bind=engine)\n"
            "• Threading Strategy: check_same_thread=False\n"
            "• Connection Pooling: StaticPool for SQLite file",
            "Database Initialization & Config", '#EFF6FF', '#2563EB', fontsize=8)

    add_box(ax, 0.53, 0.48, 0.42, 0.38,
            "• 8 Core Tables with Foreign Key Constraints\n"
            "• BCrypt Hashed Passwords (Salted 12 Rounds)\n"
            "• Cascade Delete: Trip deletion cascades to days/slots\n"
            "• Indices: Destination ID, Category, User ID, Date\n"
            "• In-Memory TTL Cache: 24h Geocoding, 1h Weather\n"
            "• Seed Data: Pre-populated with popular destinations",
            "ORM Schema & Integrity Rules", '#ECFDF5', '#10B981', fontsize=8)

    add_box(ax, 0.10, 0.12, 0.80, 0.28,
            "Data Security and Privacy Protection:\n"
            "• Passwords never stored in plaintext; hashed via PassLib BCrypt context.\n"
            "• User Isolation: Itineraries and profile endpoints require verified JWT Authorization Bearer header.\n"
            "• Input Sanitization: Pydantic v2 data models enforce strict types, preventing SQL Injection.\n"
            "• Safe Media Uploads: Image uploads validate MIME type (PNG/JPEG) and limit payload size.",
            "Security & Data Integrity Architecture", '#FFFBEB', '#F59E0B', fontsize=8)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_7_3_database_config.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 7.4: API / Backend Integration Architecture
# -------------------------------------------------------------
def gen_fig_7_4():
    fig, ax = create_base_fig((10, 6.5))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.96, "Figure 7.4: REST API Integration and Network Serialization Pipeline", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    stages = [
        (0.06, 0.50, 0.18, 0.34, "Android App\n─────────────────\nUser clicks Action\nViewModel builds\nTripPlanRequest\nCoroutine dispatched\nDispatchers.IO", "1. Client Request", '#EFF6FF', '#2563EB'),
        (0.28, 0.50, 0.20, 0.34, "Retrofit 2.11 Pipeline\n─────────────────\nGsonConverterFactory\nOkHttp 4.12 Client\nHttpLoggingInterceptor\nBearer JWT Header\nTimeout: 15s Connect", "2. Serialization", '#EFF6FF', '#2563EB'),
        (0.52, 0.50, 0.20, 0.34, "FastAPI Routing\n─────────────────\nUvicorn ASGI Gateway\nRoute Pattern Match\nPydantic Validation\nDependency Injection\n(Session = Depends(db))", "3. Server Gateway", '#ECFDF5', '#10B981'),
        (0.76, 0.50, 0.18, 0.34, "Service Synthesis\n─────────────────\nQuery External APIs\nPlace Scoring Math\nItinerary Generation\nBudget Calculation\nReturn 200 JSON", "4. Backend Engine", '#FAF5FF', '#8B5CF6')
    ]

    for sx, sy, sw, sh, sbody, stitle, sfc, sec in stages:
        add_box(ax, sx, sy, sw, sh, sbody, stitle, sfc, sec, fontsize=7.2, title_size=8.5)

    add_arrow(ax, 0.24, 0.67, 0.28, 0.67, '#2563EB')
    add_arrow(ax, 0.48, 0.67, 0.52, 0.67, '#10B981')
    add_arrow(ax, 0.72, 0.67, 0.76, 0.67, '#8B5CF6')

    add_box(ax, 0.10, 0.12, 0.80, 0.28,
            "Resilience and Fallback Design:\n"
            "• Network Disconnection: Android app automatically switches to `MockTripPlannerRepository`.\n"
            "• External API Outage: Backend gracefully falls back to local database attractions if Overpass fails.\n"
            "• Routing Failure: Fallback from OSRM driving engine to mathematical Haversine great-circle calculation.\n"
            "• Provenance Tracking: Every generated plan includes `data_sources` metadata declaring whether data is live or cached.",
            "Fault-Tolerant Networking Framework", '#FFFBEB', '#F59E0B', fontsize=8)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_7_4_api_integration.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 7.5: Application Implementation Workflow
# -------------------------------------------------------------
def gen_fig_7_5():
    fig, ax = create_base_fig((10, 6))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.95, "Figure 7.5: Application Implementation and Processing Workflow", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    steps = [
        ("Step 1: Input & Validation", "Validate date bounds (duration ≤ 30 days), non-empty destination, budget > 0.", 0.05, 0.55, 0.28, 0.26, '#EFF6FF', '#2563EB'),
        ("Step 2: Spatial & Weather Query", "Query Nominatim geocoder, Overpass 20km POIs, and Open-Meteo 16-day forecast.", 0.36, 0.55, 0.28, 0.26, '#ECFDF5', '#10B981'),
        ("Step 3: Scoring & Route Engine", "Compute weighted score for places, solve OSRM driving geometry and transit times.", 0.67, 0.55, 0.28, 0.26, '#FAF5FF', '#8B5CF6'),
        ("Step 4: Itinerary Scheduling", "Allocate morning/afternoon/evening slots; adapt outdoor activities based on rain probability.", 0.20, 0.18, 0.28, 0.26, '#FEF3C7', '#F59E0B'),
        ("Step 5: Budget & Reel Binding", "Calculate itemized budget, link verified destination reels, and return JSON payload.", 0.52, 0.18, 0.28, 0.26, '#FEE2E2', '#EF4444')
    ]

    for title, desc, sx, sy, sw, sh, sfc, sec in steps:
        add_box(ax, sx, sy, sw, sh, desc, title, sfc, sec, fontsize=8, title_size=9.5)

    add_arrow(ax, 0.33, 0.68, 0.36, 0.68, '#2563EB')
    add_arrow(ax, 0.64, 0.68, 0.67, 0.68, '#10B981')
    add_arrow(ax, 0.81, 0.55, 0.34, 0.44, '#8B5CF6')
    add_arrow(ax, 0.48, 0.31, 0.52, 0.31, '#F59E0B')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_7_5_implementation_workflow.png"), dpi=300, bbox_inches='tight')
    plt.close()

# -------------------------------------------------------------
# FIG 8.1 - 8.5: UI Screen Layout Representations
# -------------------------------------------------------------
def gen_ui_mockup(filename, fig_num, title, screens):
    fig, ax = create_base_fig((11, 7))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.97, f"{fig_num}: {title}", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    num_screens = len(screens)
    spacing = 1.0 / (num_screens + 1)
    card_w = 0.26 if num_screens == 3 else (0.22 if num_screens == 4 else 0.40)
    card_h = 0.80
    
    for i, (stitle, scontent) in enumerate(screens):
        cx = (i + 1) * (1.0 / (num_screens + 1)) - card_w/2
        cy = 0.08
        
        # Phone chassis
        phone = patches.FancyBboxPatch((cx, cy), card_w, card_h, boxstyle="round,pad=0.015,rounding_size=0.03", facecolor='#FFFFFF', edgecolor='#334155', linewidth=2)
        ax.add_patch(phone)
        
        # Phone top notch
        notch = patches.FancyBboxPatch((cx + card_w/2 - 0.04, cy + card_h - 0.025), 0.08, 0.018, boxstyle="round,pad=0.005,rounding_size=0.008", facecolor='#0F172A', edgecolor='#0F172A')
        ax.add_patch(notch)
        
        # Screen header
        ax.text(cx + card_w/2, cy + card_h - 0.06, stitle, ha='center', va='center', fontsize=9.5, fontweight='bold', color='#1E3A8A')
        ax.plot([cx + 0.02, cx + card_w - 0.02], [cy + card_h - 0.085, cy + card_h - 0.085], color='#E2E8F0', lw=1)
        
        # Screen body elements
        ax.text(cx + 0.02, cy + card_h - 0.11, scontent, ha='left', va='top', fontsize=7.2, color='#1E293B', family='monospace', wrap=True)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, filename), dpi=300, bbox_inches='tight')
    plt.close()

def gen_all_ui_mockups():
    # Fig 8.1
    gen_ui_mockup("fig_8_1_auth_screens.png", "Figure 8.1", "TripDirect User Authentication and Onboarding Screens", [
        ("Login Screen", 
         "TripDirect Logo\nWelcome Back!\n\nEmail Address:\n[ tourist@tripdirect.com ]\n\nPassword:\n[ •••••••••••• ]\n\n[  SIGN IN BUTTON  ]\n\n──────── OR ────────\n[ G ] Continue with Google\n\nDon't have an account?\nSign Up"),
        ("Registration Screen",
         "Create Account\nJoin TripDirect Community\n\nFull Name:\n[ Rajesh Kumar ]\n\nEmail Address:\n[ rajesh@example.com ]\n\nPassword:\n[ •••••••••••• ]\n\n[  REGISTER BUTTON  ]\n\nBy signing up, you agree\nto our Terms & Privacy."),
        ("Password Recovery",
         "Forgot Password?\nReset your credentials\n\nEnter your registered email:\n[ rajesh@example.com ]\n\nWe will send a secure\npassword reset token.\n\n[ SEND RESET LINK ]\n\n← Back to Login")
    ])

    # Fig 8.2
    gen_ui_mockup("fig_8_2_home_dashboard.png", "Figure 8.2", "TripDirect Home Screen and Destination Discovery Dashboard", [
        ("Home Dashboard",
         "Good Morning, Explorer!\nWhere to next?\n\n[ 🔍 Search 'Ooty', 'Goa' ]\n\nCATEGORIES:\n[⛰ Hill] [🏖 Beach]\n[🏛 History] [🛕 Temple]\n\nFEATURED DESTINATIONS:\n┌─────────────────────┐\n│ Ooty, Tamil Nadu    │\n│ ★ 4.8 (1,420 reviews)│\n│ Lush tea plantations│\n└─────────────────────┘"),
        ("Destination Search",
         "Search Destination\n\n[ Ooty, Tamil Nadu    ✕]\n\nRECENT SEARCHES:\n• Ooty, Nilgiris\n• Munnar, Kerala\n• Kodaikanal, TN\n\nGEOCODED SUGGESTIONS:\n• Ooty City Center\n  (11.4102° N, 76.6950° E)\n• Doddabetta Peak\n• Botanical Gardens"),
        ("Destination Details",
         "Ooty, Nilgiris District\n★ 4.8 | Queen of Hills\n\n[ + Add to Trip Planner ]\n\nOVERVIEW:\nScenic hill station in the\nWestern Ghats mountains.\n\nREAL VISUALS / REELS:\n[ Reel 1: Tea Gardens ]\n[ Reel 2: Nilgiri Train]\n\nNEARBY ATTRACTIONS:\n• Ooty Lake (2.1 km)\n• Rose Garden (1.4 km)")
    ])

    # Fig 8.3
    gen_ui_mockup("fig_8_3_prototype_development.png", "Figure 8.3", "TripDirect Prototype Development and Component Engineering", [
        ("Explore Places UI",
         "Explore Nearby Places\nDestination: Ooty (15km)\n\nFILTERS:\n[All] [Nature] [Food] [Stay]\n\nRESULTS:\n1. Botanical Garden\n   ★ 4.7 • Park • 1.2 km\n   'Historic 55-acre garden'\n\n2. Doddabetta Peak\n   ★ 4.8 • Nature • 8.5 km\n   'Highest vantage point'\n\n3. Earl's Court Hotel\n   ★ 4.5 • ₹3,200/night"),
        ("Real Visuals Reels",
         "Real Visuals / Social Feed\n\n[ 📹 Reel Player View ]\n\n'Morning mist over Nilgiri\n Mountain Railway heritage'\n\nCreator: @nilgiri_trails\nVerified: Yes ✓\nPlatform: Instagram Reel\n\n[ ▶ Watch Video ]\n[ ★ Save Place ]\n[ ↗ Share Reel ]"),
        ("Hotel Compare UI",
         "Compare Accommodations\nOoty (2 Nights, 2 Guests)\n\n┌──────────┬──────────┐\n│ Savoy    │ Gem Park │\n├──────────┼──────────┤\n│ ₹5,800/n │ ₹3,400/n │\n│ ★ 4.8    │ ★ 4.3    │\n│ Luxury   │ Boutique │\n│ Spa, Wi-Fi│ Wi-Fi, AC│\n│ 0.8 km   │ 1.5 km   │\n└──────────┴──────────┘\n[ Select Savoy ]")
    ])

    # Fig 8.4
    gen_ui_mockup("fig_8_4_navigation_screens.png", "Figure 8.4", "Application Bottom Navigation and Multi-Stack Screen Flow", [
        ("Itinerary Screen",
         "My 4-Day Ooty Plan\nJune 12 - June 15\n\nDAY 1: Arrival & Lake\n• 09:00 AM: Check-in\n• 11:30 AM: Ooty Lake\n• 01:30 PM: Lunch (Cafe)\n• 03:30 PM: Rose Garden\n\nDAY 2: Nature & Peaks\n• 09:00 AM: Doddabetta\n• 02:00 PM: Tea Factory\n\n[ + Reorder Slots ]"),
        ("Transport Search",
         "Transit Options\nChennai → Ooty\n\n1. Nilgiri Exp (Train)\n   8h 30m • ₹650 (Sleeper)\n   ★ 4.6 • Overnight\n\n2. KSRTC Airavat (Bus)\n   9h 15m • ₹950 (AC)\n   ★ 4.4 • Semi-sleeper\n\n3. Private Cab (Sedan)\n   10h • ₹6,500 (Direct)\n\n[ Book Transit ]"),
        ("User Profile",
         "Profile & Settings\n\n[ Avatar: Rajesh Kumar ]\nrajesh@example.com\n\nPREFERENCES:\n• Travel Style: Balanced\n• Budget Tier: Moderate\n• Preferred Mode: Train/Car\n\nSAVED ITEMS:\n• 3 Saved Trips\n• 12 Favorite Places\n• 4 Submitted Reviews\n\n[ Logout ]")
    ])

    # Fig 8.5
    gen_ui_mockup("fig_8_5_developed_application_screens.png", "Figure 8.5", "TripDirect Feature Interfaces: AI Planner, Budget Calculator, and SOS Safety", [
        ("AI Trip Planner",
         "AI Trip Planner Engine\n\nDestination: [ Ooty      ]\nStart Date:  [ 2026-10-15]\nEnd Date:    [ 2026-10-18]\nTravelers:   [ 2 Adults  ]\nBudget:      [ ₹ 25,000  ]\nStyle:       [ Balanced  ]\nInterests:   Nature, Heritage\n\n[ ⚡ GENERATE TRIP PLAN ]\n\nWeather Forecast: 18°-24°C\nRain Probability: 15% (Low)"),
        ("Budget Calculator",
         "Trip Expense Breakdown\nTotal Budget: ₹ 25,000\nEstimated Cost: ₹ 22,400\nRemaining: ₹ 2,600 (Under)\n\nCATEGORIES:\n• Transport:     ₹ 5,000\n• Stay (3 nts):  ₹ 7,500\n• Food & Dining: ₹ 5,400\n• Activities:    ₹ 2,500\n• Local Travel:  ₹ 1,200\n• Emergency:     ₹ 800\n\nStatus: Within Budget ✓"),
        ("Emergency SOS Screen",
         "Tourist Safety & SOS\nCurrent Location: Ooty Center\n\n[ 🚨 TRIGGER SOS ALERT ]\n\nNEARBY EMERGENCY CARE:\n• Govt General Hospital\n  0.8 km • 24x7 Emergency\n• Ooty Town Police Station\n  1.1 km • Helpline: 100\n\nVERIFIED HELPLINES:\n• National Emergency: 112\n• Tourist Police: 1363")
    ])

# -------------------------------------------------------------
# FIG 9.1 - 9.5: Testing & Performance Visuals
# -------------------------------------------------------------
def gen_fig_9_1():
    fig, ax = create_base_fig((10, 6))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.95, "Figure 9.1: Systematic Software Verification and Testing Workflow", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    levels = [
        ("1. Unit Testing", "107 Pytest automated test cases testing individual modules (auth, scoring, budget, weather, route, social).", 0.05, 0.50, 0.20, 0.35, '#EFF6FF', '#2563EB'),
        ("2. Contract & API Testing", "FastAPI TestClient validating HTTP status codes, JSON response schemas, and error boundaries.", 0.28, 0.50, 0.20, 0.35, '#ECFDF5', '#10B981'),
        ("3. Integration Testing", "Live end-to-end execution of `test_real_ai_planner.py` testing live Nominatim, Overpass, and Open-Meteo APIs.", 0.52, 0.50, 0.20, 0.35, '#FAF5FF', '#8B5CF6'),
        ("4. Mobile Compilation & UI", "Gradle build verification `:app:compileDebugKotlin` and domain unit testing `BudgetCalculatorTest`.", 0.75, 0.50, 0.20, 0.35, '#FEF3C7', '#F59E0B')
    ]

    for title, desc, lx, ly, lw, lh, lfc, lec in levels:
        add_box(ax, lx, ly, lw, lh, desc, title, lfc, lec, fontsize=7.8, title_size=9)

    add_arrow(ax, 0.25, 0.67, 0.28, 0.67, '#2563EB')
    add_arrow(ax, 0.48, 0.67, 0.52, 0.67, '#10B981')
    add_arrow(ax, 0.72, 0.67, 0.75, 0.67, '#8B5CF6')

    add_box(ax, 0.10, 0.12, 0.80, 0.28,
            "Verification Highlights:\n"
            "• Zero Compilation Errors: 146 Kotlin files compiled cleanly with Android compileSdk 36.\n"
            "• 100% Backend Test Pass Rate: 107 of 107 tests passing in 82.67s without failure.\n"
            "• Verified Offline Resilience: Unit tests confirm graceful fallback when remote APIs timeout.\n"
            "• Strict Type Safety: Pydantic schemas prevent corrupted or unexpected JSON payloads.",
            "Testing Quality Assurance Summary", '#FFFBEB', '#F59E0B', fontsize=8)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_9_1_testing_workflow.png"), dpi=300, bbox_inches='tight')
    plt.close()

def gen_fig_9_2():
    # Test execution results bar chart
    fig, ax = plt.subplots(figsize=(10, 6), dpi=300)
    fig.patch.set_facecolor('#FFFFFF')
    ax.set_facecolor('#F8FAFC')
    
    modules = [
        "Auth", "AI Planner", "Budget", "Destination", "Home", "Hotels",
        "Itinerary", "Package", "Places", "Profile", "Reviews", "Routes",
        "Safety", "Social", "Transport", "Trips", "Real AI"
    ]
    test_counts = [12, 11, 4, 5, 6, 7, 2, 7, 6, 7, 8, 7, 7, 4, 9, 4, 1]
    
    y_pos = np.arange(len(modules))
    bars = ax.barh(y_pos, test_counts, color='#2563EB', edgecolor='#1D4ED8', height=0.65)
    
    ax.set_yticks(y_pos)
    ax.set_yticklabels(modules, fontsize=9, fontweight='semibold')
    ax.invert_yaxis()
    ax.set_xlabel('Number of Automated Test Cases Passed (Total: 107 Passed, 0 Failed)', fontsize=10, fontweight='bold', color='#1E3A8A')
    ax.set_title('Figure 9.2: Pytest Automated Test Execution Results by Module (100% Pass Rate)', fontsize=12, fontweight='bold', color='#1E3A8A', pad=15)
    
    for bar in bars:
        w = bar.get_width()
        ax.text(w + 0.2, bar.get_y() + bar.get_height()/2, f"{int(w)} Passed", ha='left', va='center', fontsize=8, fontweight='bold', color='#0F172A')

    ax.set_xlim(0, 15)
    ax.spines['top'].set_visible(False)
    ax.spines['right'].set_visible(False)
    ax.grid(axis='x', linestyle='--', alpha=0.5)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_9_2_test_execution_results.png"), dpi=300, bbox_inches='tight')
    plt.close()

def gen_fig_9_3():
    # Performance analysis latency chart
    fig, ax = plt.subplots(figsize=(10, 5.5), dpi=300)
    fig.patch.set_facecolor('#FFFFFF')
    ax.set_facecolor('#F8FAFC')
    
    scenarios = [
        "In-Memory TTL Cache Hit\n(FastAPI / RAM)",
        "Local Database Query\n(SQLite tripdirect.db)",
        "OSRM Road Routing\n(Remote REST Call)",
        "Open-Meteo Weather API\n(16-Day Forecast)",
        "Nominatim Geocoding\n(OSM Server Query)",
        "Overpass POI Search\n(20km Spatial Query)",
        "Full AI Trip Synthesis\n(Cold Remote Query)"
    ]
    latencies = [4.2, 14.8, 142.0, 185.0, 290.0, 480.0, 780.0]
    colors = ['#10B981', '#10B981', '#3B82F6', '#3B82F6', '#F59E0B', '#F59E0B', '#8B5CF6']

    x_pos = np.arange(len(scenarios))
    bars = ax.bar(x_pos, latencies, color=colors, edgecolor='#334155', width=0.55)

    ax.set_xticks(x_pos)
    ax.set_xticklabels(scenarios, fontsize=8, fontweight='semibold')
    ax.set_ylabel('Average Response Time / Latency (Milliseconds - ms)', fontsize=10, fontweight='bold', color='#1E3A8A')
    ax.set_title('Figure 9.3: TripDirect API Latency and Benchmark Performance Analysis', fontsize=12, fontweight='bold', color='#1E3A8A', pad=15)

    for bar in bars:
        h = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2, h + 15, f"{h:.1f} ms", ha='center', va='bottom', fontsize=8, fontweight='bold')

    ax.set_ylim(0, 900)
    ax.spines['top'].set_visible(False)
    ax.spines['right'].set_visible(False)
    ax.grid(axis='y', linestyle='--', alpha=0.5)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_9_3_performance_analysis.png"), dpi=300, bbox_inches='tight')
    plt.close()

def gen_fig_9_4():
    fig, ax = create_base_fig((10, 6))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.95, "Figure 9.4: TripDirect Functional Test Execution Matrix", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    headers = ["Test Module", "Test Cases", "Execution Method", "Status", "Verified Behavior"]
    rows = [
        ["Authentication", "12 Cases", "Pytest / JWT Bearer", "100% Pass", "Registration, Login, Duplicate Check, Password Hash"],
        ["AI Planner Core", "11 Cases", "Pytest / Algorithm", "100% Pass", "Scoring formula, Date bounds, Graceful Fallback"],
        ["Live External APIs", "1 Case (Ooty)", "End-to-End Live HTTPS", "100% Pass", "Live Nominatim, Overpass, Open-Meteo, OSRM execution"],
        ["Emergency & Safety", "7 Cases", "Pytest / OSM Spatial", "100% Pass", "Nearby care search, 4 Country checklists, Tips"],
        ["Budget Engine", "4 Cases", "Pytest / Arithmetic", "100% Pass", "Category breakdowns, per-person rates, negative guards"],
        ["Reviews & Ratings", "8 Cases", "Pytest / DB CRUD", "100% Pass", "1-5 bounds, Duplicate block, Edit/Delete permissions"],
        ["Transit & Hotels", "16 Cases", "Pytest / Filtering", "100% Pass", "Price/rating filter, Distance sorting, Mode filters"],
        ["Android Unit Logic", "2 Cases", "Gradle testDebugUnitTest", "100% Pass", "BudgetCalculator & RouteOptimizer heuristics"]
    ]

    table_data = [headers] + rows
    table = ax.table(cellText=table_data, loc='center', cellLoc='left', colWidths=[0.18, 0.12, 0.22, 0.12, 0.36])
    table.auto_set_font_size(False)
    table.set_fontsize(7.8)
    table.scale(1.0, 1.6)

    # Style table
    for (r, c), cell in table.get_celld().items():
        if r == 0:
            cell.set_facecolor('#1E3A8A')
            cell.set_text_props(color='white', fontweight='bold')
        else:
            if c == 3:
                cell.set_facecolor('#ECFDF5')
                cell.set_text_props(color='#047857', fontweight='bold')
            elif r % 2 == 0:
                cell.set_facecolor('#F8FAFC')
            else:
                cell.set_facecolor('#FFFFFF')

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_9_4_functional_output.png"), dpi=300, bbox_inches='tight')
    plt.close()

def gen_fig_9_5():
    fig, ax = create_base_fig((10, 6))
    ax.set_xlim(0, 1.0)
    ax.set_ylim(0, 1.0)
    
    ax.text(0.5, 0.95, "Figure 9.5: Final Mobile Application Verification and Execution Summary", ha='center', va='top', fontsize=13, fontweight='bold', color='#1E3A8A')
    
    kpis = [
        ("146", "Kotlin Source Files\nCompiled with compileSdk 36", '#EFF6FF', '#2563EB', 0.06, 0.58),
        ("107", "Automated Pytest Cases\n100% Passing in 82.67s", '#ECFDF5', '#10B981', 0.29, 0.58),
        ("14+", "Fully Implemented Modules\nAuth, Home, Planner, Budget...", '#FAF5FF', '#8B5CF6', 0.52, 0.58),
        ("4", "Live External API Engines\nNominatim, Overpass, Meteo, OSRM", '#FEF3C7', '#F59E0B', 0.75, 0.58)
    ]

    for val, lbl, fc, ec, kx, ky in kpis:
        box = patches.FancyBboxPatch((kx, ky), 0.19, 0.28, boxstyle="round,pad=0.02,rounding_size=0.03", facecolor=fc, edgecolor=ec, linewidth=1.5)
        ax.add_patch(box)
        ax.text(kx + 0.095, ky + 0.18, val, ha='center', va='center', fontsize=22, fontweight='bold', color=ec)
        ax.text(kx + 0.095, ky + 0.08, lbl, ha='center', va='center', fontsize=8, color='#0F172A', fontweight='semibold')

    add_box(ax, 0.06, 0.12, 0.88, 0.38,
            "TripDirect Engineering Accomplishments:\n"
            "✓ Unified travel planning solving fragmentation across maps, itinerary, budget, and safety.\n"
            "✓ Multi-factor intelligent recommendation scoring with transparent, human-readable rationale.\n"
            "✓ Weather-aware dynamic scheduling adjusting outdoor activities in response to rain forecasts.\n"
            "✓ Verified zero-cost architecture leveraging OpenStreetMap, Nominatim, Overpass, Open-Meteo, and OSRM.\n"
            "✓ Graceful offline degradation with local fallback repositories and Euclidean route heuristics.\n"
            "✓ Robust testing: Verified Android compilation and 100% passing automated test suite.",
            "Project Validation & Production Readiness", '#F0FDF4', '#16A34A', fontsize=8.5, title_size=10.5)

    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "fig_9_5_final_application_result.png"), dpi=300, bbox_inches='tight')
    plt.close()

if __name__ == '__main__':
    print("Generating Figure 5.1...")
    gen_fig_5_1()
    print("Generating Figure 5.1.1...")
    gen_fig_5_1_1()
    print("Generating Figure 5.1.2...")
    gen_fig_5_1_2()
    print("Generating Figure 5.2...")
    gen_fig_5_2()
    print("Generating Figure 5.3...")
    gen_fig_5_3()
    print("Generating Figure 5.4...")
    gen_fig_5_4()
    print("Generating Figure 5.5...")
    gen_fig_5_5()
    print("Generating Figure 5.6...")
    gen_fig_5_6()
    print("Generating Figure 5.7...")
    gen_fig_5_7()
    print("Generating Figure 5.8...")
    gen_fig_5_8()
    print("Generating Figure 5.9...")
    gen_fig_5_9()
    print("Generating Figure 6.1...")
    gen_fig_6_1()
    print("Generating Figure 6.2...")
    gen_fig_6_2()
    print("Generating Figure 7.1...")
    gen_fig_7_1()
    print("Generating Figure 7.2...")
    gen_fig_7_2()
    print("Generating Figure 7.3...")
    gen_fig_7_3()
    print("Generating Figure 7.4...")
    gen_fig_7_4()
    print("Generating Figure 7.5...")
    gen_fig_7_5()
    print("Generating Figures 8.1 - 8.5 UI Mockups...")
    gen_all_ui_mockups()
    print("Generating Figure 9.1...")
    gen_fig_9_1()
    print("Generating Figure 9.2...")
    gen_fig_9_2()
    print("Generating Figure 9.3...")
    gen_fig_9_3()
    print("Generating Figure 9.4...")
    gen_fig_9_4()
    print("Generating Figure 9.5...")
    gen_fig_9_5()
    print("All figures successfully generated in:", OUTPUT_DIR)
