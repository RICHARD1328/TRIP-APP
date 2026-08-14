package com.tripdirect.ui.screens.route

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.route.FilterChipsRow
import com.tripdirect.ui.components.route.LocationBottomSheet
import com.tripdirect.ui.components.route.MockMapView
import com.tripdirect.ui.components.route.OptimizationBanner
import com.tripdirect.ui.components.route.RouteSummaryCard
import com.tripdirect.ui.components.route.RouteWarningCard
import com.tripdirect.ui.components.route.StopList
import com.tripdirect.ui.components.route.TransportModeSelector
import com.tripdirect.viewmodel.BudgetCalculatorViewModel
import com.tripdirect.viewmodel.RouteMapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteMapScreen(
    viewModel: RouteMapViewModel,
    budgetViewModel: BudgetCalculatorViewModel? = null,
    onNavigateBack: () -> Unit,
    onStartNavigation: () -> Unit,
    onNavigateToDetails: () -> Unit
) {
    val context = LocalContext.current
    val route by viewModel.currentRoute.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val transportMode by viewModel.selectedTransportMode.collectAsState()
    val selectedFilter by viewModel.selectedFilterType.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isOptimizing by viewModel.isOptimizing.collectAsState()
    val optimizationResult by viewModel.optimizationResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.searchNearRoute(it) },
                                placeholder = { Text("Search places near route...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        } else {
                            Column {
                                Text("Ooty Route Map", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("3 Days • 2 Travelers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isSearchActive) {
                                isSearchActive = false
                                viewModel.searchNearRoute("")
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(if (isSearchActive) Icons.Default.Close else Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { viewModel.saveRoute() }) {
                            Icon(if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = "Save Route")
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, viewModel.getShareableText())
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Trip Route"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                )
                FilterChipsRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { viewModel.filterByLocationType(it) }
                )
            }
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    TransportModeSelector(
                        selectedMode = transportMode,
                        onModeSelected = { viewModel.setTransportMode(it) }
                    )

                    if (route != null) {
                        RouteSummaryCard(route = route!!)
                        RouteWarningCard(totalDurationMinutes = route!!.totalDurationMinutes)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.optimizeRoute() },
                            modifier = Modifier.weight(1f),
                            enabled = !isOptimizing
                        ) {
                            if (isOptimizing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Optimize")
                            }
                        }
                        OutlinedButton(onClick = onNavigateToDetails, modifier = Modifier.weight(1f)) {
                            Text("View Details")
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (budgetViewModel != null && route != null) {
                            OutlinedButton(
                                onClick = { viewModel.addTransportCostToBudget(budgetViewModel) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Cost")
                            }
                        }

                        Button(
                            onClick = onStartNavigation,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Trip")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                optimizationResult?.let { result ->
                    OptimizationBanner(
                        result = result,
                        onDismiss = { viewModel.dismissOptimizationBanner() }
                    )
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                val err = errorMessage
                if (err != null) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    MockMapView(
                        route = route,
                        selectedLocation = selectedLocation,
                        userLocation = userLocation,
                        onLocationClick = { viewModel.selectLocation(it) },
                        onEnableUserLocation = { viewModel.enableLocationPermission() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                val currentRoute = route
                if (currentRoute != null && searchResults.isEmpty() && !isSearchActive) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        StopList(
                            route = currentRoute,
                            onSelectStop = { viewModel.selectLocation(it) }
                        )
                    }
                }
            }

            // Search Results Overlay
            if (isSearchActive && searchResults.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 6.dp
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                        items(searchResults) { place ->
                            ListItem(
                                headlineContent = { Text(place.name) },
                                supportingContent = { Text(place.address) },
                                modifier = Modifier.clickable {
                                    viewModel.selectLocation(place)
                                    isSearchActive = false
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (selectedLocation != null) {
        LocationBottomSheet(
            location = selectedLocation!!,
            onDismiss = { viewModel.selectLocation(null) },
            onNavigate = {
                viewModel.selectLocation(null)
                onStartNavigation()
            }
        )
    }
}

