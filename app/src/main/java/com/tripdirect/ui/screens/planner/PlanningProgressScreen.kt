package com.tripdirect.ui.screens.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.PlanningStep
import com.tripdirect.viewmodel.TripPlannerViewModel

@Composable
fun PlanningProgressScreen(viewModel: TripPlannerViewModel) {
    val currentStep by viewModel.planningStep.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Creating your trip...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val steps = PlanningStep.values().filter { it != PlanningStep.COMPLETED }
        
        steps.forEach { step ->
            val isCompleted = currentStep != null && step.ordinal < currentStep!!.ordinal
            val isActive = currentStep == step
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                } else if (isActive) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.Circle, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                val text = when (step) {
                    PlanningStep.ANALYZING_PREFERENCES -> "Understanding your preferences"
                    PlanningStep.FINDING_PLACES -> "Finding attractions"
                    PlanningStep.FINDING_HOTELS -> "Checking accommodation"
                    PlanningStep.FINDING_TRANSPORT -> "Finding transport"
                    PlanningStep.OPTIMIZING_ROUTE -> "Optimizing route"
                    PlanningStep.CALCULATING_BUDGET -> "Calculating budget"
                    PlanningStep.GENERATING_ITINERARY -> "Generating itinerary"
                    else -> ""
                }
                
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isActive || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isActive || isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
