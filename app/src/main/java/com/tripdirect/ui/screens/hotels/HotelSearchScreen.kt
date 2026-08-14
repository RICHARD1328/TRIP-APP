package com.tripdirect.ui.screens.hotels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.hotels.HotelCard
import com.tripdirect.viewmodel.HotelSearchViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchScreen(
    viewModel: HotelSearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHotelDetails: (String) -> Unit,
    onNavigateToCompare: () -> Unit
) {
    val destination by viewModel.destination.collectAsState()
    val checkInDate by viewModel.checkInDate.collectAsState()
    val checkOutDate by viewModel.checkOutDate.collectAsState()
    val adults by viewModel.adults.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val hotels by viewModel.filteredHotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val compareList by viewModel.selectedHotelsForCompare.collectAsState()
    
    val nights = viewModel.calculateNumberOfNights()
    
    var showSearchForm by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotel Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (compareList.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCompare,
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    text = { Text("Compare (${compareList.size}/3)") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            // Toggle form visibility if results exist
            if (hotels.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { showSearchForm = !showSearchForm },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(if (destination.isNotBlank()) destination else "Anywhere", fontWeight = FontWeight.Bold)
                            Text("${checkInDate ?: "-"} to ${checkOutDate ?: "-"} • $adults Guests • $rooms Room(s)", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = showSearchForm || hotels.isEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Find your perfect stay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Compare hotels and choose what works best for your budget.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { viewModel.setDestination(it) },
                        label = { Text("Where are you going?") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.setCheckInDate(LocalDate.now().plusDays(1)) }, // Mock date picker
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(checkInDate?.toString() ?: "Check-in")
                        }
                        OutlinedButton(
                            onClick = { viewModel.setCheckOutDate(LocalDate.now().plusDays(4)) }, // Mock date picker
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(checkOutDate?.toString() ?: "Check-out")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { viewModel.setGuests(2, 0, 1) }, // Mock guest picker
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$adults Guests, $rooms Room(s)")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    
                    Button(
                        onClick = {
                            viewModel.searchHotels()
                            showSearchForm = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Search Hotels")
                        }
                    }
                }
            }
            
            if (hotels.isNotEmpty() && !showSearchForm) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${hotels.size} hotels found", fontWeight = FontWeight.SemiBold)
                    Row {
                        TextButton(onClick = { /* Open Filter Sheet */ }) { Text("Filter") }
                        TextButton(onClick = { /* Open Sort Sheet */ }) { Text("Sort") }
                    }
                }
                
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(hotels) { hotel ->
                        HotelCard(
                            hotel = hotel,
                            nights = nights,
                            rooms = rooms,
                            isSelectedForCompare = compareList.any { it.id == hotel.id },
                            onClick = { onNavigateToHotelDetails(hotel.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(hotel.id) },
                            onCompareClick = { viewModel.toggleCompare(hotel) }
                        )
                    }
                }
            } else if (!isLoading && !showSearchForm && hotels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏨", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No hotels found", style = MaterialTheme.typography.titleLarge)
                        Text("Try changing your dates or filters", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.clearFilters(); showSearchForm = true }) {
                            Text("Change Search")
                        }
                    }
                }
            }
        }
    }
}
