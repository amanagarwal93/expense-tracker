package com.example.expensetracker.data.model

import androidx.room.Dao

@Dao
data class DailyTotal(
    val day: String,
    val total: Long
)
