# TripDirect Backend API

FastAPI REST backend service powering the **TripDirect** travel application.

## System Architecture

```text
TripDirect Android Application (Kotlin / Jetpack Compose)
                     │
                     ▼ REST API (JSON / HTTP)
FastAPI Backend (Python 3.10+ ASGI Engine)
       │                        │
       ▼ SQLAlchemy ORM         ▼ External Integrations
PostgreSQL / SQLite Database   OpenStreetMap Overpass API / Open-Meteo Weather API
```

---

## Features & Modules

The backend exposes a modular REST API structured under `/api/v1`:

| Module | Base Path | Key Functionality |
| :--- | :--- | :--- |
| **Authentication** | `/api/v1/auth` | User Registration, JWT Login, Profile & Token Refresh |
| **Home** | `/api/v1/home` | Home Dashboard, Banners, Featured Destinations & Categories |
| **Destinations** | `/api/v1/destinations` | Destination Discovery, Detailed Profiles, Filtering & Pagination |
| **Places** | `/api/v1/places` | Category-based Place Search (Attractions, Dining, Shopping, Heritage) |
| **Hotels** | `/api/v1/hotels` | Hotel Search, Rating & Price Filtering, Amenities & Availability |
| **Transport** | `/api/v1/transport` | Transit Modes (Flight, Train, Bus, Cab), Schedules, Fares & Booking |
| **AI Trip Planner** | `/api/v1/trips` | AI Plan Generation, Custom Itinerary Recommendations & Saved Trips |
| **Budget Calculator**| `/api/v1/budget` | Trip Expense Calculations, Currency Breakdown & Expense Tracking |
| **Route Map** | `/api/v1/routes` | Navigation Route Calculation, Distance & Multi-modal Directions |
| **Itinerary** | `/api/v1/itineraries` | Daily Activity Scheduling, Drag-and-drop Reordering & Time Slots |
| **Package Comparison**| `/api/v1/packages` | Tour Package Search, Side-by-Side Comparison & Feature Matrix |
| **Reviews** | `/api/v1/reviews` | Rating (1-5), Review Submissions, Aggregate Averages & Counts |
| **Emergency / Safety**| `/api/v1/safety` | OSM Spatial Service Discovery, Verified Emergency Numbers & Checklists |
| **Profile** | `/api/v1/profile` | Profile Management, Preferences, & Safe Avatar Image Uploads |

---

## Installation & Environment Setup

### 1. Prerequisites
- **Python 3.10+**
- **pip** package manager
- **PostgreSQL 14+** (Optional for production; SQLite is supported by default out-of-the-box)

### 2. Create Virtual Environment

```bash
cd TripDirect/backend
python -m venv venv
```

Activate the virtual environment:
- **Windows (PowerShell):**
  ```powershell
  .\venv\Scripts\Activate.ps1
  ```
- **Linux / macOS:**
  ```bash
  source venv/bin/activate
  ```

### 3. Install Dependencies

```bash
pip install -r requirements.txt
```

### 4. Environment Variables Setup

Create a `.env` file from `.env.example`:

```bash
cp .env.example .env
```

Sample `.env` configuration:

```env
PROJECT_NAME="TripDirect Backend API"
API_V1_STR="/api/v1"
SECRET_KEY="your-super-secret-key-change-in-production"
JWT_SECRET_KEY="your-jwt-secret-key-change-in-production"
JWT_ALGORITHM="HS256"
ACCESS_TOKEN_EXPIRE_MINUTES=1440

# Database Configuration
# SQLite Default:
DATABASE_URL="sqlite:///./tripdirect.db"

# PostgreSQL Configuration Example:
# DATABASE_URL="postgresql://postgres:postgres@localhost:5432/tripdirect_db"
```

---

## Database Setup & Migration Instructions

### Automatic Table Initialization
Tables are automatically created on startup via SQLAlchemy `Base.metadata.create_all(bind=engine)`.

### PostgreSQL Migration Setup
To switch to PostgreSQL:
1. Ensure PostgreSQL server is running and database `tripdirect_db` is created:
   ```sql
   CREATE DATABASE tripdirect_db;
   ```
2. Update `DATABASE_URL` in `.env`:
   ```env
   DATABASE_URL="postgresql://username:password@localhost:5432/tripdirect_db"
   ```
3. Run the backend server or test suite. All tables will automatically populate.

---

## Running the Backend Server

Start the FastAPI application using Uvicorn:

```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### Service Health Check
Verify backend health by calling:

```bash
curl http://127.0.0.1:8000/api/v1/health
```

Expected Response:
```json
{
  "status": "ok",
  "service": "TripDirect Backend"
}
```

---

## Interactive API Documentation

- **Swagger UI:** `http://127.0.0.1:8000/docs`
- **ReDoc:** `http://127.0.0.1:8000/redoc`
- **OpenAPI Spec:** `http://127.0.0.1:8000/api/v1/openapi.json`

---

## Android Connection Instructions

When connecting to the local backend server from an **Android Emulator**:
- Use `http://10.0.2.2:8000/api/v1/` as your Base URL (`10.0.2.2` maps to `127.0.0.1` on the host machine).

When connecting from a **Physical Android Device**:
- Connect the Android device to the same local Wi-Fi network as your host machine.
- Find your host machine's local IP (e.g. `192.168.1.105`).
- Use Base URL: `http://192.168.1.105:8000/api/v1/`.

### Android Cleartext Traffic Configuration
Ensure your `AndroidManifest.xml` includes `android:usesCleartextTraffic="true"` or a network security configuration permitting HTTP requests during local development.

---

## Running Automated Backend Tests

To execute the complete backend test suite (88 automated test cases covering all 14 modules):

```bash
.\venv\Scripts\pytest
```
