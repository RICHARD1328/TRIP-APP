package com.tripdirect.ui.screens.reviews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.ReviewCategory
import com.tripdirect.viewmodel.ReviewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    viewModel: ReviewsViewModel,
    onNavigateBack: () -> Unit
) {
    var rating by remember { mutableFloatStateOf(0f) }
    var title by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ReviewCategory.ATTRACTION) }
    var photoCount by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Write a Review") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("How was your experience?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Star selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 1..5) {
                    IconButton(onClick = { rating = i.toFloat() }) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $i",
                            modifier = Modifier.size(36.dp),
                            tint = Color(0xFFFFA000)
                        )
                    }
                }
            }

            if (rating > 0) {
                Text(
                    text = when (rating.toInt()) {
                        5 -> "⭐⭐⭐⭐⭐ Excellent"
                        4 -> "⭐⭐⭐⭐ Very Good"
                        3 -> "⭐⭐⭐ Average"
                        2 -> "⭐⭐ Poor"
                        else -> "⭐ Terrible"
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider()

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Review Title") },
                placeholder = { Text("e.g. Great family outing / Peaceful view") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Your Review") },
                placeholder = { Text("Share details about cleanliness, service, scenery, or crowd levels...") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            // Photos section
            Text("Add Photos (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..photoCount) {
                    Card(modifier = Modifier.size(64.dp)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("📷 $i")
                        }
                    }
                }
                if (photoCount < 3) {
                    OutlinedIconButton(
                        onClick = { photoCount++ },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add photo")
                    }
                }
            }

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (rating == 0f) {
                        errorMessage = "Please select a star rating."
                    } else if (title.isBlank()) {
                        errorMessage = "Please enter a review title."
                    } else if (comment.isBlank()) {
                        errorMessage = "Please write your review comment."
                    } else {
                        viewModel.submitReview(
                            entityId = "ooty_botanical_garden",
                            entityName = "Ooty Botanical Garden",
                            category = selectedCategory,
                            rating = rating,
                            title = title,
                            comment = comment,
                            photos = if (photoCount > 0) List(photoCount) { "photo_$it" } else emptyList()
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = rating > 0 && title.isNotBlank() && comment.isNotBlank()
            ) {
                Text("Submit Review")
            }
        }
    }
}
