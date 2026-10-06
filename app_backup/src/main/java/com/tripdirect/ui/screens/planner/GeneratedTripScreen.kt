package com.tripdirect.ui.screens.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.planner.BudgetSummaryCard
import com.tripdirect.ui.components.planner.TripDayCard
import com.tripdirect.viewmodel.TripPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedTripScreen(
    viewModel: TripPlannerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDay: (Int) -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val tripPlan by viewModel.tripPlan.collectAsState()

    val plan = tripPlan

    if (plan == null) {
        // Fallback if accessed without data
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No trip data found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Trip is Ready! 🎉") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onNavigateToEdit, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }
                        OutlinedButton(onClick = { viewModel.optimizeTrip() }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Optimize")
                        }
                        OutlinedButton(onClick = { /* Save Trip */ }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* Add to my trip */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add to My Trip")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "${plan.from} → ${plan.destination}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${plan.days.size} Days • ${plan.travelers} Travelers",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Trip Summary", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = plan.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            item {
                Text("Day-by-Day Itinerary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            items(plan.days) { day ->
                TripDayCard(
                    day = day,
                    onClick = { onNavigateToDay(day.dayNumber) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                BudgetSummaryCard(budget = plan.budgetSummary)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
