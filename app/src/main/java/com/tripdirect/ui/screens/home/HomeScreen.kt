package com.tripdirect.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.home.AIPlannerBanner
import com.tripdirect.ui.components.home.AttractionCard
import com.tripdirect.ui.components.home.BudgetBanner
import com.tripdirect.ui.components.home.ContinueTripCard
import com.tripdirect.ui.components.home.DestinationCard
import com.tripdirect.ui.components.home.DestinationSearchBar
import com.tripdirect.ui.components.home.HomeTopBar
import com.tripdirect.ui.components.home.HotelCard
import com.tripdirect.ui.components.home.PackageComparisonCard
import com.tripdirect.ui.components.home.QuickActionsRow
import com.tripdirect.ui.components.home.SectionHeader
import com.tripdirect.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToDestination: (String) -> Unit,
    onNavigateToAttraction: (String) -> Unit,
    onNavigateToHotel: (String) -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToHotelsSearch: () -> Unit,
    onNavigateToTransportSearch: () -> Unit,
    onNavigateToComparison: () -> Unit,
    onNavigateToItinerary: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val popularDestinations by viewModel.popularDestinations.collectAsState()
    val popularAttractions by viewModel.popularAttractions.collectAsState()
    val recommendedHotels by viewModel.recommendedHotels.collectAsState()
    val activeTrip by viewModel.activeTrip.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val err = errorMessage
    if (err != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = err, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
    ) {
        item {
            HomeTopBar(currentUser = currentUser)
        }

        item {
            DestinationSearchBar(
                onClick = onNavigateToSearch
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // We can just skip search results handling here if it's moved to SearchDestinationScreen, 
        // but for now we'll leave the state logic below just in case, though isSearchActive will be false.

        val results = searchResults
        if (isSearchActive && results != null) {
            
            if (results.destinations.isEmpty() && results.hotels.isEmpty() && results.attractions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                if (results.destinations.isNotEmpty()) {
                    item { SectionHeader(title = "Destinations") }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(results.destinations) { dest ->
                                DestinationCard(destination = dest, onClick = { onNavigateToDestination(dest.id) })
                            }
                        }
                    }
                }
                if (results.hotels.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { SectionHeader(title = "Hotels") }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(results.hotels) { hotel ->
                                HotelCard(hotel = hotel, onClick = { onNavigateToHotel(hotel.id) })
                            }
                        }
                    }
                }
                if (results.attractions.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { SectionHeader(title = "Attractions") }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(results.attractions) { attr ->
                                AttractionCard(attraction = attr, onClick = { onNavigateToAttraction(attr.id) })
                            }
                        }
                    }
                }
            }
        } else {
            // Normal Home State
            item {
                QuickActionsRow(
                    onNavigateToPlanner = onNavigateToPlanner,
                    onNavigateToBudget = onNavigateToBudget,
                    onNavigateToHotels = onNavigateToHotelsSearch,
                    onNavigateToTransport = onNavigateToTransportSearch
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SectionHeader(
                    title = "Popular Destinations",
                    onSeeAllClick = { /* Navigate to explore */ }
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(popularDestinations) { dest ->
                        DestinationCard(
                            destination = dest,
                            onClick = { onNavigateToDestination(dest.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                AIPlannerBanner(onClick = onNavigateToPlanner)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SectionHeader(
                    title = "Popular Attractions",
                    onSeeAllClick = { /* See all */ }
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(popularAttractions) { attr ->
                        AttractionCard(
                            attraction = attr,
                            onClick = { onNavigateToAttraction(attr.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SectionHeader(
                    title = "Recommended Hotels",
                    onSeeAllClick = { onNavigateToHotelsSearch() }
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(recommendedHotels) { hotel ->
                        HotelCard(
                            hotel = hotel,
                            onClick = { onNavigateToHotel(hotel.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                PackageComparisonCard(onClick = onNavigateToComparison)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                BudgetBanner(onClick = onNavigateToBudget)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SectionHeader(title = "Continue Planning")
                ContinueTripCard(
                    trip = activeTrip,
                    onContinueClick = onNavigateToItinerary,
                    onPlanNewClick = onNavigateToPlanner
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
