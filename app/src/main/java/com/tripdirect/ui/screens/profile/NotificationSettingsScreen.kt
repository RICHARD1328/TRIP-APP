package com.tripdirect.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.notificationSettings.collectAsState()

    var tripReminders by remember { mutableStateOf(settings.tripReminders) }
    var itineraryUpdates by remember { mutableStateOf(settings.itineraryUpdates) }
    var priceUpdates by remember { mutableStateOf(settings.priceUpdates) }
    var hotelUpdates by remember { mutableStateOf(settings.hotelUpdates) }
    var travelRecommendations by remember { mutableStateOf(settings.travelRecommendations) }
    var safetyAlerts by remember { mutableStateOf(settings.safetyAlerts) }
    var reviewReminders by remember { mutableStateOf(settings.reviewReminders) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotificationSwitchItem("Trip Reminders", "Get alerts about upcoming trip dates", tripReminders) {
                tripReminders = it
                viewModel.updateNotificationSettings(settings.copy(tripReminders = it))
            }
            NotificationSwitchItem("Itinerary Updates", "Alerts on route optimization & schedule changes", itineraryUpdates) {
                itineraryUpdates = it
                viewModel.updateNotificationSettings(settings.copy(itineraryUpdates = it))
            }
            NotificationSwitchItem("Price Updates", "Alerts when hotel or transport prices drop", priceUpdates) {
                priceUpdates = it
                viewModel.updateNotificationSettings(settings.copy(priceUpdates = it))
            }
            NotificationSwitchItem("Hotel Updates", "Booking status and check-in reminders", hotelUpdates) {
                hotelUpdates = it
                viewModel.updateNotificationSettings(settings.copy(hotelUpdates = it))
            }
            NotificationSwitchItem("Travel Recommendations", "Personalized recommendations for places", travelRecommendations) {
                travelRecommendations = it
                viewModel.updateNotificationSettings(settings.copy(travelRecommendations = it))
            }
            NotificationSwitchItem("Safety Alerts", "Real-time location and weather safety advisories", safetyAlerts) {
                safetyAlerts = it
                viewModel.updateNotificationSettings(settings.copy(safetyAlerts = it))
            }
            NotificationSwitchItem("Review Reminders", "Prompts to share feedback after completed visits", reviewReminders) {
                reviewReminders = it
                viewModel.updateNotificationSettings(settings.copy(reviewReminders = it))
            }
        }
    }
}

@Composable
private fun NotificationSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
