package com.tripdirect.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetPreferencesScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val prefs by viewModel.budgetPreferences.collectAsState()

    var maxBudget by remember { mutableFloatStateOf(prefs.maxTripBudget.toFloat()) }
    var dailyBudget by remember { mutableFloatStateOf(prefs.preferredDailyBudget.toFloat()) }
    var stayCategory by remember { mutableStateOf(prefs.stayCategory) }
    var foodCategory by remember { mutableStateOf(prefs.foodCategory) }

    val stayOptions = listOf(
        "Budget / Hostels (₹800 - ₹1,500)",
        "Comfort Hotels (₹1,500 - ₹3,500)",
        "Luxury Resorts (₹4,000+)"
    )

    val foodOptions = listOf(
        "Budget Local Eateries (₹300 - ₹600/day)",
        "Moderate Restaurants (₹600 - ₹1,200/day)",
        "Fine Dining (₹1,500+/day)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Preferences") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Preferred Maximum Trip Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("₹${maxBudget.toInt()}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Slider(
                value = maxBudget,
                onValueChange = { maxBudget = it },
                valueRange = 5000f..50000f,
                steps = 45
            )

            HorizontalDivider()

            Text("Daily Spending Limit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("₹${dailyBudget.toInt()} / day", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Slider(
                value = dailyBudget,
                onValueChange = { dailyBudget = it },
                valueRange = 1000f..10000f,
                steps = 18
            )

            HorizontalDivider()

            Text("Accommodation Tier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            stayOptions.forEach { opt ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    RadioButton(selected = stayCategory == opt, onClick = { stayCategory = opt })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(opt, style = MaterialTheme.typography.bodyMedium)
                }
            }

            HorizontalDivider()

            Text("Food & Dining Tier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            foodOptions.forEach { opt ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    RadioButton(selected = foodCategory == opt, onClick = { foodCategory = opt })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(opt, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateBudgetPreferences(
                        prefs.copy(
                            maxTripBudget = maxBudget.toDouble(),
                            preferredDailyBudget = dailyBudget.toDouble(),
                            stayCategory = stayCategory,
                            foodCategory = foodCategory
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Preferences")
            }
        }
    }
}
