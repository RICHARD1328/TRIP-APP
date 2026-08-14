package com.tripdirect.ui.screens.hotels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
import com.tripdirect.ui.components.hotels.RatingBadge
import com.tripdirect.viewmodel.HotelSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    hotelId: String,
    viewModel: HotelSearchViewModel,
    onNavigateBack: () -> Unit,
    onAddToTrip: () -> Unit
) {
    val selectedHotel by viewModel.selectedHotel.collectAsState()
    val nights = viewModel.calculateNumberOfNights()
    val rooms = viewModel.rooms.collectAsState().value

    LaunchedEffect(hotelId) {
        viewModel.loadHotelDetails(hotelId)
    }

    val hotel = selectedHotel

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(hotel?.name ?: "Hotel Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (hotel != null) {
                Surface(tonalElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val total = hotel.pricePerNight * nights * rooms
                            val taxes = (total * 0.1).toLong()
                            Text(
                                text = "Estimated Total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${total + taxes}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(onClick = onAddToTrip) {
                            Text("Add to My Trip")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (hotel == null) {
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
                    // Image Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hotel.vectorIcon != null) {
                            Icon(imageVector = hotel.vectorIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                item {
                    Text(text = hotel.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "📍 ${hotel.location} • ${hotel.distanceFromCenter} km from center", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBadge(rating = hotel.rating)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${hotel.reviewCount} Reviews", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(text = "About this hotel", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = hotel.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(text = "Facilities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        hotel.facilities.forEach { facility ->
                            HotelFacilityChip(text = facility)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    Text(text = "Room Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = hotel.roomType, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = if (hotel.breakfastIncluded) "🍳 Breakfast Included" else "No Breakfast", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Cancellation Policy:", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            Text(text = hotel.cancellationPolicy, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(text = "Price Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val baseTotal = hotel.pricePerNight * nights * rooms
                            val taxes = (baseTotal * 0.1).toLong()
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "₹${hotel.pricePerNight} × $nights nights × $rooms room(s)", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "₹$baseTotal", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Taxes (estimated)", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "₹$taxes", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Estimated Total", fontWeight = FontWeight.Bold)
                                Text(text = "₹${baseTotal + taxes}", fontWeight = FontWeight.Bold)
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
                                Text("Plan Directly", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Compare accommodation options, understand the estimated cost, and choose where you want to stay. No forced package selection.",
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
