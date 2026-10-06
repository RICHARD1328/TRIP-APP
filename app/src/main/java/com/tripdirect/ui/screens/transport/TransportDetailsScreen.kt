package com.tripdirect.ui.screens.transport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.ui.components.hotels.HotelFacilityChip
import com.tripdirect.viewmodel.TransportSearchViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransportDetailsScreen(
    transportId: String,
    viewModel: TransportSearchViewModel,
    onNavigateBack: () -> Unit,
    onViewRoute: () -> Unit,
    onAddToTrip: () -> Unit
) {
    val transport by viewModel.selectedTransport.collectAsState()
    val travelers by viewModel.travelers.collectAsState()

    LaunchedEffect(transportId) {
        viewModel.loadTransportDetails(transportId)
    }

    val option = transport

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(option?.providerName ?: "Transport Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (option != null) {
                Surface(tonalElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${viewModel.getEstimatedTotal(option)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = onViewRoute) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Route")
                            }
                            Button(onClick = onAddToTrip) {
                                Text("Select Option")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (option == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(text = "Journey Details", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Journey Timeline
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(option.departureTime, fontWeight = FontWeight.Bold)
                                Text(option.from, style = MaterialTheme.typography.bodyMedium)
                                
                                Icon(Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.padding(vertical = 8.dp), tint = MaterialTheme.colorScheme.primary)
                                
                                Text("${option.durationMinutes / 60}h ${if (option.durationMinutes % 60 > 0) "${option.durationMinutes % 60}m" else ""}", 
                                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                
                                Icon(Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.padding(vertical = 8.dp), tint = MaterialTheme.colorScheme.primary)
                                
                                Text(option.arrivalTime, fontWeight = FontWeight.Bold)
                                Text(option.to, style = MaterialTheme.typography.bodyMedium)
                            }
                            
                            Spacer(modifier = Modifier.width(32.dp))
                            
                            Column {
                                Text("Transport", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(option.providerName, fontWeight = FontWeight.SemiBold)
                                Text(option.type.name, style = MaterialTheme.typography.bodySmall)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text("Travelers", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$travelers Person(s)", fontWeight = FontWeight.SemiBold)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text("Rating", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("⭐ ${option.rating}", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    Text(text = "Facilities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        option.facilities.forEach { facility ->
                            HotelFacilityChip(text = facility) // Reusing chip from hotels
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    Text(text = "Route Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = option.routeDescription, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(text = "Price Transparency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Base Fare (${option.pricingType.name.lowercase().replace("_", " ")})", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "₹${option.price}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val multiplier = if (option.pricingType == com.tripdirect.data.model.PricingType.PER_PERSON) travelers else 1
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Multiplier", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "× $multiplier", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Estimated Total", fontWeight = FontWeight.Bold)
                                Text(text = "₹${viewModel.getEstimatedTotal(option)}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Broker free message
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Travel Your Way", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Compare transport options, understand the estimated cost, and choose what works for you. No forced transport package.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
