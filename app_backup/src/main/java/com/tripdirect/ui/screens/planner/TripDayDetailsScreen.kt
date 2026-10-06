package com.tripdirect.ui.screens.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.planner.ItineraryItemCard
import com.tripdirect.viewmodel.TripPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDayDetailsScreen(
    dayNumber: Int,
    viewModel: TripPlannerViewModel,
    onNavigateBack: () -> Unit
) {
    val tripPlan by viewModel.tripPlan.collectAsState()
    val day = viewModel.getDayDetails(dayNumber)

    if (day == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Day details not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(day.title) },
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
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Day ${day.dayNumber}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total estimated cost: ₹${day.estimatedCost}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            itemsIndexed(day.items) { index, item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Timeline connector
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(48.dp)
                    ) {
                        Text(item.startTime, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        
                        if (index < day.items.size - 1) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp).height(32.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Card
                    Box(modifier = Modifier.weight(1f).padding(bottom = 16.dp)) {
                        ItineraryItemCard(item = item)
                    }
                }
            }
        }
    }
}
