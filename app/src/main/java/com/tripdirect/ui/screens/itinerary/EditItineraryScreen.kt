package com.tripdirect.ui.screens.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.itinerary.CustomActivityDialog
import com.tripdirect.ui.components.itinerary.DaySelector
import com.tripdirect.viewmodel.ItineraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItineraryScreen(
    viewModel: ItineraryViewModel,
    onNavigateBack: () -> Unit
) {
    val itinerary by viewModel.itinerary.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Itinerary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.saveItinerary()
                        onNavigateBack()
                    }) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Activity")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            itinerary?.let { trip ->
                DaySelector(
                    days = trip.days,
                    selectedDay = selectedDay,
                    onDaySelected = { viewModel.selectDay(it) }
                )
                HorizontalDivider()
            }

            selectedDay?.let { day ->
                Text(
                    text = "Editing Day ${day.dayNumber} Schedule (${day.activities.size} items)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(day.activities) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("${item.startTime} • ${item.location}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.removeActivity(item.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItinerarySummaryScreen(
    viewModel: ItineraryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBudget: () -> Unit
) {
    val itinerary by viewModel.itinerary.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Summary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (itinerary == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No itinerary data available.")
            }
            return@Scaffold
        }

        val trip = itinerary!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("${trip.destination} Trip Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Duration")
                        Text("${trip.days.size} Days", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Travelers")
                        Text("${trip.travelers} Persons", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Distance")
                        Text("${String.format("%.1f", trip.totalDistanceKm)} km", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Travel Time")
                        Text("${trip.totalTravelMinutes / 60}h ${trip.totalTravelMinutes % 60}m", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Day-by-Day Cost Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    trip.days.forEach { day ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Day ${day.dayNumber}: ${day.title}")
                            Text("₹${day.estimatedCost}", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated Total", fontWeight = FontWeight.Bold)
                        Text("₹${trip.totalEstimatedCost}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Trip Budget")
                        Text("₹${trip.budget}", fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Remaining Budget")
                        Text("₹${trip.remainingBudget}", fontWeight = FontWeight.Bold, color = if (trip.isWithinBudget) Color(0xFF2E7D32) else Color(0xFFC62828))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToBudget,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Budget Calculator")
            }
        }
    }
}

