package com.tripdirect.ui.screens.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.BudgetCalculatorViewModel
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBreakdownScreen(
    viewModel: BudgetCalculatorViewModel,
    onNavigateBack: () -> Unit
) {
    val breakdown by viewModel.budgetBreakdown.collectAsState()
    val days by viewModel.days.collectAsState()
    val adults by viewModel.adults.collectAsState()
    val children by viewModel.children.collectAsState()
    val transportCost by viewModel.transportCost.collectAsState()
    val accommodationPerNight by viewModel.accommodationPerNight.collectAsState()
    val foodPerPersonPerDay by viewModel.foodPerPersonPerDay.collectAsState()
    val attractionCost by viewModel.attractionCost.collectAsState()
    val activityCost by viewModel.activityCost.collectAsState()
    val localTransportPerDay by viewModel.localTransportPerDay.collectAsState()
    val miscellaneousCost by viewModel.miscellaneousCost.collectAsState()

    val result = breakdown
    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No budget data found.")
        }
        return
    }

    val travelers = adults + children
    val nights = max(days - 1, 0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Breakdown") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BreakdownDetailCard(
                    title = "Transportation",
                    icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    calculationText = "Total transport cost for all travelers",
                    total = result.transport
                )
            }
            item {
                BreakdownDetailCard(
                    title = "Accommodation",
                    icon = { Icon(Icons.Default.Hotel, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    calculationText = "₹$accommodationPerNight/night × $nights nights",
                    total = result.accommodation
                )
            }
            item {
                BreakdownDetailCard(
                    title = "Food",
                    icon = { Icon(Icons.Default.LocalDining, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    calculationText = "₹$foodPerPersonPerDay/person/day × $travelers travelers × $days days",
                    total = result.food
                )
            }
            item {
                BreakdownDetailCard(
                    title = "Attractions & Entry Fees",
                    icon = { Icon(Icons.Default.LocalActivity, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    calculationText = "Estimated total across all attractions",
                    total = result.attractions
                )
            }
            item {
                BreakdownDetailCard(
                    title = "Activities",
                    icon = { Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    calculationText = "Estimated cost for special activities",
                    total = result.activities
                )
            }
            item {
                BreakdownDetailCard(
                    title = "Local Transport",
                    icon = { Icon(Icons.Default.LocalTaxi, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    calculationText = "₹$localTransportPerDay/day × $days days",
                    total = result.localTransport
                )
            }
            item {
                BreakdownDetailCard(
                    title = "Miscellaneous",
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    calculationText = "Shopping, emergencies, and other costs",
                    total = result.miscellaneous
                )
            }
        }
    }
}

@Composable
fun BreakdownDetailCard(
    title: String,
    icon: @Composable () -> Unit,
    calculationText: String,
    total: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(calculationText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("₹$total", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
