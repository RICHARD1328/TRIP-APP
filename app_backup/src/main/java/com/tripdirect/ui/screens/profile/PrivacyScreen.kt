package com.tripdirect.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.profile.ProfileMenuItem
import com.tripdirect.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Security", fontWeight = FontWeight.Bold) },
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
        ) {
            ProfileMenuItem(
                icon = Icons.Default.Lock,
                title = "Account Security",
                subtitle = "Manage password & login security",
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Place,
                title = "Location Permissions",
                subtitle = "Only used during active navigation & SOS",
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Security,
                title = "Privacy Policy",
                subtitle = "Read how TripDirect protects traveler data",
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.DeleteForever,
                title = "Delete Account & Data",
                subtitle = "Permanently remove your account and trips",
                iconTint = MaterialTheme.colorScheme.error,
                textColor = MaterialTheme.colorScheme.error,
                onClick = {}
            )
        }
    }
}
