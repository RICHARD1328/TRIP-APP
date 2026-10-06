package com.tripdirect.ui.screens.transport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.transport.TransportCard
import com.tripdirect.viewmodel.TransportSearchViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportSearchScreen(
    viewModel: TransportSearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToCompare: () -> Unit
) {
    val fromLocation by viewModel.fromLocation.collectAsState()
    val toLocation by viewModel.toLocation.collectAsState()
    val travelDate by viewModel.travelDate.collectAsState()
    val travelers by viewModel.travelers.collectAsState()
    val options by viewModel.filteredOptions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val compareList by viewModel.selectedTransportsForCompare.collectAsState()
    
    var showSearchForm by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transport Search") },
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
            
            if (options.isNotEmpty()) {
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
                        Icon(Icons.Default.DirectionsBus, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("$fromLocation → $toLocation", fontWeight = FontWeight.Bold)
                            Text("${travelDate ?: "-"} • $travelers Traveler(s)", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = showSearchForm || options.isEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Find the best way to travel", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Compare price, time and transport options.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = fromLocation,
                        onValueChange = { viewModel.setFromLocation(it) },
                        label = { Text("From") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        IconButton(onClick = { viewModel.swapLocations() }) {
                            Icon(Icons.Default.SwapVert, contentDescription = "Swap starting point and destination")
                        }
                    }
                    
                    OutlinedTextField(
                        value = toLocation,
                        onValueChange = { viewModel.setToLocation(it) },
                        label = { Text("To") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.setTravelDate(LocalDate.now().plusDays(2)) }, // Mock date picker
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(travelDate?.toString() ?: "Travel Date")
                        }
                        
                        OutlinedButton(
                            onClick = { viewModel.setTravelers(2, 0) }, // Mock guest picker
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$travelers Traveler(s)")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (errorMessage != null) {
                        com.tripdirect.ui.components.common.NetworkErrorBanner(
                            title = if (isOffline) "No internet connection" else "Transport Search Failed",
                            message = errorMessage!!,
                            isOffline = isOffline,
                            isRetrying = isLoading,
                            onRetry = { viewModel.retrySearch() }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    Button(
                        onClick = {
                            viewModel.searchTransport()
                            showSearchForm = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Search Transport")
                        }
                    }
                }
            }
            
            if (options.isNotEmpty() && !showSearchForm) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${options.size} options found", fontWeight = FontWeight.SemiBold)
                    Row {
                        TextButton(onClick = { /* Open Filter Sheet */ }) { Text("Filter") }
                        TextButton(onClick = { /* Open Sort Sheet */ }) { Text("Sort") }
                    }
                }
                
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(options) { option ->
                        TransportCard(
                            option = option,
                            estimatedTotal = viewModel.getEstimatedTotal(option),
                            isSelectedForCompare = compareList.any { it.id == option.id },
                            onClick = { onNavigateToDetails(option.id) },
                            onCompareClick = { viewModel.toggleCompare(option) }
                        )
                    }
                }
            } else if (!isLoading && !showSearchForm && options.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (errorMessage != null) {
                        com.tripdirect.ui.components.common.NetworkErrorCard(
                            title = if (isOffline) "No internet connection" else "Transport Search Failed",
                            message = errorMessage!!,
                            isOffline = isOffline,
                            isRetrying = isLoading,
                            onRetry = { viewModel.retrySearch() }
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🚍", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No transport options found.", style = MaterialTheme.typography.titleLarge)
                            Text("Try another date or destination.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
}
