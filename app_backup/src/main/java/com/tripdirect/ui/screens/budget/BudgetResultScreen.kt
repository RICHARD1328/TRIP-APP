package com.tripdirect.ui.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.budget.BudgetProgressBar
import com.tripdirect.ui.components.budget.ExpenseBreakdownChart
import com.tripdirect.viewmodel.BudgetCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetResultScreen(
    viewModel: BudgetCalculatorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBreakdown: () -> Unit,
    onNavigateToCompare: () -> Unit
) {
    val breakdown by viewModel.budgetBreakdown.collectAsState()
    val destination by viewModel.destination.collectAsState()
    val days by viewModel.days.collectAsState()
    val adults by viewModel.adults.collectAsState()
    val children by viewModel.children.collectAsState()

    val result = breakdown
    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No budget data found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Trip Budget") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { /* Optimize */ }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Optimize")
                        }
                        OutlinedButton(onClick = onNavigateToCompare, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Compare")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* Save Budget */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Save Budget")
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = destination,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$days Days • ${adults + children} Travelers",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Estimated Trip Cost", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${result.total}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Your Budget:", style = MaterialTheme.typography.bodyLarge)
                            Text("₹${result.budget}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        BudgetProgressBar(breakdown = result)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (result.remaining >= 0) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Within Budget", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Text("₹${result.remaining} estimated remaining", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Over Budget", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                    Text("₹${-result.remaining} estimated excess", style = MaterialTheme.typography.bodySmall, color = Color(0xFFC62828))
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Expense Breakdown", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "View Details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToBreakdown() }.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ExpenseBreakdownChart(breakdown = result)
            }
            
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Per Person", style = MaterialTheme.typography.bodyMedium)
                            Text("₹${result.perPerson}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Per Day", style = MaterialTheme.typography.bodyMedium)
                            Text("₹${result.perDay}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
