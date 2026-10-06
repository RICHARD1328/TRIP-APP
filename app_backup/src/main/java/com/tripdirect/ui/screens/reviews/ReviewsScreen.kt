package com.tripdirect.ui.screens.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.Review
import com.tripdirect.ui.components.reviews.EmptyReviewsView
import com.tripdirect.ui.components.reviews.RatingSummaryCard
import com.tripdirect.ui.components.reviews.ReportReviewSheet
import com.tripdirect.ui.components.reviews.ReviewCard
import com.tripdirect.ui.components.reviews.ReviewFilterBar
import com.tripdirect.ui.components.reviews.ReviewHeader
import com.tripdirect.ui.components.reviews.TravelerInsightCard
import com.tripdirect.viewmodel.ReviewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    viewModel: ReviewsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWriteReview: () -> Unit,
    onNavigateToReviewDetails: () -> Unit,
    onNavigateToMyReviews: () -> Unit,
    onNavigateToPlace: () -> Unit,
    onNavigateToRoute: () -> Unit
) {
    val summary by viewModel.reviewSummary.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val filteredReviews = viewModel.getFilteredReviews()

    var isSearchActive by remember { mutableStateOf(false) }
    var reportingReviewId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.searchReviews(it) },
                                placeholder = { Text("Search reviews...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        } else {
                            Text("Traveler Reviews", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isSearchActive) {
                                isSearchActive = false
                                viewModel.searchReviews("")
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(if (isSearchActive) Icons.Default.Close else Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onNavigateToMyReviews) {
                            Icon(Icons.Default.Person, contentDescription = "My Reviews")
                        }
                    }
                )
                ReviewFilterBar(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToWriteReview,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Write Review")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (filteredReviews.isEmpty()) {
                EmptyReviewsView(onWriteClick = onNavigateToWriteReview)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    summary?.let { sum ->
                        item {
                            ReviewHeader(
                                summary = sum,
                                onViewPlaceClick = onNavigateToPlace,
                                onViewRouteClick = onNavigateToRoute
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            RatingSummaryCard(summary = sum)
                            Spacer(modifier = Modifier.height(4.dp))
                            TravelerInsightCard(summary = sum)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(filteredReviews) { review ->
                        ReviewCard(
                            review = review,
                            onCardClick = {
                                viewModel.selectReview(review)
                                onNavigateToReviewDetails()
                            },
                            onHelpfulClick = { viewModel.markHelpful(review.id) },
                            onReportClick = { reportingReviewId = review.id }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    reportingReviewId?.let { reviewId ->
        ReportReviewSheet(
            onDismiss = { reportingReviewId = null },
            onReport = { reason ->
                viewModel.reportReview(reviewId, reason)
                reportingReviewId = null
            }
        )
    }
}
