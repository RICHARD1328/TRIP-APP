package com.tripdirect.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.ui.components.explore.EstimatedCostCard
import com.tripdirect.ui.components.explore.InfoCard
import com.tripdirect.ui.components.home.SectionHeader
import com.tripdirect.viewmodel.ExplorePlacesViewModel
import kotlinx.coroutines.launch

@Composable
fun PlaceDetailsScreen(
    placeId: String,
    viewModel: ExplorePlacesViewModel,
    onNavigateBack: () -> Unit,
    onPlanVisit: (String) -> Unit,
    onViewRoute: (String) -> Unit
) {
    LaunchedEffect(placeId) {
        viewModel.loadPlaceDetails(placeId)
    }

    val place by viewModel.selectedPlace.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val allPlaces by viewModel.places.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (isLoading || place == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentPlace = place ?: return
    // Fake nearby places from the same location (excluding itself)
    val nearbyPlaces = allPlaces.filter { it.location == currentPlace.location && it.id != currentPlace.id }.take(3)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Image Box
            item {
                val heroImage = currentPlace.photoUrl.takeIf { !it.isNullOrBlank() }
                    ?: com.tripdirect.data.model.AttractionImageProvider.getAttractionImage(currentPlace.name)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    coil.compose.AsyncImage(
                        model = heroImage,
                        contentDescription = currentPlace.name,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)
                    )

                    // Gradient for readability
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                                )
                            )
                    )

                    // Top Bar Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = { viewModel.toggleFavorite(currentPlace.id); viewModel.loadPlaceDetails(currentPlace.id) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = if (currentPlace.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (currentPlace.isFavorite) Color.Red else Color.White
                            )
                        }
                    }

                    currentPlace.photoAttribution?.let { attr ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = attr,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Title and Location
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentPlace.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\uD83D\uDCCD ${currentPlace.location}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentPlace.rating.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // About
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader("About this place")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentPlace.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Information Grid
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard(
                            icon = Icons.Default.LocalActivity,
                            title = "Entry Fee",
                            value = if (currentPlace.entryFee > 0) "₹${currentPlace.entryFee}" else "Free",
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.Default.AccessTime,
                            title = "Opening Hours",
                            value = "${currentPlace.openingTime} - ${currentPlace.closingTime}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard(
                            icon = Icons.Default.Timer,
                            title = "Recommended Duration",
                            value = currentPlace.estimatedVisitDuration,
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.Default.CalendarMonth,
                            title = "Best Time",
                            value = currentPlace.bestTimeToVisit,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Estimated Cost
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EstimatedCostCard(place = currentPlace)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Nearby Places
            if (nearbyPlaces.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader("Nearby Places")
                        Spacer(modifier = Modifier.height(12.dp))
                        nearbyPlaces.forEach { nearPlace ->
                            Text(
                                text = "• ${nearPlace.name}",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .clickable { /* Could navigate to this place */ },
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Bottom Buttons (Add to Trip, Route)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Added ${currentPlace.name} to your trip")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add to Trip")
                    }

                    OutlinedButton(
                        onClick = { onViewRoute(currentPlace.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = "Route")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Route")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Plan a Visit Button
            item {
                Button(
                    onClick = { onPlanVisit(currentPlace.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                ) {
                    Text("✨ Plan a Visit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp)) // padding for bottom nav space
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}
