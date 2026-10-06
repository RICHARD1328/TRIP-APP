package com.tripdirect.ui.screens.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.ui.components.explore.ExploreSearchBar
import com.tripdirect.ui.components.explore.FilterBottomSheet
import com.tripdirect.ui.components.explore.PlaceCard
import com.tripdirect.ui.components.home.SectionHeader
import com.tripdirect.viewmodel.ExplorePlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorePlacesScreen(
    viewModel: ExplorePlacesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlaceDetails: (String) -> Unit
) {
    val places by viewModel.filteredPlaces.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedRating by viewModel.selectedRating.collectAsState()
    val selectedBudget by viewModel.selectedBudget.collectAsState()
    val selectedDistance by viewModel.selectedDistance.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    val categories = listOf("All", "Nature", "Beach", "Adventure", "Historical", "Religious", "Food", "Shopping", "Family")

    if (showFilterSheet) {
        FilterBottomSheet(
            currentRating = selectedRating,
            currentBudget = selectedBudget,
            currentDistance = selectedDistance,
            onApply = { r, b, d -> viewModel.applyFilters(r, b, d) },
            onReset = { viewModel.clearFilters() },
            onDismiss = { showFilterSheet = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Explore Places",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        ExploreSearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Discover amazing places\nfor your next journey.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Categories
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.onCategorySelect(category) },
                    label = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Places",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { showFilterSheet = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = if (selectedRating != "Any Rating" || selectedBudget != "Any Price") 
                            MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val err = errorMessage
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (err != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                com.tripdirect.ui.components.common.NetworkErrorCard(
                    title = if (isOffline) "No internet connection" else "Unable to Load Places",
                    message = err,
                    isOffline = isOffline,
                    isRetrying = isLoading,
                    onRetry = { viewModel.retry() }
                )
            }
        } else if (places.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No places found", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Try changing your filters\nor search another place.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { viewModel.clearFilters(); viewModel.onSearchQueryChange("") }) {
                        Text("Clear Filters & Search")
                    }
                }
            }
        } else {
            val featuredPlaces = places.filter { it.isFeatured }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (featuredPlaces.isNotEmpty()) {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(featuredPlaces) { place ->
                                PlaceCard(
                                    place = place,
                                    onClick = { onNavigateToPlaceDetails(place.id) },
                                    onFavoriteClick = { viewModel.toggleFavorite(place.id) },
                                    modifier = Modifier.fillParentMaxWidth(0.85f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                item {
                    SectionHeader(title = "Explore All Places")
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Grid mapping to vertical list since LazyVerticalGrid inside LazyColumn is problematic
                val pairedPlaces = places.chunked(2)
                items(pairedPlaces) { rowPlaces ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlaceCard(
                            place = rowPlaces[0],
                            onClick = { onNavigateToPlaceDetails(rowPlaces[0].id) },
                            onFavoriteClick = { viewModel.toggleFavorite(rowPlaces[0].id) },
                            modifier = Modifier.weight(1f)
                        )
                        if (rowPlaces.size > 1) {
                            PlaceCard(
                                place = rowPlaces[1],
                                onClick = { onNavigateToPlaceDetails(rowPlaces[1].id) },
                                onFavoriteClick = { viewModel.toggleFavorite(rowPlaces[1].id) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
