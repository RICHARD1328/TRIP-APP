package com.tripdirect.ui.screens.route

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.RouteSegment
import com.tripdirect.viewmodel.RouteMapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    viewModel: RouteMapViewModel,
    onNavigateBack: () -> Unit
) {
    val route by viewModel.currentRoute.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentRoute = route ?: return@Scaffold
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Start Point
            item {
                val start = currentRoute.locations.firstOrNull()
                if (start != null) {
                    LocationTimelineItem(name = start.name, isStart = true, isEnd = false)
                }
            }
            
            // Segments
            itemsIndexed(route!!.segments) { index, segment ->
                SegmentTimelineItem(segment = segment)
                LocationTimelineItem(
                    name = segment.to.name,
                    isStart = false,
                    isEnd = index == route!!.segments.size - 1
                )
            }
        }
    }
}

@Composable
fun LocationTimelineItem(name: String, isStart: Boolean, isEnd: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            if (!isStart) {
                Canvas(modifier = Modifier.width(2.dp).weight(1f)) {
                    drawLine(Color.Gray, Offset(0f, 0f), Offset(0f, size.height), 2f * density)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(if (isStart || isEnd) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
            )
            
            if (!isEnd) {
                Canvas(modifier = Modifier.width(2.dp).weight(1f)) {
                    drawLine(Color.Gray, Offset(0f, 0f), Offset(0f, size.height), 2f * density)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
fun SegmentTimelineItem(segment: RouteSegment) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Canvas(modifier = Modifier.width(2.dp).fillMaxHeight()) {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                drawLine(Color.Gray, Offset(0f, 0f), Offset(0f, size.height), 2f * density, pathEffect = pathEffect)
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(String.format("%.1f km", segment.distanceKm), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("${segment.durationMinutes} min", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(segment.transportMode.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text("₹${segment.estimatedCost}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
