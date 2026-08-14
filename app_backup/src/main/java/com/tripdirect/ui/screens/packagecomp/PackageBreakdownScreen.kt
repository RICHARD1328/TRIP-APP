package com.tripdirect.ui.screens.packagecomp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.PackageComparisonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageBreakdownScreen(
    viewModel: PackageComparisonViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBudget: () -> Unit
) {
    val comparison by viewModel.comparison.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itemized Cost Breakdown") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (comparison == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No comparison data loaded.")
            }
            return@Scaffold
        }

        val comp = comparison!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("${comp.destination} Cost Allocation Analysis", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tourist Package Total")
                        Text("₹${comp.touristPackage.totalPrice}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TripDirect Self-Plan Total")
                        Text("₹${comp.selfPlan.totalCost}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Savings", fontWeight = FontWeight.Bold)
                        Text("₹${comp.savingsAmount} (${comp.savingsPercentage}%)", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Line-Item Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider()

                    comp.costBreakdown.forEach { item ->
                        val diff = item.packageCost - item.selfPlanCost
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.categoryName, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (diff > 0) "Save ₹$diff" else if (diff < 0) "Extra ₹${-diff}" else "Equal",
                                    color = if (diff >= 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Package: ₹${item.packageCost}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Self-Plan: ₹${item.selfPlanCost}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                            HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Where Does the Money Go?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tourist package fees include travel agency operational costs, regional agent commissions (~₹${comp.touristPackage.hiddenBrokerFee}), and fixed group meal surcharges. TripDirect connects you directly to service providers, saving you ₹${comp.savingsAmount}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Button(
                onClick = onNavigateToBudget,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Budget Calculator")
            }
        }
    }
}
