package com.tripdirect.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tripdirect.ui.components.TripDirectBottomNavigation
import com.tripdirect.ui.screens.home.HomeScreen
import com.tripdirect.ui.screens.explore.ExplorePlacesScreen
import com.tripdirect.ui.screens.trips.TripsScreen
import com.tripdirect.viewmodel.ExplorePlacesViewModel
import com.tripdirect.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripdirect.ui.screens.planner.AITripPlannerScreen
import com.tripdirect.ui.screens.profile.ProfileScreen
import com.tripdirect.viewmodel.ProfileViewModel
import com.tripdirect.viewmodel.TripPlannerViewModel

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    exploreViewModel: ExplorePlacesViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToDestination: (String) -> Unit,
    onNavigateToPlaceDetails: (String) -> Unit,
    onNavigateToAttraction: (String) -> Unit,
    onNavigateToHotel: (String) -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToHotelsSearch: () -> Unit,
    onNavigateToTransportSearch: () -> Unit,
    onNavigateToComparison: () -> Unit,
    onNavigateToItinerary: () -> Unit,
    onNavigateToServerSettings: () -> Unit = {}
) {
    var currentRoute by rememberSaveable { mutableStateOf("home") }
    val plannerViewModel: TripPlannerViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    Scaffold(
        bottomBar = {
            TripDirectBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentRoute) {
                "home" -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToSearch = onNavigateToSearch,
                        onNavigateToDestination = onNavigateToDestination,
                        onNavigateToAttraction = onNavigateToAttraction,
                        onNavigateToHotel = onNavigateToHotel,
                        onNavigateToPlanner = onNavigateToPlanner,
                        onNavigateToBudget = onNavigateToBudget,
                        onNavigateToHotelsSearch = onNavigateToHotelsSearch,
                        onNavigateToTransportSearch = onNavigateToTransportSearch,
                        onNavigateToComparison = onNavigateToComparison,
                        onNavigateToItinerary = onNavigateToItinerary
                    )
                }
                "explore" -> {
                    ExplorePlacesScreen(
                        viewModel = exploreViewModel,
                        onNavigateBack = { currentRoute = "home" },
                        onNavigateToPlaceDetails = onNavigateToPlaceDetails
                    )
                }
                "trips" -> {
                    TripsScreen(
                        onNavigateToHotels = onNavigateToHotelsSearch,
                        onNavigateToTransport = onNavigateToTransportSearch,
                        onNavigateToPlaces = { currentRoute = "explore" },
                        onNavigateToBudget = onNavigateToBudget,
                        onNavigateToMap = { /* Map Preview Nav */ },
                        onNavigateToItinerary = onNavigateToItinerary,
                        onNavigateToComparison = onNavigateToComparison,
                        onNavigateToSafety = { /* Safety Nav */ },
                        onNavigateToPlanner = onNavigateToPlanner,
                        onNavigateToExplorePlace = onNavigateToPlaceDetails
                    )
                }
                "planner" -> {
                    AITripPlannerScreen(
                        viewModel = plannerViewModel,
                        onNavigateBack = { currentRoute = "home" },
                        onNavigateToComparison = onNavigateToComparison
                    )
                }
                "profile" -> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onNavigateToTrips = { currentRoute = "trips" },
                        onNavigateToComparison = onNavigateToComparison,
                        onNavigateToPlaceDetails = onNavigateToPlaceDetails,
                        onNavigateToServerSettings = onNavigateToServerSettings
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title)
    }
}
