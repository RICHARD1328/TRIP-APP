package com.tripdirect.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.*
import com.tripdirect.ui.components.profile.LogoutDialog
import com.tripdirect.viewmodel.AuthViewModel
import com.tripdirect.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel? = null,
    onLogout: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToTrips: () -> Unit = {},
    onNavigateToSavedPlaces: () -> Unit = {},
    onNavigateToItineraries: () -> Unit = {},
    onNavigateToReviews: () -> Unit = {},
    onNavigateToTravelPreferences: () -> Unit = {},
    onNavigateToBudgetPreferences: () -> Unit = {},
    onNavigateToEmergencyContacts: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToHelpSupport: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToComparison: () -> Unit = {},
    onNavigateToPlaceDetails: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // State bindings
    var userProfile by remember {
        mutableStateOf(
            UserProfile(
                name = "Richard Ruban Kumar",
                email = "richardrubankumar.cse2024@citchennai.net",
                phone = "+91 98765 43210",
                homeLocation = "Chennai, Tamil Nadu"
            )
        )
    }

    val userStats = remember {
        UserStats(totalTrips = 7, upcomingTrips = 2, completedTrips = 5, placesVisited = 12, totalSaved = 8500, favorites = 18)
    }

    val savedPlaces = remember {
        mutableStateListOf(
            SavedPlace("s1", "Ooty Lake", "Ooty, Tamil Nadu", 4.5f, "Nature", TripImages.OOTY_LAKE),
            SavedPlace("s2", "Tea Gardens", "Ooty, Tamil Nadu", 4.7f, "Nature", TripImages.TEA_GARDEN),
            SavedPlace("s3", "Doddabetta Peak", "Ooty, Tamil Nadu", 4.6f, "Viewpoint", TripImages.DODDABETTA),
            SavedPlace("s4", "Coonoor", "Coonoor, Tamil Nadu", 4.4f, "Hills", TripImages.OOTY)
        )
    }

    var selectedInterests by remember { mutableStateOf(setOf("🌿 Nature", "🍴 Food", "📸 Photography", "🏛 Culture")) }
    var selectedStyle by remember { mutableStateOf("Balanced") }

    // Settings Toggle States
    var tripReminders by remember { mutableStateOf(true) }
    var priceAlerts by remember { mutableStateOf(true) }
    var weatherAlerts by remember { mutableStateOf(true) }
    var safetyAlerts by remember { mutableStateOf(true) }
    var useCurrentLocation by remember { mutableStateOf(true) }

    // Dialog Visibility
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var showEditProfileModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showRateDialog = true }) {
                        Icon(Icons.Default.StarBorder, contentDescription = "Rate App")
                    }
                    IconButton(onClick = { shareTripDirect(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "Share App")
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
            // 1. Profile Header
            item {
                ProfileHeaderCard(
                    profile = userProfile,
                    onEditProfileClick = { showEditProfileModal = true }
                )
            }

            // 2. Travel Statistics
            item {
                ProfileStatisticsCard(
                    stats = userStats,
                    onTripsClick = onNavigateToTrips,
                    onSavedClick = onNavigateToSavedPlaces
                )
            }

            // 3. Travel Savings Card
            item {
                TravelSavingsCard(
                    onViewSavings = onNavigateToComparison
                )
            }

            // 4. Travel Preferences
            item {
                ProfileTravelPreferencesSection(
                    selectedInterests = selectedInterests,
                    onToggleInterest = { interest ->
                        val current = selectedInterests.toMutableSet()
                        if (current.contains(interest)) current.remove(interest) else current.add(interest)
                        selectedInterests = current
                    }
                )
            }

            // 5. Travel Style
            item {
                ProfileTravelStyleSection(
                    selectedStyle = selectedStyle,
                    onStyleSelect = { selectedStyle = it }
                )
            }

            // 6. Saved Places
            item {
                ProfileSavedPlacesSection(
                    savedPlaces = savedPlaces,
                    onToggleSave = { id -> savedPlaces.removeAll { it.id == id } },
                    onPlaceClick = { id -> onNavigateToPlaceDetails(id) }
                )
            }

            // 7. My Trips Summary
            item {
                ProfileMyTripsCard(
                    upcomingCount = userStats.upcomingTrips,
                    completedCount = userStats.completedTrips,
                    onViewAllTrips = onNavigateToTrips
                )
            }

            // 8. Budget Preferences Section
            item {
                Column {
                    Text("BUDGET PREFERENCES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    ProfileSettingsRow(icon = Icons.Default.AccountBalanceWallet, title = "Daily Budget", subtitle = "₹2,500 / day", onClick = onNavigateToBudgetPreferences)
                    ProfileSettingsRow(icon = Icons.Default.MonetizationOn, title = "Maximum Trip Budget", subtitle = "₹15,000 max limit", onClick = onNavigateToBudgetPreferences)
                    ProfileSettingsRow(icon = Icons.Default.Hotel, title = "Preferred Hotel Budget", subtitle = "₹1,500 / night", onClick = onNavigateToBudgetPreferences)
                    ProfileSettingsRow(icon = Icons.Default.DirectionsBus, title = "Preferred Transport", subtitle = "Bus • Train • Cab", onClick = onNavigateToBudgetPreferences)
                }
            }

            // 9. Default Travel Settings Section
            item {
                Column {
                    Text("DEFAULT TRAVEL SETTINGS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    ProfileSettingsRow(icon = Icons.Default.Place, title = "Starting Location", badgeText = "Chennai", onClick = {})
                    ProfileSettingsRow(icon = Icons.Default.Group, title = "Default Travelers", badgeText = "2 Adults", onClick = {})
                    ProfileSettingsRow(icon = Icons.Default.AttachMoney, title = "Preferred Currency", badgeText = "INR ₹", onClick = onNavigateToLanguage)
                    ProfileSettingsRow(icon = Icons.Default.Language, title = "Preferred Language", badgeText = "English", onClick = onNavigateToLanguage)
                }
            }

            // 10. Packing Lists Section
            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("MY PACKING LISTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        TextButton(onClick = {}) { Text("VIEW LISTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    }
                    ProfileSettingsRow(icon = Icons.Default.Luggage, title = "Weekend Trip", subtitle = "6 / 8 items packed", onClick = {})
                    ProfileSettingsRow(icon = Icons.Default.BeachAccess, title = "Beach Trip", subtitle = "5 / 7 items packed", onClick = {})
                }
            }

            // 11. Travel Documents Section
            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("TRAVEL DOCUMENTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        TextButton(onClick = {}) { Text("MANAGE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    }
                    ProfileSettingsRow(icon = Icons.Default.Description, title = "Tickets & Reservations", badgeText = "Local Storage", onClick = {})
                    ProfileSettingsRow(icon = Icons.Default.Badge, title = "ID Proof & Insurance", badgeText = "Encrypted", onClick = {})
                }
            }

            // 12. Notifications Section
            item {
                Column {
                    Text("NOTIFICATIONS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Trip Reminders", style = MaterialTheme.typography.bodyMedium)
                                Switch(checked = tripReminders, onCheckedChange = { tripReminders = it })
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Hotel Price Alerts", style = MaterialTheme.typography.bodyMedium)
                                Switch(checked = priceAlerts, onCheckedChange = { priceAlerts = it })
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Weather Alerts", style = MaterialTheme.typography.bodyMedium)
                                Switch(checked = weatherAlerts, onCheckedChange = { weatherAlerts = it })
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Safety Alerts", style = MaterialTheme.typography.bodyMedium)
                                Switch(checked = safetyAlerts, onCheckedChange = { safetyAlerts = it })
                            }
                        }
                    }
                }
            }

            // 13. Location Settings Section
            item {
                Column {
                    Text("LOCATION SETTINGS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Use Current Location", style = MaterialTheme.typography.bodyMedium)
                                Text("Automates starting point search", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = useCurrentLocation, onCheckedChange = { useCurrentLocation = it })
                        }
                    }
                }
            }

            // 14. Privacy & Security Section
            item {
                Column {
                    Text("ACCOUNT SECURITY & PRIVACY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    ProfileSettingsRow(icon = Icons.Default.VerifiedUser, title = "Account Security Status", badgeText = "Email & Phone Verified", onClick = onNavigateToPrivacy)
                    ProfileSettingsRow(icon = Icons.Default.Lock, title = "Change Password & Biometrics", onClick = onNavigateToPrivacy)
                    ProfileSettingsRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Delete Account",
                        subtitle = "Permanently remove account & profile data",
                        iconTint = MaterialTheme.colorScheme.error,
                        textColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteAccountDialog = true }
                    )
                }
            }

            // 15. Safety Center Shortcut
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1F24))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFF5252))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("SAFETY CENTER", fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF5252))
                                Text("Emergency contacts & SOS helpline", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                        Button(
                            onClick = onNavigateToEmergencyContacts,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                        ) {
                            Text("OPEN", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // 16. Help & Support
            item {
                Column {
                    Text("HELP & SUPPORT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    ProfileSettingsRow(icon = Icons.AutoMirrored.Filled.HelpOutline, title = "FAQ & How TripDirect Works", onClick = onNavigateToHelpSupport)
                    ProfileSettingsRow(icon = Icons.Default.SupportAgent, title = "Contact Support & Feedback", onClick = onNavigateToHelpSupport)
                }
            }

            // 17. About TripDirect
            item {
                Column {
                    Text("ABOUT TRIPDIRECT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    ProfileSettingsRow(icon = Icons.Default.Info, title = "About Us & Vision", badgeText = "v1.0.0", onClick = onNavigateToAbout)
                    ProfileSettingsRow(icon = Icons.Default.Policy, title = "Terms & Privacy Policy", onClick = onNavigateToAbout)
                }
            }

            // 18. Logout Button
            item {
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG OUT", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
    }

    // Dialogs
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirmLogout = {
                authViewModel?.logout(context) { onLogout() } ?: run {
                    viewModel.logout()
                    onLogout()
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete your account? All saved trips and preferences will be permanently removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        Toast.makeText(context, "Account deletion request sent", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("DELETE ACCOUNT", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("CANCEL") }
            }
        )
    }

    if (showRateDialog) {
        RateAppDialog(onDismiss = { showRateDialog = false })
    }

    // Edit Profile Modal
    if (showEditProfileModal) {
        var editName by remember { mutableStateOf(userProfile.name) }
        var editEmail by remember { mutableStateOf(userProfile.email) }
        var editPhone by remember { mutableStateOf(userProfile.phone) }

        AlertDialog(
            onDismissRequest = { showEditProfileModal = false },
            title = { Text("EDIT PROFILE", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = editEmail, onValueChange = { editEmail = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editEmail.isNotBlank()) {
                            userProfile = userProfile.copy(name = editName, email = editEmail, phone = editPhone)
                            showEditProfileModal = false
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileModal = false }) { Text("Cancel") }
            }
        )
    }
}
