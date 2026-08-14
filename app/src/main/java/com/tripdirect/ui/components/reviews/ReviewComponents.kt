package com.tripdirect.ui.components.reviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.Review
import com.tripdirect.data.model.ReviewCategory
import com.tripdirect.data.model.ReviewSortOption
import com.tripdirect.data.model.ReviewSummary

@Composable
fun ReviewHeader(
    summary: ReviewSummary,
    onViewPlaceClick: () -> Unit,
    onViewRouteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = summary.entityName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = summary.entityLocation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFA000))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${summary.averageRating}", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onViewPlaceClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Place")
                }
                Button(
                    onClick = onViewRouteClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Route")
                }
            }
        }
    }
}

@Composable
fun RatingSummaryCard(summary: ReviewSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating big number column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    text = "${summary.averageRating}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("out of 5", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${summary.totalReviews} reviews", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Rating distribution bars
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RatingBarRow(stars = "5★", percentage = summary.fiveStarPct)
                RatingBarRow(stars = "4★", percentage = summary.fourStarPct)
                RatingBarRow(stars = "3★", percentage = summary.threeStarPct)
                RatingBarRow(stars = "2★", percentage = summary.twoStarPct)
                RatingBarRow(stars = "1★", percentage = summary.oneStarPct)
            }
        }
    }
}

@Composable
fun RatingBarRow(stars: String, percentage: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(stars, style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(28.dp))
        Spacer(modifier = Modifier.width(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("$percentage%", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(32.dp))
    }
}

@Composable
fun TravelerInsightCard(summary: ReviewSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("✨ Traveler Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Most travelers liked:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            summary.pros.forEach { pro ->
                Text(" • $pro", style = MaterialTheme.typography.bodySmall)
            }
            if (summary.cons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Common complaints:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                summary.cons.forEach { con ->
                    Text(" • $con", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ReviewFilterBar(
    selectedCategory: ReviewCategory,
    onCategorySelected: (ReviewCategory) -> Unit
) {
    val categories = listOf(
        Pair(ReviewCategory.ALL, "All"),
        Pair(ReviewCategory.ATTRACTION, "Places"),
        Pair(ReviewCategory.HOTEL, "Hotels"),
        Pair(ReviewCategory.RESTAURANT, "Food"),
        Pair(ReviewCategory.TRANSPORT, "Transport"),
        Pair(ReviewCategory.ACTIVITY, "Activities")
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (cat, label) ->
            FilterChip(
                selected = selectedCategory == cat,
                onClick = { onCategorySelected(cat) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onCardClick: () -> Unit,
    onHelpfulClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // User row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(review.userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (review.verifiedVisit) {
                            Text("✓ Verified Visit", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(review.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Stars & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFA000)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(review.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onHelpfulClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Helpful (${review.helpfulCount})", style = MaterialTheme.typography.labelSmall)
                }

                TextButton(
                    onClick = onReportClick,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ReportReviewSheet(
    onDismiss: () -> Unit,
    onReport: (reason: String) -> Unit
) {
    val reasons = listOf("Spam", "Fake information", "Offensive content", "Advertising", "Other")
    var selectedReason by remember { mutableStateOf(reasons[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Review") },
        text = {
            Column {
                Text("Why are you reporting this review?", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 6.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(reason)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onReport(selectedReason)
                    onDismiss()
                }
            ) {
                Text("Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EmptyReviewsView(onWriteClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No reviews yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Be the first traveler to share your experience with the TripDirect community.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onWriteClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Write a Review")
            }
        }
    }
}
