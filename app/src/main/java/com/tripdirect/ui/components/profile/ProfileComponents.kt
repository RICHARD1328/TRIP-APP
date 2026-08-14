package com.tripdirect.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.UserProfile
import com.tripdirect.data.model.UserStats

@Composable
fun ProfileHeader(
    profile: UserProfile,
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profile.initials,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = profile.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = profile.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onEditProfileClick,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Edit Profile")
        }
    }
}

@Composable
fun TravelStatsCard(
    stats: UserStats,
    onTripsClick: () -> Unit,
    onSavedClick: () -> Unit,
    onItinerariesClick: () -> Unit,
    onReviewsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(
                    modifier = Modifier.weight(1f),
                    title = "My Trips",
                    count = "${stats.totalTrips}",
                    icon = Icons.Default.FlightTakeoff,
                    onClick = onTripsClick
                )
                StatTile(
                    modifier = Modifier.weight(1f),
                    title = "Saved Places",
                    count = "${stats.savedPlaces}",
                    icon = Icons.Default.Favorite,
                    onClick = onSavedClick
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(
                    modifier = Modifier.weight(1f),
                    title = "Itineraries",
                    count = "${stats.itineraries}",
                    icon = Icons.Default.Assignment,
                    onClick = onItinerariesClick
                )
                StatTile(
                    modifier = Modifier.weight(1f),
                    title = "My Reviews",
                    count = "${stats.reviews}",
                    icon = Icons.Default.Star,
                    onClick = onReviewsClick
                )
            }
        }
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = textColor)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Logout") },
        text = { Text("Are you sure you want to logout from TripDirect?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmLogout()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
