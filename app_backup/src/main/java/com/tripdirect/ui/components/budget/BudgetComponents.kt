package com.tripdirect.ui.components.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.BudgetBreakdown

@Composable
fun BudgetCategoryCard(
    title: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }
}

@Composable
fun BudgetProgressBar(breakdown: BudgetBreakdown) {
    val progress = if (breakdown.budget > 0) {
        (breakdown.total.toFloat() / breakdown.budget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    
    val isOverBudget = breakdown.total > breakdown.budget
    val progressColor = if (isOverBudget) Color(0xFFC62828) else Color(0xFF2E7D32)
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Budget Used", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("₹${breakdown.total} / ₹${breakdown.budget}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
            color = progressColor,
            trackColor = trackColor
        )
    }
}

@Composable
fun ExpenseBreakdownChart(breakdown: BudgetBreakdown) {
    val total = breakdown.total.coerceAtLeast(1) // Avoid division by zero
    
    val categories = listOf(
        Pair("Accommodation", breakdown.accommodation to Color(0xFF3F51B5)),
        Pair("Food", breakdown.food to Color(0xFFFF9800)),
        Pair("Transport", breakdown.transport to Color(0xFF4CAF50)),
        Pair("Local Transport", breakdown.localTransport to Color(0xFF9C27B0)),
        Pair("Activities", breakdown.activities to Color(0xFF00BCD4)),
        Pair("Attractions", breakdown.attractions to Color(0xFFE91E63)),
        Pair("Misc", breakdown.miscellaneous to Color(0xFF9E9E9E))
    ).sortedByDescending { it.second.first } // Sort by amount descending

    Column(modifier = Modifier.fillMaxWidth()) {
        categories.filter { it.second.first > 0 }.forEach { (name, data) ->
            val amount = data.first
            val color = data.second
            val weight = (amount.toFloat() / total.toFloat()).coerceAtLeast(0.01f) // Ensure visibility
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, modifier = Modifier.weight(0.35f), style = MaterialTheme.typography.bodySmall)
                Box(
                    modifier = Modifier
                        .weight(0.65f)
                        .height(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(weight)
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonRow(label: String, selfPlanned: Int, packageCost: Int, isHighlight: Boolean = false) {
    val textStyle = if (isHighlight) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium
    val color = if (isHighlight) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = textStyle, color = color, modifier = Modifier.weight(1f))
        Text("₹$selfPlanned", style = textStyle, color = color, modifier = Modifier.weight(0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        Text("₹$packageCost", style = textStyle, color = color, modifier = Modifier.weight(0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}
