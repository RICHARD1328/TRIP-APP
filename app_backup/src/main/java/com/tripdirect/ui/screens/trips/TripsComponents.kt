package com.tripdirect.ui.screens.trips

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tripdirect.data.model.*

@Composable
fun SafeAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.clip(shape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            onLoading = {
                isLoading = true
                isError = false
            },
            onSuccess = {
                isLoading = false
                isError = false
            },
            onError = {
                isLoading = false
                isError = true
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌍", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Loading destination...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Destination image unavailable",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FeaturedNextAdventureCard(
    trip: FullTrip,
    onViewItinerary: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(
            text = "YOUR NEXT ADVENTURE",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewItinerary() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column {
                val resolvedImageUrl = trip.imageUrl ?: DestinationImageProvider.getDestinationImage(trip.destination) ?: ""
                val contentDesc = DestinationImageProvider.getDestinationDescription(trip.destination)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    SafeAsyncImage(
                        imageUrl = resolvedImageUrl,
                        contentDescription = contentDesc,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                    startY = 60f
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = trip.status,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        IconButton(
                            onClick = { isFavorite = !isFavorite },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Your next adventure",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "🌄 ${trip.destination}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${trip.startLocation} → ${trip.destination}  •  ${trip.startDate} - ${trip.endDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${trip.estimatedCost} estimated",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${trip.travelers} Travelers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onViewItinerary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("VIEW ITINERARY", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 5. TRIP HERO CARD
@Composable
fun TripHeroCard(
    trip: FullTrip,
    onViewTrip: () -> Unit,
    onEditTrip: () -> Unit,
    onDeleteTrip: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            val resolvedImageUrl = trip.imageUrl ?: DestinationImageProvider.getDestinationImage(trip.destination) ?: ""
            val contentDesc = DestinationImageProvider.getDestinationDescription(trip.destination)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                SafeAsyncImage(
                    imageUrl = resolvedImageUrl,
                    contentDescription = contentDesc,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )

                // Dark gradient overlay over bottom of image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                ),
                                startY = 60f
                            )
                        )
                )

                // Top status tag & Edit/Delete options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = trip.status,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isFavorite = !isFavorite },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onEditTrip,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Trip",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onDeleteTrip,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Trip",
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Overlay Text Info
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🌄 ${trip.destination}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${trip.startLocation} → ${trip.destination}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${trip.startDate} - ${trip.endDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "3 Days • ${trip.travelers} Travelers",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${trip.estimatedCost}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val savings = trip.packageCost - trip.estimatedCost
                        if (savings > 0) {
                            Text(
                                text = "Potential Saving ₹$savings",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    Button(
                        onClick = onViewTrip,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("VIEW TRIP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 6. TRIP PROGRESS & 7. COUNTDOWN
@Composable
fun TripProgressCard(trip: FullTrip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TRIP PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Planning completed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${trip.planningCompletedPercentage}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { trip.planningCompletedPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Completed:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("✓ Destination", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                    Text("✓ Hotel", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                    Text("✓ Transport", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                    Text("✓ Places", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Remaining:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("○ Final itinerary", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (trip.status == "UPCOMING") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YOUR TRIP STARTS IN ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${trip.daysUntilStart} DAYS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

// 8. QUICK ACTIONS
data class QuickActionItem(
    val title: String,
    val emoji: String,
    val onClick: () -> Unit
)

@Composable
fun QuickActionsSection(actions: List<QuickActionItem>) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(actions) { action ->
                Card(
                    modifier = Modifier
                        .width(96.dp)
                        .height(90.dp)
                        .clickable { action.onClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = action.emoji, fontSize = 26.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = action.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// 9. BUDGET SUMMARY
@Composable
fun BudgetSummaryCard(
    totalBudget: Int,
    estimatedCost: Int,
    packageCost: Int
) {
    val remaining = totalBudget - estimatedCost
    val potentialSaving = packageCost - estimatedCost
    val percentPlanned = ((estimatedCost.toFloat() / totalBudget) * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TRIP BUDGET",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Budget", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹$totalBudget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Estimated Cost", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹$estimatedCost", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text("Remaining", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹$remaining", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { estimatedCost.toFloat() / totalBudget },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (estimatedCost <= totalBudget) MaterialTheme.colorScheme.primary else Color.Red,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₹$estimatedCost / ₹$totalBudget",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$percentPlanned% of budget planned",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (potentialSaving > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Potential Saving vs Package: ₹$potentialSaving",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// 10. EXPENSE BREAKDOWN
@Composable
fun ExpenseBreakdownCard(expenses: List<ExpenseItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "EXPENSE BREAKDOWN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            var total = 0
            expenses.forEach { expense ->
                total += expense.amount
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = expense.iconName, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = expense.category, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = "₹${expense.amount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("₹$total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// 11. SELF-PLAN VS PACKAGE
@Composable
fun SelfPlanVsPackageCard(
    packageCost: Int,
    selfPlanCost: Int,
    onCompare: () -> Unit
) {
    val saving = packageCost - selfPlanCost
    val savingPercent = ((saving.toDouble() / packageCost) * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TRAVEL SMARTER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tour Package", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "₹$packageCost",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Column {
                    Text("TripDirect Self Plan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text("₹$selfPlanCost", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF4CAF50).copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Potential Saving: ₹$saving", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    Text("$savingPercent% cheaper", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Plan independently. Avoid unnecessary broker fees.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCompare,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("COMPARE OPTIONS", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 12. HOTEL PREVIEW
@Composable
fun HotelPreviewCard(
    hotel: HotelItem,
    onViewHotel: () -> Unit
) {
    Column {
        Text(
            text = "YOUR STAY",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(modifier = Modifier.height(160.dp)) {
                // ~40% width for image
                SafeAsyncImage(
                    imageUrl = hotel.imageUrl,
                    contentDescription = hotel.name,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.4f),
                    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🏨 ${hotel.name}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                Text(text = "${hotel.rating}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "₹${hotel.pricePerNight} / night",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${hotel.distance} from center",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            hotel.facilities.take(3).forEach { fac ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "✓ $fac",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onViewHotel,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("VIEW HOTEL", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 13. ATTRACTIONS
@Composable
fun AttractionsSection(
    attractions: List<AttractionItem>,
    onAttractionClick: (AttractionItem) -> Unit
) {
    Column {
        Text(
            text = "PLACES TO VISIT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(attractions) { attraction ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .clickable { onAttractionClick(attraction) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SafeAsyncImage(
                            imageUrl = attraction.imageUrl,
                            contentDescription = attraction.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = attraction.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                    Text("${attraction.rating}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Text("${attraction.distance}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = if (attraction.entryFee == 0) "Free entry" else "Entry: ₹${attraction.entryFee}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// 14. RESTAURANT RECOMMENDATIONS
@Composable
fun RestaurantsSection(
    restaurants: List<RestaurantItem>,
    onViewRestaurant: (RestaurantItem) -> Unit
) {
    Column {
        Text(
            text = "NEARBY FOOD",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(restaurants) { restaurant ->
                Card(
                    modifier = Modifier
                        .width(200.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SafeAsyncImage(
                            imageUrl = restaurant.imageUrl,
                            contentDescription = restaurant.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = restaurant.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(restaurant.priceLevel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                Text(" ${restaurant.rating} • ${restaurant.distance}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = restaurant.cuisine,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = { onViewRestaurant(restaurant) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 2.dp)
                            ) {
                                Text("VIEW", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 15. WEATHER
@Composable
fun WeatherCard(trip: FullTrip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("☀️", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("TRIP WEATHER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("${trip.destination} • ${trip.startDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(trip.weatherCondition, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(trip.weatherTemp, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text(trip.weatherRange, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// 16. DAY-BY-DAY ITINERARY
@Composable
fun ItineraryPreviewCard(
    itinerary: List<ItineraryDayItem>,
    onViewFullItinerary: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "YOUR ITINERARY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            itinerary.forEach { day ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "DAY ${day.dayNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    day.activities.forEach { act ->
                        Text(
                            text = act,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onViewFullItinerary,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("VIEW FULL ITINERARY", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 17. MAP PREVIEW
@Composable
fun MapPreviewCard(
    routeSteps: List<String>,
    onOpenMap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YOUR ROUTE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Route Sequence
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                routeSteps.forEachIndexed { index, step ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = step, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                    if (index < routeSteps.size - 1) {
                        Box(
                            modifier = Modifier
                                .padding(start = 3.dp, top = 2.dp, bottom = 2.dp)
                                .width(2.dp)
                                .height(12.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onOpenMap,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("OPEN MAP", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 18. SAVED PLACES
@Composable
fun SavedPlacesSection(
    savedPlaces: List<Pair<String, Boolean>>,
    onToggleSave: (String) -> Unit
) {
    Column {
        Text(
            text = "SAVED PLACES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            savedPlaces.forEach { (place, isSaved) ->
                FilterChip(
                    selected = isSaved,
                    onClick = { onToggleSave(place) },
                    label = { Text(place) },
                    leadingIcon = {
                        Icon(
                            if (isSaved) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isSaved) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

// 19. RECENT SEARCHES
@Composable
fun RecentSearchesSection(
    recentSearches: List<String>,
    onSelectSearch: (String) -> Unit
) {
    Column {
        Text(
            text = "RECENTLY SEARCHED",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            recentSearches.forEach { search ->
                SuggestionChip(
                    onClick = { onSelectSearch(search) },
                    label = { Text(search) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

// 20. PACKING CHECKLIST
@Composable
fun PackingChecklistCard(
    itemsList: List<PackingItem>,
    onToggleItem: (String) -> Unit
) {
    val packedCount = itemsList.count { it.isPacked }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PACKING CHECKLIST",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$packedCount / ${itemsList.size} items packed",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            itemsList.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleItem(item.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isPacked,
                        onCheckedChange = { onToggleItem(item.id) }
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.isPacked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// 21. TRAVEL DOCUMENTS
@Composable
fun TravelDocumentsCard(
    documents: List<TravelDocumentItem>,
    onAddDocument: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRAVEL DOCUMENTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onAddDocument) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD DOCUMENT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            documents.forEach { doc ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = doc.title, style = MaterialTheme.typography.bodyMedium)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = doc.type,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// 22. SAFETY & EMERGENCY
@Composable
fun SafetyCard(onOpenSafetyCenter: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFF5252))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SAFETY & ASSISTANCE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Quick access to emergency contacts, local police, nearest hospital, and 24/7 tourist helpline.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onOpenSafetyCenter,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("OPEN SAFETY CENTER", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
            }
        }
    }
}

// 23. SHARE TRIP HELPER
fun shareTripSummary(context: Context, trip: FullTrip) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            """
            TripDirect Trip
            Destination: ${trip.destination}
            Dates: ${trip.startDate} - ${trip.endDate}
            Travelers: ${trip.travelers}
            Estimated Cost: ₹${trip.estimatedCost}
            Planned with TripDirect!
            """.trimIndent()
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Trip Details"))
}

// 27. EMPTY STATE
@Composable
fun TripsEmptyState(onPlanMyTrip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🧳", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No trips planned yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start planning your next journey without unnecessary packages or middlemen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onPlanMyTrip,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("PLAN MY TRIP", fontWeight = FontWeight.Bold)
            }
        }
    }
}
