package com.tripdirect.ui.screens.packagecomp

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.ui.components.packagecomp.CategoryTabRow
import com.tripdirect.ui.components.packagecomp.ComparisonFeatureCard
import com.tripdirect.ui.components.packagecomp.ComparisonHeader
import com.tripdirect.ui.components.packagecomp.CostVsPackageSummary
import com.tripdirect.ui.components.packagecomp.CustomizationDialog
import com.tripdirect.ui.components.packagecomp.ItemizedCostBarChart
import com.tripdirect.ui.components.packagecomp.SavingsCard
import com.tripdirect.viewmodel.PackageComparisonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageComparisonScreen(
    viewModel: PackageComparisonViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBreakdown: () -> Unit,
    onImportToItinerary: () -> Unit,
    onNavigateToBudget: () -> Unit
) {
    val context = LocalContext.current
    val comparison by viewModel.comparison.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var showCustomizeDialog by remember { mutableStateOf(false) }
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
            TopAppBar(
                title = { Text("Package Comparison") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCustomizeDialog = true }) {
                        Icon(Icons.Default.Tune, contentDescription = "Customize")
                    }

                    IconButton(onClick = onNavigateToBreakdown) {
                        Icon(Icons.Default.PieChart, contentDescription = "Cost Breakdown")
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, viewModel.getShareableComparisonText())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Comparison"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            if (comparison != null) {
                Surface(tonalElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateToBudget,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Budget Calc")
                        }
                        Button(
                            onClick = onImportToItinerary,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import Self-Plan")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (comparison != null) {
                val comp = comparison!!
                val filteredFeatures = comp.features.filter {
                    selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        ComparisonHeader(
                            destination = comp.destination,
                            days = comp.durationDays,
                            travelers = comp.travelersCount
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SavingsCard(
                            savingsAmount = comp.savingsAmount,
                            savingsPercentage = comp.savingsPercentage
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CostVsPackageSummary(comp = comp)
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryTabRow(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.filterCategory(it) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }

                    items(filteredFeatures) { feature ->
                        ComparisonFeatureCard(feature = feature)
                    }

                    item {
                        ItemizedCostBarChart(breakdown = comp.costBreakdown)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onNavigateToBreakdown,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Full Itemized Cost Breakdown")
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showCustomizeDialog && comparison != null) {
        CustomizationDialog(
            currentStay = comparison!!.selfPlan.stayCost,
            currentTransport = comparison!!.selfPlan.transportCost,
            onDismiss = { showCustomizeDialog = false },
            onApply = { stay, transport ->
                viewModel.customizeSelfPlanCosts(stay, transport)
            }
        )
    }
}
