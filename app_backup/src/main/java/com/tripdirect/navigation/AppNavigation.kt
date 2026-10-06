package com.tripdirect.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tripdirect.ui.screens.auth.ForgotPasswordScreen
import com.tripdirect.ui.screens.auth.LoginScreen
import com.tripdirect.ui.screens.auth.RegisterScreen
import com.tripdirect.ui.screens.main.MainScreen
import com.tripdirect.ui.screens.search.SearchDestinationScreen
import com.tripdirect.ui.screens.budget.BudgetBreakdownScreen
import com.tripdirect.ui.screens.budget.BudgetCalculatorScreen
import com.tripdirect.ui.screens.budget.BudgetCompareScreen
import com.tripdirect.ui.screens.budget.BudgetResultScreen
import com.tripdirect.ui.screens.explore.ExplorePlacesScreen
import com.tripdirect.ui.screens.explore.PlaceDetailsScreen
import com.tripdirect.ui.screens.explore.DestinationDetailScreen
import com.tripdirect.viewmodel.DestinationDetailViewModel
import com.tripdirect.ui.screens.splash.SplashScreen
import com.tripdirect.ui.screens.route.NavigationScreen
import com.tripdirect.ui.screens.route.RouteDetailsScreen
import com.tripdirect.ui.screens.route.RouteMapScreen
import com.tripdirect.ui.screens.itinerary.DayDetailsScreen
import com.tripdirect.ui.screens.itinerary.EditItineraryScreen
import com.tripdirect.ui.screens.itinerary.ItineraryScreen
import com.tripdirect.ui.screens.itinerary.ItinerarySummaryScreen
import com.tripdirect.ui.screens.hotels.HotelCompareScreen
import com.tripdirect.ui.screens.hotels.HotelDetailsScreen
import com.tripdirect.ui.screens.hotels.HotelSearchScreen
import com.tripdirect.ui.screens.planner.AITripPlannerScreen
import com.tripdirect.ui.screens.planner.EditTripScreen
import com.tripdirect.ui.screens.planner.GeneratedTripScreen
import com.tripdirect.ui.screens.planner.PlanningProgressScreen
import com.tripdirect.ui.screens.planner.TripDayDetailsScreen
import com.tripdirect.ui.screens.transport.TransportCompareScreen
import com.tripdirect.ui.screens.transport.TransportDetailsScreen
import com.tripdirect.ui.screens.transport.TransportSearchScreen
import com.tripdirect.ui.screens.packagecomp.PackageBreakdownScreen
import com.tripdirect.ui.screens.packagecomp.PackageComparisonScreen
import com.tripdirect.ui.screens.reviews.MyReviewsScreen
import com.tripdirect.ui.screens.reviews.ReviewDetailsScreen
import com.tripdirect.ui.screens.reviews.ReviewsScreen
import com.tripdirect.ui.screens.reviews.WriteReviewScreen
import com.tripdirect.ui.screens.safety.EmergencyContactsScreen
import com.tripdirect.ui.screens.safety.EmergencyScreen
import com.tripdirect.ui.screens.safety.NearbyServicesScreen
import com.tripdirect.ui.screens.safety.SOSActiveScreen
import com.tripdirect.ui.screens.safety.SafetyTipsScreen
import com.tripdirect.ui.screens.profile.AboutScreen
import com.tripdirect.ui.screens.profile.BudgetPreferencesScreen
import com.tripdirect.ui.screens.profile.EditProfileScreen
import com.tripdirect.ui.screens.profile.HelpSupportScreen
import com.tripdirect.ui.screens.profile.LanguageScreen
import com.tripdirect.ui.screens.profile.MyTripsScreen
import com.tripdirect.ui.screens.profile.NotificationSettingsScreen
import com.tripdirect.ui.screens.profile.PrivacyScreen
import com.tripdirect.ui.screens.profile.ProfileScreen
import com.tripdirect.ui.screens.profile.SavedPlacesScreen
import com.tripdirect.ui.screens.profile.TravelPreferencesScreen
import com.tripdirect.viewmodel.AuthViewModel
import com.tripdirect.viewmodel.ExplorePlacesViewModel
import com.tripdirect.viewmodel.HomeViewModel
import com.tripdirect.viewmodel.HotelSearchViewModel
import com.tripdirect.viewmodel.SearchViewModel
import com.tripdirect.viewmodel.TransportSearchViewModel
import com.tripdirect.viewmodel.TripPlannerViewModel
import com.tripdirect.viewmodel.BudgetCalculatorViewModel
import com.tripdirect.viewmodel.RouteMapViewModel
import com.tripdirect.viewmodel.ItineraryViewModel
import com.tripdirect.viewmodel.PackageComparisonViewModel
import com.tripdirect.viewmodel.ReviewsViewModel
import com.tripdirect.viewmodel.EmergencyViewModel
import com.tripdirect.viewmodel.ProfileViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel() // Use a shared ViewModel or scoped
    val searchViewModel: SearchViewModel = viewModel()
    val exploreViewModel: ExplorePlacesViewModel = viewModel()
    val hotelViewModel: HotelSearchViewModel = viewModel()
    val transportViewModel: TransportSearchViewModel = viewModel()
    val plannerViewModel: TripPlannerViewModel = viewModel()
    val budgetViewModel: BudgetCalculatorViewModel = viewModel()
    val routeMapViewModel: RouteMapViewModel = viewModel()
    val itineraryViewModel: ItineraryViewModel = viewModel()
    val packageComparisonViewModel: PackageComparisonViewModel = viewModel()
    val reviewsViewModel: ReviewsViewModel = viewModel()
    val emergencyViewModel: EmergencyViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()





    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        
        composable(route = Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        
        composable(route = Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        // Home/Main Flow
        composable(route = Screen.Main.route) {
            MainScreen(
                homeViewModel = homeViewModel,
                exploreViewModel = exploreViewModel,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToDestination = { id -> navController.navigate(Screen.DestinationDetail.createRoute(id)) },
                onNavigateToPlaceDetails = { id -> navController.navigate(Screen.PlaceDetail.createRoute(id)) },
                onNavigateToAttraction = { id -> navController.navigate(Screen.AttractionDetail.createRoute(id)) },
                onNavigateToHotel = { id -> navController.navigate(Screen.HotelDetail.createRoute(id)) },
                onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) },
                onNavigateToHotelsSearch = { navController.navigate(Screen.Hotels.route) },
                onNavigateToTransportSearch = { navController.navigate(Screen.Transport.route) },
                onNavigateToComparison = { navController.navigate(Screen.Comparison.route) },
                onNavigateToItinerary = { navController.navigate(Screen.ItineraryMain.route) }
            )
        }
        
        composable(route = Screen.Search.route) {
            SearchDestinationScreen(
                viewModel = searchViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDestination = { id -> navController.navigate(Screen.DestinationDetail.createRoute(id)) },
                onNavigateToAttraction = { id -> navController.navigate(Screen.AttractionDetail.createRoute(id)) },
                onNavigateToHotel = { id -> navController.navigate(Screen.HotelDetail.createRoute(id)) }
            )
        }
        
        // Destination detail screen (supports "destination/{id}" and "destination_details/{destinationId}")
        composable(
            route = Screen.DestinationDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            if (id.isNullOrBlank()) {
                com.tripdirect.ui.screens.explore.DestinationNotFoundScreen(
                    onBack = { navController.popBackStack() }
                )
            } else {
                val destinationViewModel: DestinationDetailViewModel = viewModel()
                DestinationDetailScreen(
                    destinationId = id,
                    viewModel = destinationViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAIPlanner = { destName ->
                        plannerViewModel.setDestination(destName)
                        navController.navigate(Screen.Planner.route)
                    },
                    onNavigateToBudgetCalculator = {
                        navController.navigate(Screen.Budget.route)
                    }
                )
            }
        }

        composable(
            route = "destination_details/{destinationId}",
            arguments = listOf(navArgument("destinationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("destinationId")
            if (id.isNullOrBlank()) {
                com.tripdirect.ui.screens.explore.DestinationNotFoundScreen(
                    onBack = { navController.popBackStack() }
                )
            } else {
                val destinationViewModel: DestinationDetailViewModel = viewModel()
                DestinationDetailScreen(
                    destinationId = id,
                    viewModel = destinationViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAIPlanner = { destName ->
                        plannerViewModel.setDestination(destName)
                        navController.navigate(Screen.Planner.route)
                    },
                    onNavigateToBudgetCalculator = {
                        navController.navigate(Screen.Budget.route)
                    }
                )
            }
        }
        
        composable(
            route = Screen.AttractionDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            PlaceDetailsScreen(
                placeId = id,
                viewModel = exploreViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPlanVisit = { placeName -> navController.navigate(Screen.Planner.route) },
                onViewRoute = { placeId -> navController.navigate(Screen.Route.createRoute(placeId)) }
            )
        }
        
        composable(
            route = Screen.HotelDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            HotelDetailsScreen(
                hotelId = id,
                viewModel = hotelViewModel,
                onNavigateBack = { navController.popBackStack() },
                onAddToTrip = { navController.navigate(Screen.ItineraryMain.route) }
            )
        }
        
        composable(route = Screen.Hotels.route) { 
            HotelSearchScreen(
                viewModel = hotelViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHotelDetails = { id -> navController.navigate(Screen.HotelDetail.createRoute(id)) },
                onNavigateToCompare = { navController.navigate(Screen.HotelCompare.route) }
            )
        }
        
        composable(route = Screen.HotelCompare.route) {
            HotelCompareScreen(
                viewModel = hotelViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHotelDetails = { id -> navController.navigate(Screen.HotelDetail.createRoute(id)) }
            )
        }
        
        composable(route = Screen.Transport.route) { 
            TransportSearchScreen(
                viewModel = transportViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { id -> navController.navigate(Screen.TransportDetail.createRoute(id)) },
                onNavigateToCompare = { navController.navigate(Screen.TransportCompare.route) }
            )
        }
        
        composable(
            route = Screen.TransportDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            TransportDetailsScreen(
                transportId = id,
                viewModel = transportViewModel,
                onNavigateBack = { navController.popBackStack() },
                onViewRoute = { navController.navigate(Screen.Route.route) },
                onAddToTrip = { navController.navigate(Screen.ItineraryMain.route) }
            )
        }
        
        composable(route = Screen.TransportCompare.route) {
            TransportCompareScreen(
                viewModel = transportViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { id -> navController.navigate(Screen.TransportDetail.createRoute(id)) }
            )
        }
        
        composable(
            route = Screen.PlaceDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            PlaceDetailsScreen(
                placeId = id,
                viewModel = exploreViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPlanVisit = { placeName -> navController.navigate(Screen.Planner.route) },
                onViewRoute = { placeId -> navController.navigate(Screen.Route.createRoute(placeId)) }
            )
        }
        
        composable(
            route = Screen.Route.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            PlaceholderDetailScreen("Route map for Place ID: $id")
        }
        composable(route = Screen.Planner.route) { 
            AITripPlannerScreen(
                viewModel = plannerViewModel,
                onNavigateBack = { navController.popBackStack() },
                onGenerate = { navController.navigate(Screen.PlanningProgress.route) }
            )
        }
        
        composable(route = Screen.PlanningProgress.route) {
            PlanningProgressScreen(viewModel = plannerViewModel)
            // Navigation to GeneratedTrip happens when state changes, usually triggered inside the screen or via callback.
            // But we hooked onComplete in AITripPlannerScreen... Actually, we didn't inject the navigation there correctly.
            // Let's observe the state here directly to navigate.
            androidx.compose.runtime.LaunchedEffect(Unit) {
                plannerViewModel.tripPlan.collect { plan ->
                    if (plan != null) {
                        navController.navigate(Screen.GeneratedTrip.route) {
                            popUpTo(Screen.Planner.route) { inclusive = false }
                        }
                    }
                }
            }
        }
        
        composable(route = Screen.GeneratedTrip.route) {
            GeneratedTripScreen(
                viewModel = plannerViewModel,
                onNavigateBack = { navController.popBackStack(Screen.Planner.route, false) },
                onNavigateToDay = { day -> navController.navigate(Screen.TripDayDetail.createRoute(day)) },
                onNavigateToEdit = { navController.navigate(Screen.EditTrip.route) }
            )
        }
        
        composable(
            route = Screen.TripDayDetail.route,
            arguments = listOf(navArgument("day") { type = NavType.IntType })
        ) { backStackEntry ->
            val day = backStackEntry.arguments?.getInt("day") ?: return@composable
            TripDayDetailsScreen(
                dayNumber = day,
                viewModel = plannerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.EditTrip.route) {
            EditTripScreen(
                viewModel = plannerViewModel,
                onNavigateBack = { navController.popBackStack() },
                onModificationComplete = { navController.navigate(Screen.PlanningProgress.route) }
            )
        }
        
        composable(route = Screen.Budget.route) {
            BudgetCalculatorScreen(
                viewModel = budgetViewModel,
                onNavigateBack = { navController.popBackStack() },
                onCalculate = { navController.navigate(Screen.BudgetResult.route) }
            )
        }
        
        composable(route = Screen.BudgetResult.route) {
            BudgetResultScreen(
                viewModel = budgetViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBreakdown = { navController.navigate(Screen.BudgetBreakdown.route) },
                onNavigateToCompare = { navController.navigate(Screen.BudgetCompare.route) }
            )
        }
        
        composable(route = Screen.BudgetBreakdown.route) {
            BudgetBreakdownScreen(
                viewModel = budgetViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.BudgetCompare.route) {
            BudgetCompareScreen(
                viewModel = budgetViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.RouteMap.route) {
            RouteMapScreen(
                viewModel = routeMapViewModel,
                budgetViewModel = budgetViewModel,
                onNavigateBack = { navController.popBackStack() },
                onStartNavigation = { navController.navigate(Screen.Navigation.route) },
                onNavigateToDetails = { navController.navigate(Screen.RouteDetails.route) }
            )
        }
        
        composable(route = Screen.RouteDetails.route) {
            RouteDetailsScreen(
                viewModel = routeMapViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.Navigation.route) {
            NavigationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.ItineraryMain.route) {
            ItineraryScreen(
                viewModel = itineraryViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) },
                onNavigateToRoute = { navController.navigate(Screen.RouteMap.route) },
                onNavigateToDayDetails = { navController.navigate(Screen.ItineraryDayDetails.route) },
                onNavigateToEdit = { navController.navigate(Screen.ItineraryEdit.route) },
                onNavigateToSummary = { navController.navigate(Screen.ItinerarySummary.route) },
                onGenerateItinerary = { navController.navigate(Screen.Planner.route) }
            )
        }
        
        composable(route = Screen.ItineraryDayDetails.route) {
            DayDetailsScreen(
                viewModel = itineraryViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { navController.navigate(Screen.RouteMap.route) }
            )
        }
        
        composable(route = Screen.ItineraryEdit.route) {
            EditItineraryScreen(
                viewModel = itineraryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.ItinerarySummary.route) {
            ItinerarySummaryScreen(
                viewModel = itineraryViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) }
            )
        }

        
        composable(route = Screen.Comparison.route) {
            PackageComparisonScreen(
                viewModel = packageComparisonViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBreakdown = { navController.navigate(Screen.PackageBreakdown.route) },
                onImportToItinerary = { navController.navigate(Screen.ItineraryMain.route) },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) }
            )
        }

        composable(route = Screen.PackageBreakdown.route) {
            PackageBreakdownScreen(
                viewModel = packageComparisonViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) }
            )
        }

        composable(route = Screen.Reviews.route) {
            ReviewsScreen(
                viewModel = reviewsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWriteReview = { navController.navigate(Screen.WriteReview.route) },
                onNavigateToReviewDetails = { navController.navigate(Screen.ReviewDetails.route) },
                onNavigateToMyReviews = { navController.navigate(Screen.MyReviews.route) },
                onNavigateToPlace = { navController.navigate(Screen.PlaceDetail.createRoute("ooty_botanical_garden")) },
                onNavigateToRoute = { navController.navigate(Screen.RouteMap.route) }
            )
        }

        composable(route = Screen.WriteReview.route) {
            WriteReviewScreen(
                viewModel = reviewsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.ReviewDetails.route) {
            ReviewDetailsScreen(
                viewModel = reviewsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.MyReviews.route) {
            MyReviewsScreen(
                viewModel = reviewsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Emergency.route) {
            EmergencyScreen(
                viewModel = emergencyViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSosActive = { navController.navigate(Screen.SOSActive.route) },
                onNavigateToNearbyServices = { navController.navigate(Screen.NearbyServices.route) },
                onNavigateToContacts = { navController.navigate(Screen.EmergencyContacts.route) },
                onNavigateToSafetyTips = { navController.navigate(Screen.SafetyTips.route) },
                onNavigateToRoute = { navController.navigate(Screen.RouteMap.route) }
            )
        }

        composable(route = Screen.SOSActive.route) {
            SOSActiveScreen(
                viewModel = emergencyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.NearbyServices.route) {
            NearbyServicesScreen(
                viewModel = emergencyViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { navController.navigate(Screen.RouteMap.route) }
            )
        }

        composable(route = Screen.EmergencyContacts.route) {
            EmergencyContactsScreen(
                viewModel = emergencyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.SafetyTips.route) {
            SafetyTipsScreen(
                viewModel = emergencyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.ProfileMain.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToTrips = { navController.navigate(Screen.MyTrips.route) },
                onNavigateToSavedPlaces = { navController.navigate(Screen.SavedPlaces.route) },
                onNavigateToItineraries = { navController.navigate(Screen.ItineraryMain.route) },
                onNavigateToReviews = { navController.navigate(Screen.MyReviews.route) },
                onNavigateToTravelPreferences = { navController.navigate(Screen.TravelPreferences.route) },
                onNavigateToBudgetPreferences = { navController.navigate(Screen.BudgetPreferences.route) },
                onNavigateToEmergencyContacts = { navController.navigate(Screen.EmergencyContacts.route) },
                onNavigateToLanguage = { navController.navigate(Screen.Language.route) },
                onNavigateToNotifications = { navController.navigate(Screen.NotificationSettings.route) },
                onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                onNavigateToHelpSupport = { navController.navigate(Screen.HelpSupport.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }


        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.TravelPreferences.route) {
            TravelPreferencesScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.BudgetPreferences.route) {
            BudgetPreferencesScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.SavedPlaces.route) {
            SavedPlacesScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlaceDetails = { id -> navController.navigate(Screen.PlaceDetail.createRoute(id)) }
            )
        }

        composable(route = Screen.MyTrips.route) {
            MyTripsScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToItinerary = { navController.navigate(Screen.ItineraryMain.route) }
            )
        }

        composable(route = Screen.NotificationSettings.route) {
            NotificationSettingsScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Language.route) {
            LanguageScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Privacy.route) {
            PrivacyScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.HelpSupport.route) {
            HelpSupportScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.About.route) {
            AboutScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }




    }
}

@Composable
fun PlaceholderDetailScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title)
    }
}
