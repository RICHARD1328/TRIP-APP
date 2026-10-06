package com.tripdirect.ui.screens.hotels

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.HotelSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelCompareScreen(
    viewModel: HotelSearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHotelDetails: (String) -> Unit
) {
    val compareList by viewModel.selectedHotelsForCompare.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Hotels") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (compareList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hotels selected for comparison.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .horizontalScroll(rememberScrollState())
            ) {
                // Table Header
                Row(modifier = Modifier.padding(16.dp)) {
                    Spacer(modifier = Modifier.width(100.dp)) // Label column width
                    compareList.forEach { hotel ->
                        Column(
                            modifier = Modifier.width(140.dp).padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = hotel.name,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onNavigateToHotelDetails(hotel.id) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("Select")
                            }
                        }
                    }
                }
                
                HorizontalDivider()
                
                // Compare Rows
                CompareRow(label = "Rating", values = compareList.map { "⭐ ${it.rating}" })
                CompareRow(label = "Price/night", values = compareList.map { "₹${it.pricePerNight}" })
                CompareRow(label = "Distance", values = compareList.map { "${it.distanceFromCenter} km" })
                CompareRow(label = "Breakfast", values = compareList.map { if (it.breakfastIncluded) "✓" else "✕" })
                CompareRow(label = "Wi-Fi", values = compareList.map { if (it.facilities.contains("Wi-Fi") || it.facilities.contains("Free Wi-Fi")) "✓" else "✕" })
                CompareRow(label = "Parking", values = compareList.map { if (it.facilities.contains("Parking")) "✓" else "✕" })
            }
        }
    }
}

@Composable
fun CompareRow(label: String, values: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(100.dp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        values.forEach { value ->
            Box(
                modifier = Modifier.width(140.dp),
                contentAlignment = Alignment.Center
            ) {
                if (value == "✓") {
                    Icon(Icons.Default.Check, contentDescription = "Yes", tint = Color.Green)
                } else if (value == "✕") {
                    Icon(Icons.Default.Close, contentDescription = "No", tint = Color.Red)
                } else {
                    Text(text = value, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
}
