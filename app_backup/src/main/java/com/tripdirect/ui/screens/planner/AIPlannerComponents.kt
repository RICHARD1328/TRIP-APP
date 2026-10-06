package com.tripdirect.ui.screens.planner

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tripdirect.data.model.*
import com.tripdirect.ui.screens.trips.SafeAsyncImage
import java.text.SimpleDateFormat
import java.util.*

// 2. HEADER ROBOT ILLUSTRATION
@Composable
fun AIRobotIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE91E63).copy(alpha = 0.35f),
                        Color(0xFF9C27B0).copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(64.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            
            // Antenna Sparkle Glow
            drawCircle(
                color = Color(0xFFE91E63),
                radius = 6f,
                center = Offset(center.x, 8f)
            )
            drawLine(
                color = Color(0xFFCE93D8),
                start = Offset(center.x, 8f),
                end = Offset(center.x, 20f),
                strokeWidth = 4f
            )

            // Head (Rounded Robot)
            drawRoundRect(
                color = Color(0xFF2C243B),
                topLeft = Offset(12f, 20f),
                size = Size(size.width - 24f, 36f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = Color(0xFFE91E63),
                topLeft = Offset(12f, 20f),
                size = Size(size.width - 24f, 36f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
            )

            // Friendly Glowing Eyes
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = 5f,
                center = Offset(center.x - 12f, 34f)
            )
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = 5f,
                center = Offset(center.x + 12f, 34f)
            )

            // Smiling Mouth Line
            drawArc(
                color = Color.White,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(center.x - 8f, 38f),
                size = Size(16f, 10f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
            )

            // Floating mini travel star
            drawCircle(
                color = Color(0xFFFFD54F),
                radius = 3f,
                center = Offset(center.x + 24f, 16f)
            )
        }
    }
}

@Composable
fun AIPlannerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AI Planner",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "✨", fontSize = 24.sp)
            }
            Text(
                text = "Your AI Travel Assistant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Plan smarter. Travel better. Save more.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AIRobotIllustration()
    }
}

// 3. MAIN AI TRIP PLANNER CARD
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainTripPlannerCard(
    destination: String,
    onDestinationChange: (String) -> String,
    startDate: String,
    endDate: String,
    onSelectDatesClick: () -> Unit,
    adultsCount: Int,
    onAdultsChange: (Int) -> Unit,
    childrenCount: Int,
    onChildrenChange: (Int) -> Unit,
    budget: String,
    onBudgetChange: (String) -> Unit,
    selectedPreferences: Set<String>,
    onTogglePreference: (String) -> Unit,
    selectedStyle: String,
    onStyleSelect: (String) -> Unit,
    onGenerateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Where do you want to go?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Destination Field
            OutlinedTextField(
                value = destination,
                onValueChange = { onDestinationChange(it) },
                placeholder = { Text("📍 Enter destination or location") },
                trailingIcon = {
                    IconButton(onClick = { onDestinationChange("Ooty") }) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Current Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Date Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSelectDatesClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Start Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(startDate.ifEmpty { "Select" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onSelectDatesClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("End Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(endDate.ifEmpty { "Select" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Traveler Selection Counter
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Travelers", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Adults", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Age 12+", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (adultsCount > 1) onAdultsChange(adultsCount - 1) },
                                modifier = Modifier.size(32.dp),
                                enabled = adultsCount > 1
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("$adultsCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                            FilledIconButton(
                                onClick = { onAdultsChange(adultsCount + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Children", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Age 0-11", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (childrenCount > 0) onChildrenChange(childrenCount - 1) },
                                modifier = Modifier.size(32.dp),
                                enabled = childrenCount > 0
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("$childrenCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                            FilledIconButton(
                                onClick = { onChildrenChange(childrenCount + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Budget Input & Quick Chips
            Column {
                Text("What's your budget?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = budget,
                    onValueChange = { onBudgetChange(it) },
                    placeholder = { Text("e.g. ₹10,000") },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("₹5K", "₹10K", "₹20K", "₹50K+").forEach { chipVal ->
                        val cleanVal = when (chipVal) {
                            "₹5K" -> "₹5,000"
                            "₹10K" -> "₹10,000"
                            "₹20K" -> "₹20,000"
                            else -> "₹50,000"
                        }
                        FilterChip(
                            selected = budget == cleanVal,
                            onClick = { onBudgetChange(cleanVal) },
                            label = { Text(chipVal) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Travel Preferences Chips
            Column {
                Text("Travel Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val preferencesList = listOf("Nature", "Adventure", "Food", "Culture", "Shopping", "Relaxation", "Photography", "History", "Nightlife", "Family")
                    preferencesList.forEach { pref ->
                        val isSelected = selectedPreferences.contains(pref)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onTogglePreference(pref) },
                            label = { Text(if (isSelected) "$pref ✓" else pref) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Travel Style Selector
            Column {
                Text("Travel Style", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Budget", "Balanced", "Comfort", "Luxury").forEach { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { onStyleSelect(style) },
                            label = { Text(style) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Prominent Gradient Generate Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE91E63),
                                Color(0xFF9C27B0)
                            )
                        )
                    )
                    .clickable { onGenerateClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✨ Generate AI Plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}

// 10. QUICK AI TOOLS
data class QuickToolItem(
    val title: String,
    val emoji: String,
    val onClick: () -> Unit
)

@Composable
fun QuickToolsSection(tools: List<QuickToolItem>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Quick Tools",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tools) { tool ->
                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .height(100.dp)
                        .clickable { tool.onClick() },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = tool.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = tool.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// 16. POPULAR ITINERARIES
@Composable
fun PopularItinerariesSection(
    itineraries: List<PopularItinerary>,
    onSelectItinerary: (PopularItinerary) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐ Popular Itineraries",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* View All */ }) {
                Text("View All →", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(itineraries) { item ->
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .clickable { onSelectItinerary(item) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SafeAsyncImage(
                            imageUrl = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.duration,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${item.startingPrice} onwards",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// 18. SMART TOOLS
@Composable
fun SmartToolsGrid(
    smartTools: List<SmartToolItem>,
    onToolClick: (SmartToolItem) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "⚡ Smart Tools",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // 2-column grid using Chunked Rows for maximum layout stability inside LazyColumn
        smartTools.chunked(2).forEach { rowTools ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowTools.forEach { tool ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clickable { onToolClick(tool) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = tool.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = tool.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = tool.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                if (rowTools.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// 27. ASK AI ANYTHING BANNER
@Composable
fun AskAIChatBanner(onOpenChat: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🤖 ✨", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ask AI Anything About Travel",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Get instant answers, recommendations and custom plans tailored just for you.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onOpenChat,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("💬 Chat with AI", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 28. AI CHAT DIALOG
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatDialog(
    onDismiss: () -> Unit
) {
    var userQuery by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "AI", "Hello! I'm your TripDirect AI Assistant. How can I help you plan your next trip?"),
            ChatMessage("2", "USER", "How can I travel to Ooty from Chennai under ₹8,000?"),
            ChatMessage("3", "AI", "Here's a budget-friendly plan for Ooty under ₹8,000:\n\n• Train (Shatabdi/Nilgiri Express): ₹1,200 return\n• Resort/Homestay (2 nights): ₹3,000\n• Local Food & Tea: ₹1,500\n• Sightseeing (Lake, Tea Garden): ₹800\nTotal Estimated Cost: ₹6,500 (Save ~₹3,500 vs agent packages!)")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💬 TripDirect AI Assistant", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(modifier = Modifier.height(340.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        messages.forEach { msg ->
                            val isUser = msg.sender == "USER"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isUser) 14.dp else 2.dp,
                                        bottomEnd = if (isUser) 2.dp else 14.dp
                                    ),
                                    color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.widthIn(max = 240.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = userQuery,
                        onValueChange = { userQuery = it },
                        placeholder = { Text("Ask about hotels, routes, budget...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (userQuery.isNotBlank()) {
                                messages.add(ChatMessage("${messages.size + 1}", "USER", userQuery))
                                val q = userQuery.lowercase()
                                userQuery = ""
                                val response = when {
                                    q.contains("hotel") || q.contains("stay") -> "Recommended stays in Ooty start from ₹1,500/night at Ooty View Resort with free Wi-Fi and Breakfast!"
                                    q.contains("budget") || q.contains("cost") -> "Average daily self-planned budget in Ooty is ₹2,000 - ₹3,500 including food and local cabs."
                                    else -> "TripDirect helps you book directly without middleman fees. You can save up to 30% by self-planning your itinerary!"
                                }
                                messages.add(ChatMessage("${messages.size + 1}", "AI", response))
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}
