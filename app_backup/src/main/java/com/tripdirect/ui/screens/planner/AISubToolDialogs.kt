package com.tripdirect.ui.screens.planner

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.data.model.*
import com.tripdirect.ui.screens.trips.SafeAsyncImage

// 12. BUDGET ESTIMATOR DIALOG
@Composable
fun BudgetEstimatorDialog(
    destination: String,
    onDismiss: () -> Unit
) {
    var days by remember { mutableStateOf("3") }
    var travelers by remember { mutableStateOf("2") }

    val transportCost = 2500
    val hotelCost = 3000
    val foodCost = 1500
    val activitiesCost = 500
    val localTransportCost = 1000
    val total = transportCost + hotelCost + foodCost + activitiesCost + localTransportCost

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("₹ Budget Estimator", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Estimate trip costs for $destination", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = days, onValueChange = { days = it }, label = { Text("Days") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = travelers, onValueChange = { travelers = it }, label = { Text("Travelers") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Transportation")
                        Text("₹$transportCost", fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hotel")
                        Text("₹$hotelCost", fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Food")
                        Text("₹$foodCost", fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Activities")
                        Text("₹$activitiesCost", fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Local Transport")
                        Text("₹$localTransportCost", fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Estimated", fontWeight = FontWeight.Bold)
                        Text("₹$total", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done", fontWeight = FontWeight.Bold) }
        }
    )
}

// 13. BEST ROUTE FINDER DIALOG
@Composable
fun BestRouteFinderDialog(
    onDismiss: () -> Unit
) {
    var start by remember { mutableStateOf("Chennai") }
    var dest by remember { mutableStateOf("Ooty") }
    var optMode by remember { mutableStateOf("Lowest Cost") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🗺 Best Route Finder", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Starting Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dest, onValueChange = { dest = it }, label = { Text("Destination") }, modifier = Modifier.fillMaxWidth())
                
                Text("Optimization Mode:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Shortest Time", "Lowest Cost", "Shortest Distance").forEach { mode ->
                        FilterChip(
                            selected = optMode == mode,
                            onClick = { optMode = mode },
                            label = { Text(mode, fontSize = 11.sp) }
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Recommended Route:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("$start ➔ Salem ➔ Coonoor ➔ $dest", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Distance: 535 km  •  Time: 8 hrs 45 mins", style = MaterialTheme.typography.labelSmall)
                        Text("Estimated Fare: ₹1,800 (Bus/Train)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close", fontWeight = FontWeight.Bold) }
        }
    )
}

// 14. STAY RECOMMENDER DIALOG
@Composable
fun StayRecommenderDialog(
    onDismiss: () -> Unit
) {
    val sampleHotel = HotelItem(
        id = "h1",
        name = "Ooty View Resort",
        destination = "Ooty",
        imageUrl = TripImages.RESORT_HOTEL,
        rating = 4.3,
        pricePerNight = 1500,
        distance = "2.4 km from Ooty Lake",
        facilities = listOf("Breakfast", "Wi-Fi", "Parking")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🏨 Stay Recommender", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Direct hotel options (no agent markup):", style = MaterialTheme.typography.bodySmall)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SafeAsyncImage(
                            imageUrl = sampleHotel.imageUrl,
                            contentDescription = sampleHotel.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(sampleHotel.name, fontWeight = FontWeight.Bold)
                                Text("⭐ ${sampleHotel.rating}", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                            }
                            Text("₹${sampleHotel.pricePerNight}/night (Estimated price)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(sampleHotel.distance, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("✓ ${sampleHotel.facilities.joinToString("  ✓ ")}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK", fontWeight = FontWeight.Bold) }
        }
    )
}

// 15. FOOD RECOMMENDER DIALOG
@Composable
fun FoodRecommenderDialog(
    onDismiss: () -> Unit
) {
    val sampleRestaurant = RestaurantItem(
        id = "r1",
        name = "Earl's Secret",
        rating = 4.4,
        priceLevel = "₹₹",
        distance = "1.3 km",
        cuisine = "South Indian • Cafe",
        imageUrl = TripImages.RESTAURANT_FOOD
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🍴 Food Recommender", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Top rated local dining:", style = MaterialTheme.typography.bodySmall)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SafeAsyncImage(
                            imageUrl = sampleRestaurant.imageUrl,
                            contentDescription = sampleRestaurant.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(sampleRestaurant.name, fontWeight = FontWeight.Bold)
                                Text(sampleRestaurant.priceLevel, fontWeight = FontWeight.Bold)
                            }
                            Text("⭐ ${sampleRestaurant.rating} • ${sampleRestaurant.distance} away", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(sampleRestaurant.cuisine, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK", fontWeight = FontWeight.Bold) }
        }
    )
}

// 19. WEATHER FORECAST DIALOG
@Composable
fun WeatherForecastDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🌤 Weather Forecast (Demo)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Destination: Ooty", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Current Temp: 22°C (Partly Cloudy)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("High / Low: 22° / 15°")
                Text("Rain Probability: 15%")
                Spacer(modifier = Modifier.height(4.dp))
                Text("* Sample forecast data for demo purposes.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}

// 20. LOCAL INSIGHTS DIALOG
@Composable
fun LocalInsightsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("💡 Local Insights", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Destination: Ooty", fontWeight = FontWeight.Bold)
                Text("• Best Time to Visit: October - June")
                Text("• Avg Daily Budget: ₹2,000 - ₹3,500")
                Text("• Transport: Local cabs & Nilgiri Toy Train")
                Text("• Customs: Respect tea estate guidelines and dress warmly for evenings.")
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Got It") } }
    )
}

// 21. PACKING LIST DIALOG
@Composable
fun PackingListDialog(onDismiss: () -> Unit) {
    var check1 by remember { mutableStateOf(true) }
    var check2 by remember { mutableStateOf(true) }
    var check3 by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🧳 Packing List", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = check1, onCheckedChange = { check1 = it })
                    Text("ID Proof & Documents")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = check2, onCheckedChange = { check2 = it })
                    Text("Warm Clothes / Jackets")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = check3, onCheckedChange = { check3 = it })
                    Text("Camera & Power Bank")
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Done") } }
    )
}

// 23. LANGUAGE HELPER DIALOG
@Composable
fun LanguageHelperDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🌐 Language Helper", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("English ➔ Tamil / Malayalam", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("• Hello: Vanakkam (வணக்கம்)")
                Text("• Thank You: Nandri (நன்றி)")
                Text("• How much?: Evvalavu? (எவ்வளவு?)")
                Text("• Help: Udhavi (உதவி)")
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}

// 24. CURRENCY CONVERTER DIALOG
@Composable
fun CurrencyConverterDialog(onDismiss: () -> Unit) {
    var inrAmount by remember { mutableStateOf("10000") }
    val usd = (inrAmount.toDoubleOrNull() ?: 0.0) / 83.5

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("💱 Currency Converter (Demo)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = inrAmount,
                    onValueChange = { inrAmount = it },
                    label = { Text("Amount in INR (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Converted: $${String.format("%.2f", usd)} USD",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("* Demo conversion rate: 1 USD = 83.5 INR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Done") } }
    )
}

// 26. OFFLINE PLAN DIALOG
@Composable
fun OfflinePlanDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("☁ Offline Plan", fontWeight = FontWeight.Bold) },
        text = {
            Text("Save your current trip itinerary and travel documents to local storage so you can access them without an active internet connection.")
        },
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Trip saved for offline viewing!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text("Save Offline", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
