package com.tripdirect.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.data.model.Trip

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (onSeeAllClick != null) {
            TextButton(onClick = onSeeAllClick) {
                Text("See All →")
            }
        }
    }
}

@Composable
fun AIPlannerBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI TRIP PLANNER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Plan your complete trip\nbased on your:",
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val features = listOf("Budget", "Duration", "Interests", "Travel preferences")
            features.forEach { feature ->
                Text(
                    text = "✓ $feature",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Plan My Trip")
            }
        }
    }
}

@Composable
fun BudgetBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "\uD83D\uDCB0 Travel Smart. Spend Less.",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Know your estimated trip cost\nbefore you travel.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Calculate My Budget")
            }
        }
    }
}

@Composable
fun PackageComparisonCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "\uD83D\uDCA1 WHY PAY MORE?",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tour Package", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹12,000", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Self Planned", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹8,500", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Potential Saving", fontSize = 12.sp, color = Color(0xFF2E7D32))
                    Text("₹3,500", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Plan your trip independently\nand keep control of your budget.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compare Now")
            }
        }
    }
}

@Composable
fun ContinueTripCard(
    trip: Trip?,
    onContinueClick: () -> Unit,
    onPlanNewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (trip != null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "${trip.destinationName} Trip",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${trip.durationDays} Days • ${trip.travelersCount} Travelers\nBudget: ₹${trip.budget}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Progress", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(trip.progressPercentage / 100f)
                                .height(8.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${trip.progressPercentage}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onContinueClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue Planning")
                }
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No active trips yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onPlanNewClick) {
                    Text("Plan Your First Trip")
                }
            }
        }
    }
}
