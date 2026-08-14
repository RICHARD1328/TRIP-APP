package com.tripdirect.ui.screens.trips

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onNavigateToHotels: () -> Unit = {},
    onNavigateToTransport: () -> Unit = {},
    onNavigateToPlaces: () -> Unit = {},
    onNavigateToBudget: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToItinerary: () -> Unit = {},
    onNavigateToComparison: () -> Unit = {},
    onNavigateToSafety: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToExplorePlace: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // Sample Sample Data initialization matching user specifications
    val sampleHotel = HotelItem(
        id = "h1",
        name = "Ooty View Resort",
        destination = "Ooty",
        imageUrl = TripImages.RESORT_HOTEL,
        rating = 4.3,
        pricePerNight = 1500,
        distance = "2.4 km from Ooty Lake",
        facilities = listOf("Breakfast", "Wi-Fi", "Parking")
    )

    val sampleAttractions = listOf(
        AttractionItem("a1", "Ooty Lake", "Ooty", TripImages.OOTY_LAKE, 4.5, 50, "2.1 km", "9 AM - 6 PM"),
        AttractionItem("a2", "Tea Gardens", "Ooty", TripImages.TEA_GARDEN, 4.7, 0, "5.2 km", "8 AM - 5 PM"),
        AttractionItem("a3", "Doddabetta Peak", "Ooty", TripImages.DODDABETTA, 4.6, 20, "8.4 km", "9 AM - 5:30 PM")
    )

    val sampleRestaurants = listOf(
        RestaurantItem("r1", "Earl's Secret", 4.4, "₹₹", "1.3 km", "South Indian • Cafe", TripImages.RESTAURANT_FOOD),
        RestaurantItem("r2", "Hyderabadi Biryani House", 4.2, "₹", "2.0 km", "Indian • Mughlai", TripImages.SOUTH_INDIAN_FOOD)
    )

    val sampleExpenses = listOf(
        ExpenseItem("Transportation", 2500, "🚌"),
        ExpenseItem("Hotel", 3000, "🏨"),
        ExpenseItem("Food", 1500, "🍴"),
        ExpenseItem("Activities", 500, "🎟️"),
        ExpenseItem("Local Transport", 1000, "🛺")
    )

    val sampleItinerary = listOf(
        ItineraryDayItem(1, "Arrival & Sightseeing", listOf("🚗 Chennai → Ooty", "🏨 Hotel Check-in", "📍 Ooty Lake", "🍴 Local Dinner")),
        ItineraryDayItem(2, "Nature & Peaks", listOf("🌿 Botanical Garden", "🍵 Tea Museum", "⛰️ Doddabetta Peak")),
        ItineraryDayItem(3, "Coonoor & Departure", listOf("🌄 Coonoor Sightseeing", "🚗 Return to Chennai"))
    )

    val samplePacking = remember {
        mutableStateListOf(
            PackingItem("p1", "ID Proof", true),
            PackingItem("p2", "Clothes", true),
            PackingItem("p3", "Medicines", false),
            PackingItem("p4", "Charger", false),
            PackingItem("p5", "Power Bank", true),
            PackingItem("p6", "Camera", true)
        )
    }

    val sampleDocs = remember {
        mutableStateListOf(
            TravelDocumentItem("d1", "Train Ticket", "Tickets"),
            TravelDocumentItem("d2", "Resort Confirmation", "Hotel Reservation"),
            TravelDocumentItem("d3", "Aadhaar / Passport", "ID Proof"),
            TravelDocumentItem("d4", "Travel Cover Policy", "Travel Insurance")
        )
    }

    var savedPlaces by remember {
        mutableStateOf(
            listOf(
                "Ooty Lake" to true,
                "Tea Gardens" to true,
                "Coonoor" to true
            )
        )
    }

    val ootyTrip = FullTrip(
        id = "t1",
        destination = "Ooty",
        startLocation = "Chennai",
        startDate = "Aug 20",
        endDate = "Aug 23",
        travelers = 2,
        budget = 10000,
        estimatedCost = 8500,
        status = "UPCOMING",
        imageUrl = TripImages.OOTY,
        packageCost = 12000,
        planningCompletedPercentage = 80,
        daysUntilStart = 9,
        hotel = sampleHotel,
        attractions = sampleAttractions,
        restaurants = sampleRestaurants,
        itinerary = sampleItinerary,
        expenses = sampleExpenses,
        routeSteps = listOf("Chennai", "Ooty", "Hotel", "Ooty Lake", "Doddabetta", "Coonoor"),
        packingChecklist = samplePacking,
        documents = sampleDocs
    )

    val kodaikanalTrip = FullTrip(
        id = "t2",
        destination = "Kodaikanal",
        startLocation = "Chennai",
        startDate = "Sep 15",
        endDate = "Sep 18",
        travelers = 2,
        budget = 11000,
        estimatedCost = 9200,
        status = "UPCOMING",
        imageUrl = TripImages.KODAIKANAL,
        packageCost = 13500,
        planningCompletedPercentage = 60,
        daysUntilStart = 35
    )

    val pondicherryTrip = FullTrip(
        id = "t3",
        destination = "Pondicherry",
        startLocation = "Chennai",
        startDate = "Jul 10",
        endDate = "Jul 12",
        travelers = 1,
        budget = 6000,
        estimatedCost = 4800,
        status = "COMPLETED",
        imageUrl = TripImages.PONDICHERRY
    )

    val munnarTrip = FullTrip(
        id = "t4",
        destination = "Munnar",
        startLocation = "Chennai",
        startDate = "Oct 05",
        endDate = "Oct 08",
        travelers = 2,
        budget = 12000,
        estimatedCost = 9800,
        status = "UPCOMING",
        imageUrl = TripImages.MUNNAR
    )

    val goaTrip = FullTrip(
        id = "t5",
        destination = "Goa",
        startLocation = "Chennai",
        startDate = "Nov 12",
        endDate = "Nov 16",
        travelers = 4,
        budget = 18000,
        estimatedCost = 14500,
        status = "UPCOMING",
        imageUrl = DestinationImageProvider.GOA
    )

    val jaipurTrip = FullTrip(
        id = "t6",
        destination = "Jaipur, Rajasthan",
        startLocation = "Delhi",
        startDate = "Dec 01",
        endDate = "Dec 04",
        travelers = 2,
        budget = 16000,
        estimatedCost = 13200,
        status = "UPCOMING",
        imageUrl = DestinationImageProvider.JAIPUR
    )

    val agraTrip = FullTrip(
        id = "t7",
        destination = "Agra",
        startLocation = "Delhi",
        startDate = "Dec 05",
        endDate = "Dec 06",
        travelers = 2,
        budget = 7000,
        estimatedCost = 5500,
        status = "UPCOMING",
        imageUrl = DestinationImageProvider.AGRA
    )

    val manaliTrip = FullTrip(
        id = "t8",
        destination = "Manali",
        startLocation = "Chandigarh",
        startDate = "Jan 10",
        endDate = "Jan 15",
        travelers = 2,
        budget = 22000,
        estimatedCost = 18000,
        status = "UPCOMING",
        imageUrl = DestinationImageProvider.MANALI
    )

    val coorgTrip = FullTrip(
        id = "t9",
        destination = "Coorg, Karnataka",
        startLocation = "Bengaluru",
        startDate = "Feb 14",
        endDate = "Feb 16",
        travelers = 2,
        budget = 10000,
        estimatedCost = 8200,
        status = "UPCOMING",
        imageUrl = DestinationImageProvider.COORG
    )

    val alleppeyTrip = FullTrip(
        id = "t10",
        destination = "Alleppey",
        startLocation = "Kochi",
        startDate = "Mar 01",
        endDate = "Mar 03",
        travelers = 2,
        budget = 14000,
        estimatedCost = 11500,
        status = "COMPLETED",
        imageUrl = DestinationImageProvider.ALLEPPEY
    )

    val tripsList = remember { mutableStateListOf(ootyTrip, kodaikanalTrip, pondicherryTrip, munnarTrip, goaTrip, jaipurTrip, agraTrip, manaliTrip, coorgTrip, alleppeyTrip) }
    var selectedTab by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog States
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddDocDialog by remember { mutableStateOf(false) }
    var selectedTripForAction by remember { mutableStateOf<FullTrip?>(null) }

    // Add document text states
    var newDocTitle by remember { mutableStateOf("") }
    var newDocType by remember { mutableStateOf("ID Proof") }

    val filteredTrips = tripsList.filter { trip ->
        val matchesTab = if (selectedTab == "All") true else trip.status.equals(selectedTab, ignoreCase = true)
        val matchesSearch = if (searchQuery.isBlank()) true else {
            trip.destination.contains(searchQuery, ignoreCase = true) ||
            trip.startLocation.contains(searchQuery, ignoreCase = true) ||
            (trip.hotel?.name?.contains(searchQuery, ignoreCase = true) ?: false)
        }
        matchesTab && matchesSearch
    }

    val upcomingNextTrip = tripsList.firstOrNull { it.status == "UPCOMING" }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MY TRIPS",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Plan, manage and enjoy your journeys",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onNavigateToPlanner,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Trip", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search your trips (Destination, City, Hotel)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section Filter Chips: All, Upcoming, Ongoing, Completed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Upcoming", "Ongoing", "Completed").forEach { tab ->
                        FilterChip(
                            selected = selectedTab.equals(tab, ignoreCase = true),
                            onClick = { selectedTab = tab },
                            label = {
                                val count = if (tab == "All") tripsList.size else tripsList.count { it.status.equals(tab, ignoreCase = true) }
                                Text("$tab ($count)", fontWeight = FontWeight.Bold)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (filteredTrips.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues)) {
                TripsEmptyState(onPlanMyTrip = onNavigateToPlanner)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Featured Next Adventure Card (Top of Trips screen)
                if (upcomingNextTrip != null && searchQuery.isBlank() && selectedTab != "Completed") {
                    item {
                        FeaturedNextAdventureCard(
                            trip = upcomingNextTrip,
                            onViewItinerary = onNavigateToItinerary
                        )
                    }
                }

                // Vertical List of Trip Cards
                item {
                    Text(
                        text = "MY TRIPS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(filteredTrips) { trip ->
                    TripHeroCard(
                        trip = trip,
                        onViewTrip = onNavigateToItinerary,
                        onEditTrip = {
                            selectedTripForAction = trip
                            showEditDialog = true
                        },
                        onDeleteTrip = {
                            selectedTripForAction = trip
                            showDeleteDialog = true
                        }
                    )
                }

                // If upcoming trip exists, show detailed controls for the active trip (first in filtered)
                val activeTrip = filteredTrips.firstOrNull()
                if (activeTrip != null) {
                    // 6. Trip Progress & 7. Countdown
                    item {
                        TripProgressCard(trip = activeTrip)
                    }

                    // 8. Quick Actions
                    item {
                        val quickActions = listOf(
                            QuickActionItem("Hotels", "🏨", onNavigateToHotels),
                            QuickActionItem("Transport", "🚌", onNavigateToTransport),
                            QuickActionItem("Places", "📍", onNavigateToPlaces),
                            QuickActionItem("Budget", "💰", onNavigateToBudget),
                            QuickActionItem("Map", "🗺️", onNavigateToMap),
                            QuickActionItem("Itinerary", "📅", onNavigateToItinerary)
                        )
                        QuickActionsSection(actions = quickActions)
                    }

                    // 9. Budget Summary
                    item {
                        BudgetSummaryCard(
                            totalBudget = activeTrip.budget,
                            estimatedCost = activeTrip.estimatedCost,
                            packageCost = activeTrip.packageCost
                        )
                    }

                    // 10. Expense Breakdown
                    if (activeTrip.expenses.isNotEmpty()) {
                        item {
                            ExpenseBreakdownCard(expenses = activeTrip.expenses)
                        }
                    }

                    // 11. Self-Plan vs Package Comparison Card
                    item {
                        SelfPlanVsPackageCard(
                            packageCost = activeTrip.packageCost,
                            selfPlanCost = activeTrip.estimatedCost,
                            onCompare = onNavigateToComparison
                        )
                    }

                    // 12. Hotel Preview
                    if (activeTrip.hotel != null) {
                        item {
                            HotelPreviewCard(
                                hotel = activeTrip.hotel,
                                onViewHotel = onNavigateToHotels
                            )
                        }
                    }

                    // 13. Places to Visit (Attractions)
                    if (activeTrip.attractions.isNotEmpty()) {
                        item {
                            AttractionsSection(
                                attractions = activeTrip.attractions,
                                onAttractionClick = { onNavigateToExplorePlace(it.name) }
                            )
                        }
                    }

                    // 14. Nearby Food (Restaurants)
                    if (activeTrip.restaurants.isNotEmpty()) {
                        item {
                            RestaurantsSection(
                                restaurants = activeTrip.restaurants,
                                onViewRestaurant = { onNavigateToPlaces() }
                            )
                        }
                    }

                    // 15. Trip Weather
                    item {
                        WeatherCard(trip = activeTrip)
                    }

                    // 16. Day-By-Day Itinerary Preview
                    if (activeTrip.itinerary.isNotEmpty()) {
                        item {
                            ItineraryPreviewCard(
                                itinerary = activeTrip.itinerary,
                                onViewFullItinerary = onNavigateToItinerary
                            )
                        }
                    }

                    // 17. Route Map Preview
                    if (activeTrip.routeSteps.isNotEmpty()) {
                        item {
                            MapPreviewCard(
                                routeSteps = activeTrip.routeSteps,
                                onOpenMap = onNavigateToMap
                            )
                        }
                    }

                    // 18. Saved Places
                    item {
                        SavedPlacesSection(
                            savedPlaces = savedPlaces,
                            onToggleSave = { placeName ->
                                savedPlaces = savedPlaces.map {
                                    if (it.first == placeName) it.first to !it.second else it
                                }
                            }
                        )
                    }

                    // 19. Recent Searches
                    item {
                        RecentSearchesSection(
                            recentSearches = listOf("Ooty", "Kodaikanal", "Munnar", "Coorg"),
                            onSelectSearch = { search -> onNavigateToExplorePlace(search) }
                        )
                    }

                    // 20. Packing Checklist
                    item {
                        PackingChecklistCard(
                            itemsList = samplePacking,
                            onToggleItem = { id ->
                                val index = samplePacking.indexOfFirst { it.id == id }
                                if (index != -1) {
                                    val item = samplePacking[index]
                                    samplePacking[index] = item.copy(isPacked = !item.isPacked)
                                }
                            }
                        )
                    }

                    // 21. Travel Documents
                    item {
                        TravelDocumentsCard(
                            documents = sampleDocs,
                            onAddDocument = { showAddDocDialog = true }
                        )
                    }

                    // 22. Emergency / Safety
                    item {
                        SafetyCard(onOpenSafetyCenter = onNavigateToSafety)
                    }

                    // 23. Share Trip
                    item {
                        Button(
                            onClick = { shareTripSummary(context, activeTrip) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SHARE TRIP", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 25. Delete Trip Confirmation Dialog
    if (showDeleteDialog && selectedTripForAction != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Trip?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${selectedTripForAction?.destination}' trip? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        tripsList.remove(selectedTripForAction)
                        showDeleteDialog = false
                        Toast.makeText(context, "Trip deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 24. Edit Trip Dialog
    if (showEditDialog && selectedTripForAction != null) {
        var editDestination by remember { mutableStateOf(selectedTripForAction?.destination ?: "") }
        var editDates by remember { mutableStateOf("${selectedTripForAction?.startDate} - ${selectedTripForAction?.endDate}") }
        var editTravelers by remember { mutableStateOf(selectedTripForAction?.travelers?.toString() ?: "2") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("EDIT TRIP", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editDestination,
                        onValueChange = { editDestination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDates,
                        onValueChange = { editDates = it },
                        label = { Text("Dates") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editTravelers,
                        onValueChange = { editTravelers = it },
                        label = { Text("Travelers") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val index = tripsList.indexOfFirst { it.id == selectedTripForAction?.id }
                        if (index != -1) {
                            val current = tripsList[index]
                            tripsList[index] = current.copy(
                                destination = editDestination,
                                travelers = editTravelers.toIntOrNull() ?: current.travelers
                            )
                        }
                        showEditDialog = false
                        Toast.makeText(context, "Trip updated", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Document Dialog
    if (showAddDocDialog) {
        AlertDialog(
            onDismissRequest = { showAddDocDialog = false },
            title = { Text("ADD TRAVEL DOCUMENT", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newDocTitle,
                        onValueChange = { newDocTitle = it },
                        label = { Text("Document Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDocType,
                        onValueChange = { newDocType = it },
                        label = { Text("Document Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDocTitle.isNotBlank()) {
                            sampleDocs.add(
                                TravelDocumentItem(
                                    id = "d${sampleDocs.size + 1}",
                                    title = newDocTitle,
                                    type = newDocType
                                )
                            )
                            newDocTitle = ""
                            showAddDocDialog = false
                            Toast.makeText(context, "Document added locally", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Add Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDocDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
