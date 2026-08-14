package com.tripdirect.ui.screens.profile

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.NavigateNext
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.data.model.*
import com.tripdirect.ui.screens.trips.SafeAsyncImage

// 1. PROFILE HEADER CARD
@Composable
fun ProfileHeaderCard(
    profile: UserProfile,
    onEditProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Avatar with border & fallback initials
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (!profile.profileImage.isNull_or_blank()) {
                    SafeAsyncImage(
                        imageUrl = profile.profileImage ?: "",
                        contentDescription = profile.name,
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape
                    )
                } else {
                    Text(
                        text = profile.initials,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onEditProfileClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 4. TRAVEL STATISTICS CARD
@Composable
fun ProfileStatisticsCard(
    stats: UserStats,
    onTripsClick: () -> Unit,
    onSavedClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onTripsClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${stats.totalTrips}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text("Trips", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${stats.placesVisited}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Places", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("₹${stats.totalSaved}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                Text("Savings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onSavedClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${stats.favorites}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.Red)
                Text("Favorites", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// Helper String Null or Blank
private fun String?.isNull_or_blank() = this == null || this.isBlank()

// 5. TRAVEL SAVINGS CARD
@Composable
fun TravelSavingsCard(
    onViewSavings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2E1E))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Savings, contentDescription = null, tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text("YOUR TRAVEL SAVINGS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("You saved ₹8,500 through self-planned trips.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tour Packages", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                    Text("₹42,000", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f))
                }
                Column {
                    Text("Self Planned", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                    Text("₹33,500", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column {
                    Text("Potential Savings", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                    Text("₹8,500", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Plan independently. Travel smarter.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onViewSavings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("VIEW SAVINGS", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// 6. TRAVEL PREFERENCES
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileTravelPreferencesSection(
    selectedInterests: Set<String>,
    onToggleInterest: (String) -> Unit
) {
    Column {
        Text(
            text = "TRAVEL PREFERENCES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val options = listOf(
                "🌿 Nature", "🏔 Adventure", "🍴 Food", "📸 Photography",
                "🏛 Culture", "🛍 Shopping", "🏖 Beaches", "🏕 Camping",
                "🌙 Nightlife", "👨‍👩‍👧 Family", "🧘 Relaxation", "📜 History"
            )
            options.forEach { opt ->
                val isSelected = selectedInterests.contains(opt)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleInterest(opt) },
                    label = { Text(if (isSelected) "$opt ✓" else opt) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

// 7. TRAVEL STYLE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTravelStyleSection(
    selectedStyle: String,
    onStyleSelect: (String) -> Unit
) {
    Column {
        Text(
            text = "TRAVEL STYLE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Budget", "Balanced", "Comfort", "Luxury").forEach { style ->
                val isSelected = selectedStyle == style
                FilterChip(
                    selected = isSelected,
                    onClick = { onStyleSelect(style) },
                    label = { Text(if (isSelected) "$style ✓" else style) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}

// 8. SAVED PLACES SECTION
@Composable
fun ProfileSavedPlacesSection(
    savedPlaces: List<SavedPlace>,
    onToggleSave: (String) -> Unit,
    onPlaceClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MY SAVED PLACES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${savedPlaces.size} items",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (savedPlaces.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❤️", fontSize = 36.sp)
                    Text("No saved places yet.", fontWeight = FontWeight.Bold)
                    Text("Explore destinations and save your favorites.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(savedPlaces) { place ->
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .clickable { onPlaceClick(place.id) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Box {
                                SafeAsyncImage(
                                    imageUrl = place.imageUrl ?: "",
                                    contentDescription = place.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                                )
                                IconButton(
                                    onClick = { onToggleSave(place.id) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(32.dp)
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Unsave", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(place.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                    Text(" ${place.rating} • ${place.destination}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 9. MY TRIPS SUMMARY
@Composable
fun ProfileMyTripsCard(
    upcomingCount: Int = 2,
    completedCount: Int = 5,
    onViewAllTrips: () -> Unit
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
                Text("MY TRIPS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = onViewAllTrips) {
                    Text("VIEW ALL TRIPS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Upcoming: $upcomingCount", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("Completed: $completedCount", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Total: ${upcomingCount + completedCount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recent trip preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("🌄 Ooty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Chennai → Ooty  •  Aug 20 - Aug 23", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹8,500 estimated", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(onClick = onViewAllTrips, shape = RoundedCornerShape(10.dp)) {
                        Text("VIEW TRIP", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// 35. REUSABLE SETTINGS ROW COMPONENT
@Composable
fun ProfileSettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = textColor)
                    if (subtitle != null) {
                        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// 24. RATE APP DIALOG
@Composable
fun RateAppDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var rating by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("RATE TRIPDIRECT", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("How has your self-planning travel experience been?", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "$star Star",
                                tint = if (star <= rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Thank you for rating TripDirect $rating stars!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text("RATE NOW", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Later") }
        }
    )
}

// 25. SHARE TRIPDIRECT INTENT HELPER
fun shareTripDirect(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Plan your trips independently and avoid broker fees with TripDirect! Download now.")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share TripDirect"))
}
