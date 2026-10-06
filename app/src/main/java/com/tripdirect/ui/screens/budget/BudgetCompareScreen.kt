package com.tripdirect.ui.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.budget.ComparisonRow
import com.tripdirect.viewmodel.BudgetCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCompareScreen(
    viewModel: BudgetCalculatorViewModel,
    onNavigateBack: () -> Unit
) {
    val breakdown by viewModel.budgetBreakdown.collectAsState()
    
    val result = breakdown
    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No budget data found.")
        }
        return
    }

    // Mock Package Data (Usually fetched from repository)
    val packageTransport = (result.transport * 1.3).toInt()
    val packageHotel = (result.accommodation * 1.25).toInt()
    val packageFood = (result.food * 1.15).toInt()
    val packageActivities = (result.activities * 1.5).toInt()
    val packageAttractions = (result.attractions * 1.2).toInt()
    val packageLocalTransport = (result.localTransport * 1.2).toInt()
    val packageMisc = result.miscellaneous // usually same
    
    val packageTotal = packageTransport + packageHotel + packageFood + packageActivities + packageAttractions + packageLocalTransport + packageMisc

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Cost Comparison") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Why TripDirect?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Instead of showing only a package price, TripDirect breaks your trip into individual expenses so you can understand where your money goes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Category", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Text("Self Planned", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    Text("Package", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
            }

            item {
                ComparisonRow("Transport", result.transport, packageTransport)
                ComparisonRow("Hotel", result.accommodation, packageHotel)
                ComparisonRow("Food", result.food, packageFood)
                ComparisonRow("Activities", result.activities, packageActivities)
                ComparisonRow("Attractions", result.attractions, packageAttractions)
                ComparisonRow("Local Transport", result.localTransport, packageLocalTransport)
                ComparisonRow("Miscellaneous", result.miscellaneous, packageMisc)
                
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                ComparisonRow("Estimated Total", result.total, packageTotal, isHighlight = true)
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Potential Difference", style = MaterialTheme.typography.titleMedium, color = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("₹${packageTotal - result.total}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Package prices are illustrative in prototype mode.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
