package com.example.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.data.model.CategoryTotal
import com.example.expensetracker.data.model.DailyTotal
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Query(value = "SELECT * FROM expenses WHERE dateEpochMs BETWEEN :start AND :end ORDER BY dateEpochMs DESC")
    fun getBetween(start: Long, end: Long): Flow<List<ExpenseEntity>>

    @Query(value = "SELECT SUM(amount) FROM expenses WHERE dateEpochMs BETWEEN :start AND :end")
    fun sumBetween(start: Long, end: Long): Flow<Double?>

    @Query(
        value = """
    SELECT strftime('%Y-%m-%d', dateEpochMs/1000, 'unixepoch') as day, SUM(amount) as total
    FROM expenses
    WHERE dateEpochMs BETWEEN :start AND :end
    GROUP BY day
"""
    )
    suspend fun getDailyTotals(start: Long, end: Long): List<DailyTotal>

    @Query(
        value = """
    SELECT category, SUM(amount) as total
    FROM expenses
    WHERE dateEpochMs BETWEEN :start AND :end
    GROUP BY category
"""
    )
    suspend fun getCategoryTotals(start: Long, end: Long): List<CategoryTotal>
}
