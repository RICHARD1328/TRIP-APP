package com.tripdirect.ui.components.packagecomp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.ComparisonFeature
import com.tripdirect.data.model.FeatureAdvantage
import com.tripdirect.data.model.ItemCostBreakdown
import com.tripdirect.data.model.PackageComparison

@Composable
fun ComparisonHeader(destination: String, days: Int, travelers: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$destination Package Comparison",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$days Days • $travelers Travelers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            AssistChip(
                onClick = {},
                label = { Text("Transparent Analysis") },
                leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

@Composable
fun SavingsCard(savingsAmount: Int, savingsPercentage: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF2E7D32)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "💰 Estimated Savings: ₹$savingsAmount",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "$savingsPercentage% lower estimated cost with TripDirect Self-Plan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
fun CostVsPackageSummary(comp: PackageComparison) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tourist Package Side
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tourist Package", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${comp.touristPackage.totalPrice}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text("₹${comp.touristPackage.pricePerPerson}/person", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Includes Agent Fees & Rigid Schedules", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Self Planned Side
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TripDirect Self-Plan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${comp.selfPlan.totalCost}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("₹${comp.selfPlan.totalCost / comp.travelersCount}/person", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("0% Broker Fees • 100% Flexible", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CategoryTabRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Cost", "Stay", "Transport", "Attractions", "Flexibility", "Cancellation", "Hidden Costs")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
fun ComparisonFeatureCard(feature: ComparisonFeature) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(feature.featureName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = when (feature.advantage) {
                                FeatureAdvantage.SELF_PLAN -> "✓ Self-Plan Advantage"
                                FeatureAdvantage.PACKAGE -> "✓ Package Advantage"
                                FeatureAdvantage.NEUTRAL -> "Equal Value"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (feature.advantage) {
                            FeatureAdvantage.SELF_PLAN -> Color(0xFFE8F5E9)
                            FeatureAdvantage.PACKAGE -> MaterialTheme.colorScheme.secondaryContainer
                            FeatureAdvantage.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Package side
                Column(modifier = Modifier.weight(1f)) {
                    Text("Package", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(feature.packageValue, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }

                HorizontalDivider(modifier = Modifier.height(30.dp).width(1.dp))

                // Self Plan side
                Column(modifier = Modifier.weight(1f)) {
                    Text("TripDirect Self-Plan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(feature.selfPlanValue, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Analysis & Notes:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text(feature.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ItemizedCostBarChart(breakdown: List<ItemCostBreakdown>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Category Cost Allocation (Package vs Self)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            breakdown.forEach { item ->
                val maxCost = maxOf(item.packageCost, item.selfPlanCost).coerceAtLeast(1)
                val packageRatio = item.packageCost.toFloat() / maxCost
                val selfRatio = item.selfPlanCost.toFloat() / maxCost

                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.categoryName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("Pkg: ₹${item.packageCost} vs Self: ₹${item.selfPlanCost}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Package bar
                    LinearProgressIndicator(
                        progress = { packageRatio },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Self bar
                    LinearProgressIndicator(
                        progress = { selfRatio },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CustomizationDialog(
    currentStay: Int,
    currentTransport: Int,
    onDismiss: () -> Unit,
    onApply: (stay: Int, transport: Int) -> Unit
) {
    var stay by remember { mutableStateOf(currentStay.toString()) }
    var transport by remember { mutableStateOf(currentTransport.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Self-Plan Estimate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = stay,
                    onValueChange = { stay = it },
                    label = { Text("Hotel & Stay Budget (₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = transport,
                    onValueChange = { transport = it },
                    label = { Text("Transport Budget (₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(stay.toIntOrNull() ?: currentStay, transport.toIntOrNull() ?: currentTransport)
                    onDismiss()
                }
            ) {
                Text("Recalculate Savings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
