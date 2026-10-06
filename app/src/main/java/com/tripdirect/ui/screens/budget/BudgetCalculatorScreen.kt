package com.tripdirect.ui.screens.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.budget.BudgetCategoryCard
import com.tripdirect.viewmodel.BudgetCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCalculatorScreen(
    viewModel: BudgetCalculatorViewModel,
    onNavigateBack: () -> Unit,
    onCalculate: () -> Unit
) {
    val destination by viewModel.destination.collectAsState()
    val days by viewModel.days.collectAsState()
    val adults by viewModel.adults.collectAsState()
    val children by viewModel.children.collectAsState()
    val totalBudget by viewModel.totalBudget.collectAsState()
    val transportCost by viewModel.transportCost.collectAsState()
    val accommodationPerNight by viewModel.accommodationPerNight.collectAsState()
    val foodPerPersonPerDay by viewModel.foodPerPersonPerDay.collectAsState()
    val attractionCost by viewModel.attractionCost.collectAsState()
    val activityCost by viewModel.activityCost.collectAsState()
    val localTransportPerDay by viewModel.localTransportPerDay.collectAsState()
    val miscellaneousCost by viewModel.miscellaneousCost.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        // Optionally load suggested budget here if destination is empty
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Calculator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = {
                            viewModel.calculateBudget {
                                onCalculate()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate My Budget", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Plan your trip budget", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Know your estimated expenses before you travel.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compare your expected expenses before choosing a package or booking services.", color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = destination,
                    onValueChange = { viewModel.setDestination(it) },
                    label = { Text("Destination") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = if (days == 0) "" else days.toString(),
                        onValueChange = { viewModel.setDays(it.toIntOrNull() ?: 0) },
                        label = { Text("Days") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = if (adults == 0) "" else adults.toString(),
                        onValueChange = { viewModel.setAdults(it.toIntOrNull() ?: 0) },
                        label = { Text("Travelers (Adults)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = if (totalBudget == 0) "" else totalBudget.toString(),
                    onValueChange = { viewModel.setTotalBudget(it) },
                    label = { Text("What's your budget? (Total)") },
                    leadingIcon = { Text("₹", modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Text("Expenses", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }

            item {
                BudgetCategoryCard(
                    title = "Transportation",
                    description = "Travel to destination (Total for all travelers)",
                    value = if (transportCost == 0) "" else transportCost.toString(),
                    onValueChange = { viewModel.setTransportCost(it) },
                    icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )
            }

            item {
                BudgetCategoryCard(
                    title = "Accommodation",
                    description = "Price per night",
                    value = if (accommodationPerNight == 0) "" else accommodationPerNight.toString(),
                    onValueChange = { viewModel.setAccommodationPerNight(it) },
                    icon = { Icon(Icons.Default.Hotel, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )
            }

            item {
                BudgetCategoryCard(
                    title = "Food",
                    description = "Estimated food cost per person/day",
                    value = if (foodPerPersonPerDay == 0) "" else foodPerPersonPerDay.toString(),
                    onValueChange = { viewModel.setFoodPerPersonPerDay(it) },
                    icon = { Icon(Icons.Default.LocalDining, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )
            }

            item {
                BudgetCategoryCard(
                    title = "Attractions & Entry Fees",
                    description = "Estimated total for all attractions",
                    value = if (attractionCost == 0) "" else attractionCost.toString(),
                    onValueChange = { viewModel.setAttractionCost(it) },
                    icon = { Icon(Icons.Default.LocalActivity, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )
            }

            item {
                BudgetCategoryCard(
                    title = "Activities",
                    description = "Estimated cost (Adventure, Safaris, etc.)",
                    value = if (activityCost == 0) "" else activityCost.toString(),
                    onValueChange = { viewModel.setActivityCost(it) },
                    icon = { Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )
            }

            item {
                BudgetCategoryCard(
                    title = "Local Transport",
                    description = "Daily estimate (Auto, Taxi, Rental)",
                    value = if (localTransportPerDay == 0) "" else localTransportPerDay.toString(),
                    onValueChange = { viewModel.setLocalTransportPerDay(it) },
                    icon = { Icon(Icons.Default.LocalTaxi, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )
            }

            item {
                BudgetCategoryCard(
                    title = "Miscellaneous",
                    description = "Shopping, Emergency, Tips",
                    value = if (miscellaneousCost == 0) "" else miscellaneousCost.toString(),
                    onValueChange = { viewModel.setMiscellaneousCost(it) },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )
            }
        }
    }
}
