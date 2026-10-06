package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.BudgetBreakdown
import com.tripdirect.data.model.BudgetInput
import com.tripdirect.data.repository.BudgetRepository
import com.tripdirect.data.repository.MockBudgetRepository
import com.tripdirect.domain.BudgetCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BudgetCalculatorViewModel(
    private val repository: BudgetRepository = MockBudgetRepository()
) : ViewModel() {

    private val _destination = MutableStateFlow("Ooty")
    val destination: StateFlow<String> = _destination.asStateFlow()

    private val _days = MutableStateFlow(3)
    val days: StateFlow<Int> = _days.asStateFlow()

    private val _adults = MutableStateFlow(2)
    val adults: StateFlow<Int> = _adults.asStateFlow()

    private val _children = MutableStateFlow(0)
    val children: StateFlow<Int> = _children.asStateFlow()

    private val _totalBudget = MutableStateFlow(10000)
    val totalBudget: StateFlow<Int> = _totalBudget.asStateFlow()

    private val _transportCost = MutableStateFlow(1700)
    val transportCost: StateFlow<Int> = _transportCost.asStateFlow()

    private val _accommodationPerNight = MutableStateFlow(1500)
    val accommodationPerNight: StateFlow<Int> = _accommodationPerNight.asStateFlow()

    private val _foodPerPersonPerDay = MutableStateFlow(300)
    val foodPerPersonPerDay: StateFlow<Int> = _foodPerPersonPerDay.asStateFlow()

    private val _attractionCost = MutableStateFlow(250)
    val attractionCost: StateFlow<Int> = _attractionCost.asStateFlow()

    private val _activityCost = MutableStateFlow(500)
    val activityCost: StateFlow<Int> = _activityCost.asStateFlow()

    private val _localTransportPerDay = MutableStateFlow(400)
    val localTransportPerDay: StateFlow<Int> = _localTransportPerDay.asStateFlow()

    private val _miscellaneousCost = MutableStateFlow(500)
    val miscellaneousCost: StateFlow<Int> = _miscellaneousCost.asStateFlow()

    private val _budgetBreakdown = MutableStateFlow<BudgetBreakdown?>(null)
    val budgetBreakdown: StateFlow<BudgetBreakdown?> = _budgetBreakdown.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setDestination(dest: String) { _destination.value = dest }
    fun setDays(d: Int) { if (d >= 1) _days.value = d }
    fun setAdults(count: Int) { if (count >= 1) _adults.value = count }
    fun setChildren(count: Int) { if (count >= 0) _children.value = count }
    
    fun setTotalBudget(value: String) {
        val num = value.replace(Regex("[^0-9]"), "").toIntOrNull()
        if (num != null && num >= 0) _totalBudget.value = num
    }
    
    fun setTransportCost(value: String) { _transportCost.value = value.toIntOrNull() ?: 0 }
    fun setAccommodationPerNight(value: String) { _accommodationPerNight.value = value.toIntOrNull() ?: 0 }
    fun setFoodPerPersonPerDay(value: String) { _foodPerPersonPerDay.value = value.toIntOrNull() ?: 0 }
    fun setAttractionCost(value: String) { _attractionCost.value = value.toIntOrNull() ?: 0 }
    fun setActivityCost(value: String) { _activityCost.value = value.toIntOrNull() ?: 0 }
    fun setLocalTransportPerDay(value: String) { _localTransportPerDay.value = value.toIntOrNull() ?: 0 }
    fun setMiscellaneousCost(value: String) { _miscellaneousCost.value = value.toIntOrNull() ?: 0 }

    fun calculateBudget(onComplete: () -> Unit) {
        if (_days.value < 1 || _adults.value < 1 || _totalBudget.value <= 0) {
            _errorMessage.value = "Please ensure days, travelers, and budget are valid."
            return
        }
        
        _errorMessage.value = null

        val input = BudgetInput(
            destination = _destination.value,
            days = _days.value,
            adults = _adults.value,
            children = _children.value,
            totalBudget = _totalBudget.value,
            transportCost = _transportCost.value,
            accommodationPerNight = _accommodationPerNight.value,
            foodPerPersonPerDay = _foodPerPersonPerDay.value,
            attractionCost = _attractionCost.value,
            activityCost = _activityCost.value,
            localTransportPerDay = _localTransportPerDay.value,
            miscellaneousCost = _miscellaneousCost.value
        )

        _budgetBreakdown.value = BudgetCalculator.calculate(input)
        onComplete()
    }
    
    fun loadSuggestedBudget(dest: String) {
        viewModelScope.launch {
            val suggested = repository.getSuggestedBudget(dest, _days.value, _adults.value + _children.value)
            _destination.value = suggested.destination
            _totalBudget.value = suggested.totalBudget
            _transportCost.value = suggested.transportCost
            _accommodationPerNight.value = suggested.accommodationPerNight
            _foodPerPersonPerDay.value = suggested.foodPerPersonPerDay
            _attractionCost.value = suggested.attractionCost
            _activityCost.value = suggested.activityCost
            _localTransportPerDay.value = suggested.localTransportPerDay
        }
    }

    fun addExternalTransportExpense(cost: Int) {
        _transportCost.value += cost
    }
}


