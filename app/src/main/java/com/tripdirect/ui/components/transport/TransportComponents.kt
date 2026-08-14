package com.tripdirect.ui.components.transport

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.data.model.TransportOption
import com.tripdirect.data.model.TransportType
import com.tripdirect.ui.components.hotels.RatingBadge

@Composable
fun TransportCard(
    option: TransportOption,
    estimatedTotal: Int,
    isSelectedForCompare: Boolean = false,
    onClick: () -> Unit,
    onCompareClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Icon + Provider + Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (option.type) {
                            TransportType.BUS -> Icons.Default.DirectionsBus
                            TransportType.TRAIN -> Icons.Default.Train
                            TransportType.FLIGHT -> Icons.Default.Flight
                            TransportType.TAXI, TransportType.CAB -> Icons.Default.LocalTaxi
                            TransportType.CAR_RENTAL -> Icons.Default.DirectionsCar
                        },
                        contentDescription = option.type.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.providerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                RatingBadge(rating = option.rating)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Route details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = option.departureTime, fontWeight = FontWeight.SemiBold)
                    Text(text = option.from, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(text = "→", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${option.durationMinutes / 60}h ${if (option.durationMinutes % 60 > 0) "${option.durationMinutes % 60}m" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = option.arrivalTime, fontWeight = FontWeight.SemiBold)
                    Text(text = option.to, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            
            // Footer: Price + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${option.price} / ${option.pricingType.name.lowercase().replace("_", " ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹$estimatedTotal estimated total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (onCompareClick != null) {
                    OutlinedButton(
                        onClick = onCompareClick,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelectedForCompare) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                    ) {
                        if (isSelectedForCompare) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(if (isSelectedForCompare) "Selected" else "Compare", fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onClick,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Text("View Details", fontSize = 12.sp)
                    }
                }
            }
            
            if (option.isRecommended) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⭐ Recommended for your budget",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
