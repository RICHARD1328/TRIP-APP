package com.tripdirect.ui.screens.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.ItineraryItemType
import com.tripdirect.data.model.ItineraryStatus
import com.tripdirect.viewmodel.ItineraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailsScreen(
    viewModel: ItineraryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRoute: () -> Unit
) {
    val selectedItem by viewModel.selectedItem.collectAsState()
    var showSafetyDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(5) }
    var reviewSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSafetyDialog = true }) {
                        Icon(Icons.Default.Warning, contentDescription = "Emergency Safety", tint = Color(0xFFC62828))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (selectedItem == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues))
            return@Scaffold
        }

        val item = selectedItem!!
        val isCompleted = item.status == ItineraryStatus.COMPLETED

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    AssistChip(
                        onClick = {},
                        label = { Text(item.type.name) },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(item.location, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Schedule & Times", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Start Time")
                        Text(item.startTime, fontWeight = FontWeight.SemiBold)
                    }
                    if (item.endTime.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("End Time")
                            Text(item.endTime, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Visit Duration")
                        Text("${item.durationMinutes} minutes", fontWeight = FontWeight.SemiBold)
                    }
                    if (item.openingTime != null) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Opening Hours")
                            Text("${item.openingTime} – ${item.closingTime}", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Travel & Expenses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    if (item.distanceFromPreviousKm > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Travel Distance")
                            Text("${String.format("%.1f", item.distanceFromPreviousKm)} km", fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Travel Time")
                            Text("${item.travelTimeMinutes} mins (${item.transportMode})", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated Cost")
                        Text("₹${item.estimatedCost}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            if (!item.notes.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Notes & Advice", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.notes, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }

            // Review section for completed activities
            if (isCompleted || item.type == ItineraryItemType.ATTRACTION) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("How was your visit?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (star in 1..5) {
                                IconButton(onClick = { rating = star }) {
                                    Icon(
                                        imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Star $star",
                                        tint = Color(0xFFFFA000)
                                    )
                                }
                            }
                        }
                        if (reviewSubmitted) {
                            Text("✓ Review submitted! Thank you.", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        } else {
                            Button(
                                onClick = { reviewSubmitted = true },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Submit Review")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        viewModel.markItemStatus(item.id, ItineraryStatus.COMPLETED)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isCompleted) "✓ Completed" else "Mark Complete")
                }

                Button(
                    onClick = onNavigateToRoute,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Route")
                }
            }
        }
    }

    if (showSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Emergency / Safety Info")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📍 Current Area: Ooty Town Center")
                    Text("🚨 Police Emergency: 100")
                    Text("🚑 Ambulance: 108")
                    Text("🏥 Nearby Hospital: Government Hospital, Ooty (1.8 km)")
                    Text("🛡 Tourist Helpline: 1800-425-4646")
                }
            },
            confirmButton = {
                Button(onClick = { showSafetyDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }
}

