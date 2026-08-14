package com.tripdirect.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.AccommodationType
import com.tripdirect.data.model.TravelPace
import com.tripdirect.data.model.TravelStyle
import com.tripdirect.viewmodel.ProfileViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TravelPreferencesScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val prefs by viewModel.travelPreferences.collectAsState()

    var selectedStyles by remember { mutableStateOf(prefs.styles) }
    var selectedInterests by remember { mutableStateOf(prefs.interests) }
    var selectedPace by remember { mutableStateOf(prefs.pace) }
    var selectedAccommodation by remember { mutableStateOf(prefs.accommodation) }

    val allInterests = listOf("Nature", "Beaches", "Mountains", "History", "Food", "Shopping", "Wildlife", "Adventure", "Culture")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Preferences") },
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
            Text("Travel Style", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TravelStyle.entries.forEach { style ->
                    FilterChip(
                        selected = selectedStyles.contains(style),
                        onClick = {
                            selectedStyles = if (selectedStyles.contains(style)) selectedStyles - style else selectedStyles + style
                        },
                        label = { Text(style.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            HorizontalDivider()

            Text("Interests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                allInterests.forEach { interest ->
                    FilterChip(
                        selected = selectedInterests.contains(interest),
                        onClick = {
                            selectedInterests = if (selectedInterests.contains(interest)) selectedInterests - interest else selectedInterests + interest
                        },
                        label = { Text(interest) }
                    )
                }
            }

            HorizontalDivider()

            Text("Travel Pace", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TravelPace.entries.forEach { pace ->
                    FilterChip(
                        selected = selectedPace == pace,
                        onClick = { selectedPace = pace },
                        label = { Text(pace.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            HorizontalDivider()

            Text("Accommodation Preference", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccommodationType.entries.forEach { acc ->
                    FilterChip(
                        selected = selectedAccommodation == acc,
                        onClick = { selectedAccommodation = acc },
                        label = { Text(acc.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateTravelPreferences(
                        prefs.copy(
                            styles = selectedStyles,
                            interests = selectedInterests,
                            pace = selectedPace,
                            accommodation = selectedAccommodation
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Preferences")
            }
        }
    }
}
