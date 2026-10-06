package com.tripdirect.ui.components.route

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.LocationPoint
import com.tripdirect.data.model.LocationType
import com.tripdirect.data.model.RouteOptimizationResult
import com.tripdirect.data.model.TransportMode
import com.tripdirect.data.model.TripRoute

@Composable
fun MockMapView(
    route: TripRoute?,
    selectedLocation: LocationPoint?,
    userLocation: LocationPoint?,
    onLocationClick: (LocationPoint) -> Unit,
    onEnableUserLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }

    if (route == null || route.locations.isEmpty()) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading Interactive Route Map...", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val errorColor = MaterialTheme.colorScheme.error
    val secondaryColor = MaterialTheme.colorScheme.secondary

    BoxWithConstraints(
        modifier = modifier
            .background(Color(0xFFEFEFEF))
            .clip(RoundedCornerShape(0.dp))
    ) {
        val width = maxWidth.value
        val height = maxHeight.value

        val allPoints = if (userLocation != null) route.locations + userLocation else route.locations
        val minX = allPoints.minOf { it.latitude }
        val maxX = allPoints.maxOf { it.latitude }
        val minY = allPoints.minOf { it.longitude }
        val maxY = allPoints.maxOf { it.longitude }

        val rangeX = (maxX - minX).coerceAtLeast(1.0)
        val rangeY = (maxY - minY).coerceAtLeast(1.0)

        val padding = 50f / zoomLevel

        fun getScreenOffset(point: LocationPoint): Offset {
            val normalizedX = (point.latitude - minX) / rangeX
            val normalizedY = (point.longitude - minY) / rangeY
            val screenX = padding + (normalizedX * (width - 2 * padding))
            val screenY = height - padding - (normalizedY * (height - 2 * padding))
            return Offset(screenX.toFloat(), screenY.toFloat())
        }

        // Draw Map Grid and Route Polyline
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw background grid simulation
            val gridSpacing = 60f * density
            var x = 0f
            while (x < size.width) {
                drawLine(Color.LightGray.copy(alpha = 0.4f), Offset(x, 0f), Offset(x, size.height), 1f)
                x += gridSpacing
            }
            var y = 0f
            while (y < size.height) {
                drawLine(Color.LightGray.copy(alpha = 0.4f), Offset(0f, y), Offset(size.width, y), 1f)
                y += gridSpacing
            }

            // Draw route connecting lines
            if (route.locations.size > 1) {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                for (i in 0 until route.locations.size - 1) {
                    val p1 = getScreenOffset(route.locations[i])
                    val p2 = getScreenOffset(route.locations[i + 1])

                    drawLine(
                        color = primaryColor,
                        start = Offset(p1.x * density, p1.y * density),
                        end = Offset(p2.x * density, p2.y * density),
                        strokeWidth = 5f * density,
                        pathEffect = pathEffect
                    )
                }
            }
        }

        // Draw Location Markers
        route.locations.forEachIndexed { index, location ->
            val pos = getScreenOffset(location)
            val isSelected = location.id == selectedLocation?.id

            Box(
                modifier = Modifier
                    .offset(x = pos.x.dp - 20.dp, y = pos.y.dp - 20.dp)
                    .size(40.dp)
                    .background(
                        color = if (isSelected) errorColor else if (index == 0) secondaryColor else primaryColor,
                        shape = CircleShape
                    )
                    .clickable { onLocationClick(location) },
                contentAlignment = Alignment.Center
            ) {
                val (icon, label) = when (location.type) {
                    LocationType.START -> Pair(Icons.Default.Place, "Start")
                    LocationType.HOTEL -> Pair(Icons.Default.Hotel, "Hotel")
                    LocationType.ATTRACTION -> Pair(Icons.Default.Star, "Attraction")
                    LocationType.RESTAURANT -> Pair(Icons.Default.Restaurant, "Food")
                    LocationType.TRANSPORT -> Pair(Icons.Default.DirectionsBus, "Transport")
                    LocationType.END -> Pair(Icons.Default.Flag, "End")
                    else -> Pair(Icons.Default.Place, "Point")
                }
                Icon(icon, contentDescription = label, tint = onPrimaryColor, modifier = Modifier.size(20.dp))
            }
        }

        // User Location Marker
        if (userLocation != null) {
            val userPos = getScreenOffset(userLocation)
            Box(
                modifier = Modifier
                    .offset(x = userPos.x.dp - 16.dp, y = userPos.y.dp - 16.dp)
                    .size(32.dp)
                    .background(Color(0xFF2196F3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        // Map Control Floating Buttons (Zoom In, Zoom Out, Center, Fit Route)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(2.5f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            SmallFloatingActionButton(
                onClick = { zoomLevel = (zoomLevel / 1.25f).coerceAtLeast(0.75f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            SmallFloatingActionButton(
                onClick = onEnableUserLocation,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }

            SmallFloatingActionButton(
                onClick = { zoomLevel = 1.0f },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.CropFree, contentDescription = "Fit Route")
            }
        }
    }
}

@Composable
fun FilterChipsRow(
    selectedFilter: LocationType?,
    onFilterSelected: (LocationType?) -> Unit
) {
    val filters = listOf(
        Pair(null, "All"),
        Pair(LocationType.HOTEL, "Hotels"),
        Pair(LocationType.ATTRACTION, "Attractions"),
        Pair(LocationType.RESTAURANT, "Restaurants"),
        Pair(LocationType.TRANSPORT, "Transport")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        items(filters) { (type, label) ->
            FilterChip(
                selected = selectedFilter == type,
                onClick = { onFilterSelected(type) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun TransportModeSelector(
    selectedMode: TransportMode,
    onModeSelected: (TransportMode) -> Unit
) {
    val modes = listOf(
        Triple(TransportMode.WALKING, "Walk", Icons.Default.DirectionsWalk),
        Triple(TransportMode.BUS, "Bus", Icons.Default.DirectionsBus),
        Triple(TransportMode.CAR, "Car", Icons.Default.DirectionsCar),
        Triple(TransportMode.TAXI, "Taxi", Icons.Default.LocalTaxi),
        Triple(TransportMode.BICYCLE, "Bike", Icons.Default.DirectionsBike)
    )

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "Travel Mode",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(modes) { (mode, label, icon) ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { onModeSelected(mode) },
                    label = { Text(label) },
                    leadingIcon = { Icon(icon, contentDescription = label, modifier = Modifier.size(18.dp)) }
                )
            }
        }
    }
}

@Composable
fun RouteSummaryCard(route: TripRoute) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                value = String.format("%.1f km", route.totalDistanceKm),
                label = "Distance"
            )
            Divider(modifier = Modifier.height(36.dp).width(1.dp))
            val hours = route.totalDurationMinutes / 60
            val mins = route.totalDurationMinutes % 60
            val timeStr = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
            SummaryItem(
                value = timeStr,
                label = "Estimated Time"
            )
            Divider(modifier = Modifier.height(36.dp).width(1.dp))
            SummaryItem(
                value = "₹${route.totalEstimatedCost}",
                label = "Estimated Cost"
            )
        }
    }
}

@Composable
private fun SummaryItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun OptimizationBanner(
    result: RouteOptimizationResult,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("✨ Route Optimized", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "Distance reduced by ${String.format("%.1f", result.distanceSavedKm)} km • Time saved: ${result.durationSavedMinutes} min",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun RouteWarningCard(totalDurationMinutes: Int) {
    if (totalDurationMinutes > 240) { // Warning if travel time > 4 hours
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("⚠ Long Route Warning", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                    Text("Today's route requires extensive travel. Consider optimizing or splitting places across days.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationBottomSheet(
    location: LocationPoint,
    onDismiss: () -> Unit,
    onNavigate: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (location.type) {
                    LocationType.HOTEL -> Icons.Default.Hotel
                    LocationType.ATTRACTION -> Icons.Default.Star
                    LocationType.RESTAURANT -> Icons.Default.Restaurant
                    LocationType.TRANSPORT -> Icons.Default.DirectionsBus
                    else -> Icons.Default.Place
                }
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(location.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(location.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (location.estimatedVisitMinutes > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Visit: ${location.estimatedVisitMinutes} min", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${location.openingHours} – ${location.closingHours}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("View Details")
                }
                Button(onClick = onNavigate, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Get Directions")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StopList(
    route: TripRoute,
    onSelectStop: (LocationPoint) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Today's Route Stops (${route.locations.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        route.locations.forEachIndexed { index, location ->
            val prevSegment = if (index > 0 && index - 1 < route.segments.size) route.segments[index - 1] else null
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectStop(location) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(location.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        if (prevSegment != null) {
                            Text(
                                "${String.format("%.1f", prevSegment.distanceKm)} km from previous stop • ${prevSegment.durationMinutes} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text("Starting Point", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

