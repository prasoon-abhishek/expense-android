package com.prasoon.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prasoon.expense.model.Budget
import com.prasoon.expense.model.Category

@Dao
interface BudgetDao {

    @Query("UPDATE budget SET budgetAmount = :amount WHERE id = :id")
    suspend fun updateBudget(id: Long, amount: Double)

    @Query("SELECT * FROM budget ORDER BY id DESC")
    suspend fun getBudget(): List<Budget>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

}