package com.example.expensetracker.data.model

import androidx.room.Dao

@Dao
data class CategoryTotal(
    val category: String,
    val total: Long
)
