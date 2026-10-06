package com.tripdirect.ui.screens.safety

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.EmergencyServiceType
import com.tripdirect.ui.components.safety.NearbyServiceCard
import com.tripdirect.viewmodel.EmergencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyServicesScreen(
    viewModel: EmergencyViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRoute: () -> Unit
) {
    val context = LocalContext.current
    val services by viewModel.nearbyServices.collectAsState()
    val selectedFilter by viewModel.selectedFilterType.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filteredServices = if (selectedFilter == null) {
        services
    } else {
        services.filter { it.type == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Emergency Services", fontWeight = FontWeight.Bold) },
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
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    Pair(null, "All"),
                    Pair(EmergencyServiceType.HOSPITAL, "Hospitals"),
                    Pair(EmergencyServiceType.POLICE, "Police"),
                    Pair(EmergencyServiceType.PHARMACY, "Pharmacies"),
                    Pair(EmergencyServiceType.AMBULANCE, "Ambulance"),
                    Pair(EmergencyServiceType.FIRE, "Fire Station")
                )
                items(filters) { (type, label) ->
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = { viewModel.filterServices(type) },
                        label = { Text(label) }
                    )
                }
            }

            HorizontalDivider()

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (filteredServices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No services found in this category.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredServices) { service ->
                        NearbyServiceCard(
                            service = service,
                            onCallClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${service.phoneNumber}"))
                                context.startActivity(intent)
                            },
                            onDirectionsClick = onNavigateToRoute
                        )
                    }
                }
            }
        }
    }
}
