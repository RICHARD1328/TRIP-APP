package com.tripdirect.ui.screens.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.TripPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(
    viewModel: TripPlannerViewModel,
    onNavigateBack: () -> Unit,
    onModificationComplete: () -> Unit
) {
    var instruction by remember { mutableStateOf("") }
    val isGenerating by viewModel.isGenerating.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change your plan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = instruction,
                        onValueChange = { instruction = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tell AI what you'd like to change") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (instruction.isNotBlank()) {
                                viewModel.modifyTrip(instruction)
                                onModificationComplete()
                            }
                        },
                        enabled = instruction.isNotBlank() && !isGenerating
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Update Trip", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))

            val quickActions = listOf(
                "Make it cheaper",
                "Add more places",
                "Remove crowded places",
                "Add more food spots",
                "Make it relaxing",
                "Add adventure activities",
                "Reduce travel time"
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickActions) { action ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.modifyTrip(action)
                            onModificationComplete()
                        }
                    ) {
                        Text(
                            text = action,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
