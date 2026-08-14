package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.PackageComparison
import com.tripdirect.data.repository.MockPackageRepository
import com.tripdirect.data.repository.PackageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackageComparisonViewModel(
    private val repository: PackageRepository = MockPackageRepository()
) : ViewModel() {

    private val _comparison = MutableStateFlow<PackageComparison?>(null)
    val comparison: StateFlow<PackageComparison?> = _comparison.asStateFlow()

    private val _selectedTab = MutableStateFlow("Overview")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        loadComparison("Ooty", 3, 2)
    }

    fun loadComparison(destination: String, durationDays: Int, travelers: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.getComparison(destination, durationDays, travelers)
                _comparison.value = data
            } catch (e: Exception) {
                _statusMessage.value = "Failed to load package comparison."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTab(tabName: String) {
        _selectedTab.value = tabName
    }

    fun filterCategory(categoryName: String) {
        _selectedCategoryFilter.value = categoryName
    }

    fun customizeSelfPlanCosts(stayCost: Int, transportCost: Int) {
        val current = _comparison.value ?: return
        viewModelScope.launch {
            val updated = repository.customizeSelfPlan(current, stayCost, transportCost)
            _comparison.value = updated
            _statusMessage.value = "Self-plan estimate updated!"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun getShareableComparisonText(): String {
        val comp = _comparison.value ?: return "Package Comparison - TripDirect"
        val sb = StringBuilder()
        sb.append("TRIPDIRECT PACKAGE COMPARISON — ${comp.destination.uppercase()}\n\n")
        sb.append("Tourist Package Price: ₹${comp.touristPackage.totalPrice}\n")
        sb.append("TripDirect Self-Plan Estimated Price: ₹${comp.selfPlan.totalCost}\n")
        sb.append("ESTIMATED SAVINGS: ₹${comp.savingsAmount} (${comp.savingsPercentage}% lower)\n\n")
        sb.append("Why Self-Plan?\n")
        sb.append("• 0 Agency Broker Commission\n")
        sb.append("• 100% Schedule & Hotel Choice Flexibility\n")
        sb.append("• Free & Flexible Cancellation\n")
        return sb.toString()
    }
}
