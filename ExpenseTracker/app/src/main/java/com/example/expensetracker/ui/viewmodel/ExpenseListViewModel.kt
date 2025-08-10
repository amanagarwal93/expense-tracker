package com.example.expensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.utils.todayRange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ExpenseListViewModel(private val repo: ExpenseRepository) : ViewModel() {

    private val range = MutableStateFlow(value = todayRange())

    @OptIn(markerClass = [ExperimentalCoroutinesApi::class])
    val expenses = range.flatMapLatest { (s, e) -> repo.getExpensesForRange(s, e) }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Companion.Lazily,
            initialValue = emptyList()
        )

    @OptIn(markerClass = [ExperimentalCoroutinesApi::class])
    val total = range.flatMapLatest { (s, e) -> repo.getTotalForRange(s, e) }
        .stateIn(viewModelScope, started = SharingStarted.Companion.Lazily, initialValue = 0.0)

    fun setRange(start: Long, end: Long) {
        range.value = start to end
    }

    fun setToday() {
        range.value = todayRange()
    }
}