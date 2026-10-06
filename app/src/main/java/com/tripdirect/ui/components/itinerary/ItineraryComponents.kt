package com.tripdirect.ui.components.itinerary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.Itinerary
import com.tripdirect.data.model.ItineraryDay
import com.tripdirect.data.model.ItineraryItem
import com.tripdirect.data.model.ItineraryItemType
import com.tripdirect.data.model.ItineraryStatus

@Composable
fun TripHeader(
    itinerary: Itinerary,
    onEditTripClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${itinerary.destination} Trip",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${itinerary.days.size} Days • ${itinerary.travelers} Travelers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${itinerary.startDate} – ${itinerary.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            OutlinedButton(
                onClick = onEditTripClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit Trip")
            }
        }
    }
}

@Composable
fun BudgetSummaryCard(itinerary: Itinerary, onClick: () -> Unit) {
    val isWithinBudget = itinerary.isWithinBudget
    val remaining = itinerary.remainingBudget
    val statusColor = if (isWithinBudget) Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Trip Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Estimated Cost", style = MaterialTheme.typography.bodyMedium)
                Text("₹${itinerary.totalEstimatedCost}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Your Budget", style = MaterialTheme.typography.bodyMedium)
                Text("₹${itinerary.budget}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isWithinBudget) "✓ ₹$remaining remaining" else "⚠ ₹${-remaining} over budget",
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
                Text("View Budget", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun DaySelector(days: List<ItineraryDay>, selectedDay: ItineraryDay?, onDaySelected: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(days) { day ->
            val isSelected = day.dayNumber == selectedDay?.dayNumber
            FilterChip(
                selected = isSelected,
                onClick = { onDaySelected(day.dayNumber) },
                label = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("DAY ${day.dayNumber}", fontWeight = FontWeight.Bold)
                        Text(day.date, style = MaterialTheme.typography.labelSmall)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedType: ItineraryItemType?,
    onTypeSelected: (ItineraryItemType?) -> Unit
) {
    val filters = listOf(
        Pair(null, "All"),
        Pair(ItineraryItemType.ATTRACTION, "Attractions"),
        Pair(ItineraryItemType.HOTEL, "Hotels"),
        Pair(ItineraryItemType.RESTAURANT, "Food"),
        Pair(ItineraryItemType.TRAVEL, "Travel")
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (type, label) ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun DaySummaryCard(
    day: ItineraryDay,
    onViewRouteClick: () -> Unit,
    onOptimizeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DAY ${day.dayNumber} — ${day.title}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${day.activities.size} Stops", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f km", day.totalDistanceKm), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${day.totalTravelMinutes} min Travel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOptimizeClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onTertiaryContainer)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Optimize Day")
                }
                Button(
                    onClick = onViewRouteClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Day Route")
                }
            }
        }
    }
}

@Composable
fun TimelineItem(
    item: ItineraryItem,
    isLast: Boolean,
    onItemClick: () -> Unit,
    onMarkStatus: (ItineraryStatus) -> Unit,
    onViewRoute: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val isSkipped = item.status == ItineraryStatus.SKIPPED
    val isCompleted = item.status == ItineraryStatus.COMPLETED
    val isInProgress = item.status == ItineraryStatus.IN_PROGRESS

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp)
            .clickable {
                expanded = !expanded
                onItemClick()
            }
    ) {
        // Time Column
        Column(modifier = Modifier.width(72.dp).padding(top = 12.dp)) {
            Text(
                text = item.startTime,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSkipped) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Timeline Dot & Line
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .size(14.dp)
                    .background(
                        color = when {
                            isSkipped -> Color.Gray
                            isCompleted -> Color(0xFF2E7D32)
                            isInProgress -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        },
                        shape = CircleShape
                    )
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        // Card Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, top = 8.dp, bottom = if (isLast) 8.dp else 16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSkipped -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        isInProgress -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            val icon = when (item.type) {
                                ItineraryItemType.TRAVEL, ItineraryItemType.TRANSPORT -> Icons.Default.DirectionsBus
                                ItineraryItemType.HOTEL -> Icons.Default.Hotel
                                ItineraryItemType.ATTRACTION -> Icons.Default.Star
                                ItineraryItemType.RESTAURANT -> Icons.Default.Restaurant
                                ItineraryItemType.ACTIVITY -> Icons.Default.Map
                                else -> Icons.Default.Place
                            }
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (isSkipped) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }

                        if (isCompleted) {
                            Text("✓ Done", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        } else if (isSkipped) {
                            Text("⊘ Skipped", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (item.description.isNotEmpty()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (item.distanceFromPreviousKm > 0) {
                            Text(
                                text = "${String.format("%.1f", item.distanceFromPreviousKm)} km • ${item.travelTimeMinutes} min (${item.transportMode})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(item.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (item.estimatedCost > 0) {
                            Text("₹${item.estimatedCost}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            if (item.openingTime != null) {
                                Text("Hours: ${item.openingTime} – ${item.closingTime}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (item.durationMinutes > 0) {
                                Text("Recommended Visit: ${item.durationMinutes} mins", style = MaterialTheme.typography.bodySmall)
                            }
                            if (!item.notes.isNullOrEmpty()) {
                                Text("Notes: ${item.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isCompleted) {
                                    OutlinedButton(
                                        onClick = { onMarkStatus(ItineraryStatus.COMPLETED) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Mark Complete", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                if (!isSkipped) {
                                    OutlinedButton(
                                        onClick = { onMarkStatus(ItineraryStatus.SKIPPED) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Skip", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Button(
                                    onClick = onViewRoute,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("View Route", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomActivityDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, location: String, type: ItineraryItemType, startTime: String, duration: Int, cost: Int, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("10:00 AM") }
    var duration by remember { mutableStateOf("60") }
    var cost by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ItineraryItemType.ATTRACTION) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Activity") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Activity Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (mins)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Estimated Cost (₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            title,
                            location,
                            selectedType,
                            startTime,
                            duration.toIntOrNull() ?: 60,
                            cost.toIntOrNull() ?: 0,
                            notes
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Add to Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EmptyItineraryView(
    onGenerateClick: () -> Unit,
    onCreateManualClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your itinerary is empty", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Create a personalized trip plan with TripDirect AI or build one manually.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onGenerateClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate with AI")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onCreateManualClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Manually")
            }
        }
    }
}

