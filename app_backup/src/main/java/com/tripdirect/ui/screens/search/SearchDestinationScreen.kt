package com.tripdirect.ui.screens.search

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
    onNavigateBack: () -> Unit,
    onNavigateToDestination: (String) -> Unit,
    onNavigateToAttraction: (String) -> Unit,
    onNavigateToHotel: (String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val destinations by viewModel.destinations.collectAsState()
    val attractions by viewModel.attractions.collectAsState()
    val hotels by viewModel.hotels.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val popularSearches by viewModel.popularSearches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Top Search Bar
        SearchInput(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onClearQuery = viewModel::clearSearch,
            onBackClick = onNavigateBack,
            onSearchAction = { viewModel.onSearchAction(searchQuery) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isEmpty()) {
            // Empty Search State
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Searches",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = viewModel::clearRecentSearches) {
                                Text("Clear All")
                            }
                        }
                    }
                    items(recentSearches) { query ->
                        RecentSearchItem(
                            query = query,
                            onClick = { viewModel.onSearchQueryChange(query) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                item {
                    Text(
                        text = "Popular Searches",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                // For simplicity, showing popular searches as a wrapped grid or just list
                items(popularSearches) { pop ->
                    Text(
                        text = pop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onSearchQueryChange(pop) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            // Active Search State

            // Categories
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SearchCategory.values()) { category ->
                    SearchCategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { viewModel.onCategorySelect(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Searching destinations...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = { viewModel.onSearchAction(searchQuery) }) {
                            Text("Try Again")
                        }
                    }
                }
            } else {
                val showDests = (selectedCategory == SearchCategory.All || selectedCategory == SearchCategory.Destinations)
                val showAttrs = (selectedCategory == SearchCategory.All || selectedCategory == SearchCategory.Attractions)
                val showHots = (selectedCategory == SearchCategory.All || selectedCategory == SearchCategory.Hotels)

                val hasResults = (showDests && destinations.isNotEmpty()) || 
                                 (showAttrs && attractions.isNotEmpty()) || 
                                 (showHots && hotels.isNotEmpty())

                if (!hasResults) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No results found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We couldn't find anything\nmatching \"$searchQuery\"",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Try another destination",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (showDests && destinations.isNotEmpty()) {
                            if (selectedCategory == SearchCategory.All) {
                                item { SectionHeader("Destinations") }
                            }
                            items(destinations) { dest ->
                                SearchResultCard(
                                    title = dest.name,
                                    subtitle = dest.state,
                                    rating = dest.rating,
                                    type = dest.category,
                                    priceOrDetails = "",
                                    icon = dest.vectorIcon,
                                    onClick = { onNavigateToDestination(dest.id) }
                                )
                            }
                        }

                        if (showAttrs && attractions.isNotEmpty()) {
                            if (selectedCategory == SearchCategory.All) {
                                item { Spacer(modifier = Modifier.height(8.dp)) }
                                item { SectionHeader("Attractions") }
                            }
                            items(attractions) { attr ->
                                SearchResultCard(
                                    title = attr.name,
                                    subtitle = attr.location,
                                    rating = attr.rating,
                                    type = "Attraction",
                                    priceOrDetails = if (attr.entryFee > 0) "Entry ₹${attr.entryFee}" else "Free",
                                    icon = attr.vectorIcon,
                                    onClick = { onNavigateToAttraction(attr.id) }
                                )
                            }
                        }

                        if (showHots && hotels.isNotEmpty()) {
                            if (selectedCategory == SearchCategory.All) {
                                item { Spacer(modifier = Modifier.height(8.dp)) }
                                item { SectionHeader("Hotels") }
                            }
                            items(hotels) { hotel ->
                                SearchResultCard(
                                    title = hotel.name,
                                    subtitle = "${hotel.distanceFromCenter} km from center",
                                    rating = hotel.rating,
                                    type = "Hotel",
                                    priceOrDetails = "₹${hotel.pricePerNight} / night",
                                    icon = hotel.vectorIcon,
                                    onClick = { onNavigateToHotel(hotel.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
