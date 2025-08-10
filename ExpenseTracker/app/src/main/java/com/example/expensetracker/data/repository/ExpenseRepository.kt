package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.local.ExpenseEntity
import kotlinx.coroutines.flow.map

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    suspend fun addExpense(entity: ExpenseEntity) = expenseDao.insert(expense = entity)
    fun getExpensesForRange(start: Long, end: Long) = expenseDao.getBetween(start, end)
    fun getTotalForRange(start: Long, end: Long) =
        expenseDao.sumBetween(start, end).map { it ?: 0.0 }
}
