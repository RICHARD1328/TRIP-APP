package com.tripdirect.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.ui.components.home.SectionHeader
import com.tripdirect.ui.components.search.RecentSearchItem
import com.tripdirect.ui.components.search.SearchCategoryChip
import com.tripdirect.ui.components.search.SearchInput
import com.tripdirect.ui.components.search.SearchResultCard
import com.tripdirect.viewmodel.SearchCategory
import com.tripdirect.viewmodel.SearchViewModel

@Composable
fun SearchDestinationScreen(
    viewModel: SearchViewModel,
    locationViewModel: com.tripdirect.viewmodel.LocationSearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDestination: (String) -> Unit,
    onNavigateToAttraction: (String) -> Unit,
    onNavigateToHotel: (String) -> Unit
) {
    val searchQuery by locationViewModel.searchQuery.collectAsState()
    val nominatimResults by locationViewModel.nominatimResults.collectAsState()
    val isLoading by locationViewModel.isLoading.collectAsState()
    val errorMessage by locationViewModel.errorMessage.collectAsState()
    val hasSubmitted by locationViewModel.hasSubmitted.collectAsState()
    val popularSearches by viewModel.popularSearches.collectAsState()

    val isOffline by locationViewModel.isOffline.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Top Search Bar (Keyboard submit triggers search)
        SearchInput(
            query = searchQuery,
            onQueryChange = { locationViewModel.onSearchQueryChange(it) },
            onClearQuery = { locationViewModel.clearSearch() },
            onBackClick = onNavigateBack,
            onSearchAction = { locationViewModel.submitSearch() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasSubmitted && searchQuery.isEmpty()) {
            // Initial / Empty State before submitting
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = "Popular Destinations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(popularSearches) { pop ->
                    Text(
                        text = "📍 $pop",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                locationViewModel.onSearchQueryChange(pop)
                                locationViewModel.submitSearch()
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            // Active Nominatim Search State
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Searching OpenStreetMap Nominatim...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    com.tripdirect.ui.components.common.NetworkErrorCard(
                        title = if (isOffline) "No internet connection" else "Network Error",
                        message = errorMessage ?: "Connect to the internet to search destinations.",
                        isOffline = isOffline,
                        isRetrying = isLoading,
                        onRetry = { locationViewModel.retrySearch() }
                    )
                }
            } else if (hasSubmitted && nominatimResults.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No locations found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "We couldn't find any place matching\n\"$searchQuery\"",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { locationViewModel.clearSearch() }) {
                            Text("Clear Search")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Search Results (${nominatimResults.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(nominatimResults) { item ->
                        NominatimSearchResultCard(
                            item = item,
                            onClick = {
                                locationViewModel.selectLocation(item)
                                val destinationId = item.name.trim().lowercase()
                                onNavigateToDestination(destinationId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NominatimSearchResultCard(
    item: com.tripdirect.data.network.NominatimSearchResultItem,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = item.category,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.fullAddress,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lat: %.4f | Lon: %.4f".format(item.latitude, item.longitude),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

