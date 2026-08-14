package com.tripdirect.ui.screens.itinerary

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.itinerary.BudgetSummaryCard
import com.tripdirect.ui.components.itinerary.CategoryFilterRow
import com.tripdirect.ui.components.itinerary.CustomActivityDialog
import com.tripdirect.ui.components.itinerary.DaySelector
import com.tripdirect.ui.components.itinerary.DaySummaryCard
import com.tripdirect.ui.components.itinerary.EmptyItineraryView
import com.tripdirect.ui.components.itinerary.TimelineItem
import com.tripdirect.ui.components.itinerary.TripHeader
import com.tripdirect.viewmodel.ItineraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    viewModel: ItineraryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToRoute: () -> Unit,
    onNavigateToDayDetails: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToSummary: () -> Unit,
    onGenerateItinerary: () -> Unit
) {
    val context = LocalContext.current
    val itinerary by viewModel.itinerary.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val selectedFilter by viewModel.selectedFilterType.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isOptimizing by viewModel.isOptimizing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.searchItinerary(it) },
                                placeholder = { Text("Search activities, places...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        } else {
                            Text("My Itinerary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isSearchActive) {
                                isSearchActive = false
                                viewModel.searchItinerary("")
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(if (isSearchActive) Icons.Default.Close else Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onNavigateToSummary) {
                            Icon(Icons.Default.PieChart, contentDescription = "Summary")
                        }
                        IconButton(onClick = { viewModel.saveItinerary() }) {
                            Icon(if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = "Save Itinerary")
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, viewModel.getShareableText())
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Itinerary"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                )
                CategoryFilterRow(
                    selectedType = selectedFilter,
                    onTypeSelected = { viewModel.filterByType(it) }
                )
            }
        },
        floatingActionButton = {
            if (itinerary != null && selectedDay != null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Activity")
                }
            }
        },
        bottomBar = {
            if (itinerary != null && selectedDay != null) {
                Surface(tonalElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateToBudget,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Budget")
                        }
                        Button(
                            onClick = onNavigateToRoute,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Route Map")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            } else if (itinerary == null) {
                EmptyItineraryView(
                    onGenerateClick = onGenerateItinerary,
                    onCreateManualClick = { showAddDialog = true }
                )
            } else {
                val trip = itinerary!!
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        TripHeader(
                            itinerary = trip,
                            onEditTripClick = onNavigateToEdit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BudgetSummaryCard(itinerary = trip, onClick = onNavigateToBudget)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        DaySelector(
                            days = trip.days,
                            selectedDay = selectedDay,
                            onDaySelected = { viewModel.selectDay(it) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }

                    if (selectedDay != null) {
                        item {
                            DaySummaryCard(
                                day = selectedDay!!,
                                onViewRouteClick = onNavigateToRoute,
                                onOptimizeClick = { viewModel.optimizeSelectedDay() }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        val filteredActivities = selectedDay!!.activities.filter { item ->
                            val matchesType = selectedFilter == null || item.type == selectedFilter
                            val matchesQuery = searchQuery.isBlank() ||
                                    item.title.contains(searchQuery, ignoreCase = true) ||
                                    item.location.contains(searchQuery, ignoreCase = true)
                            matchesType && matchesQuery
                        }

                        itemsIndexed(filteredActivities) { index, item ->
                            TimelineItem(
                                item = item,
                                isLast = index == filteredActivities.size - 1,
                                onItemClick = {
                                    viewModel.selectItem(item)
                                    onNavigateToDayDetails()
                                },
                                onMarkStatus = { status -> viewModel.markItemStatus(item.id, status) },
                                onViewRoute = onNavigateToRoute
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Clearance for FAB and BottomBar
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && selectedDay != null) {
        CustomActivityDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, location, type, startTime, duration, cost, notes ->
                viewModel.addCustomActivity(
                    dayNumber = selectedDay!!.dayNumber,
                    title = title,
                    location = location,
                    type = type,
                    startTime = startTime,
                    durationMinutes = duration,
                    cost = cost,
                    notes = notes
                )
            }
        )
    }
}

