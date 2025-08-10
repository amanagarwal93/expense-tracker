package com.example.expensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.utils.getLast7DayRanges
import com.example.expensetracker.utils.last7DaysRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExpenseReportViewModel(private val repo: ExpenseRepository) : ViewModel() {

    val dailyTotals: StateFlow<List<Pair<String, Long>>> = run {
        val ranges = getLast7DayRanges()
        if (ranges.isEmpty()) {
            MutableStateFlow(value = emptyList())
        } else {
            val flows: Array<Flow<Double>> = ranges.map { (_, s, e) ->
                repo.getTotalForRange(s, e)
            }.toTypedArray()

            combine(*flows) { values: Array<Any?> ->
                values.mapIndexed { idx, any ->
                    val v = (any as? Double) ?: 0.0
                    ranges[idx].first to v.toLong()
                }
            }.stateIn(
                viewModelScope,
                started = SharingStarted.Companion.Eagerly,
                initialValue = ranges.map { it.first to 0L })
        }
    }

    val categoryTotals: StateFlow<Map<String, Long>> = run {
        val (start, end) = last7DaysRange()
        repo.getExpensesForRange(start, end)
            .map { expenses ->
                expenses.groupBy { it.category }
                    .mapValues { (_, list) -> list.sumOf { it.amount.toLong() } }
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.Companion.Eagerly,
                initialValue = emptyMap()
            )
    }

    suspend fun getExpensesForRange(start: Long, end: Long): List<ExpenseEntity> {
        return repo.getExpensesForRange(start, end).first()
    }
}