package com.example.expensetracker.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.utils.AppConstants
import com.example.expensetracker.utils.todayRange
import kotlinx.coroutines.launch

class ExpenseEntryViewModel(private val repo: ExpenseRepository) : ViewModel() {

    var title by mutableStateOf(value = "")
        internal set
    var amount by mutableStateOf(value = "")
        internal set
    var category by mutableStateOf(value = AppConstants.FOOD)
    var notes by mutableStateOf(value = "")
    var imageUri by mutableStateOf<String?>(value = null)
    var totalToday by mutableDoubleStateOf(value = 0.0)
        private set

    init {
        viewModelScope.launch {
            val (s, e) = todayRange()
            repo.getTotalForRange(s, e).collect { totalToday = it }
        }
    }

    fun addExpense(onDone: () -> Unit = {}) {
        val amountVal = amount.toDoubleOrNull() ?: 0.0
        if (title.isBlank() || amountVal <= 0.0) return
        viewModelScope.launch {
            repo.addExpense(
                entity = ExpenseEntity(
                    title = title.trim(),
                    amount = amountVal,
                    category = category,
                    notes = notes.takeIf { it.isNotBlank() },
                    dateEpochMs = System.currentTimeMillis(),
                    imagePath = imageUri
                )
            )
            title = ""
            amount = ""
            notes = ""
            imageUri = null
            onDone()
        }
    }
}