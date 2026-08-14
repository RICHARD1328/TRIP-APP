package com.tripdirect.ui.components.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.data.model.ItineraryItem
import com.tripdirect.data.model.ItineraryItemType
import com.tripdirect.data.model.TripBudget
import com.tripdirect.data.model.TripDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = if (selected) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else null
    )
}

@Composable
fun TripDayCard(
    day: TripDay,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("DAY ${day.dayNumber}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(day.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Show first 3 items summary
            day.items.take(3).forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    val icon = when (item.type) {
                        ItineraryItemType.TRANSPORT, ItineraryItemType.TRAVEL -> Icons.Default.DirectionsBus
                        ItineraryItemType.HOTEL -> Icons.Default.Hotel
                        ItineraryItemType.ATTRACTION, ItineraryItemType.ACTIVITY -> Icons.Default.Map
                        ItineraryItemType.RESTAURANT -> Icons.Default.LocalDining
                        ItineraryItemType.FREE_TIME, ItineraryItemType.BREAK -> Icons.Default.Schedule
                        else -> Icons.Default.Place
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            if (day.items.size > 3) {
                Text("+ ${day.items.size - 3} more activities", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, start = 24.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Estimated: ₹${day.estimatedCost}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ItineraryItemCard(item: ItineraryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.startTime} - ${item.endTime}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("₹${item.estimatedCost}", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (item.type) {
                    ItineraryItemType.TRANSPORT, ItineraryItemType.TRAVEL -> Icons.Default.DirectionsBus
                    ItineraryItemType.HOTEL -> Icons.Default.Hotel
                    ItineraryItemType.ATTRACTION, ItineraryItemType.ACTIVITY -> Icons.Default.Map
                    ItineraryItemType.RESTAURANT -> Icons.Default.LocalDining
                    ItineraryItemType.FREE_TIME, ItineraryItemType.BREAK -> Icons.Default.Schedule
                    else -> Icons.Default.Place
                }
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.description, style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Duration: ${item.durationMinutes / 60}h ${if (item.durationMinutes % 60 > 0) "${item.durationMinutes % 60}m" else ""}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun BudgetSummaryCard(budget: TripBudget) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estimated Trip Cost", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            BudgetRow("Transport", budget.transport)
            BudgetRow("Hotel", budget.accommodation)
            BudgetRow("Food", budget.food)
            BudgetRow("Activities", budget.activities)
            BudgetRow("Local Travel", budget.localTransport)
            if (budget.miscellaneous > 0) {
                BudgetRow("Miscellaneous", budget.miscellaneous)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text("₹${budget.total}", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val diff = budget.userBudget - budget.total
            if (diff >= 0) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)).padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Within Budget", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                        Text("Estimated savings: ₹$diff", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32), modifier = Modifier.padding(start = 24.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)).padding(12.dp)
                ) {
                    Column {
                        Text("⚠ Over Budget", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        Text("Estimated excess: ₹${-diff}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFC62828))
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetRow(label: String, amount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("₹$amount", style = MaterialTheme.typography.bodyMedium)
    }
}
