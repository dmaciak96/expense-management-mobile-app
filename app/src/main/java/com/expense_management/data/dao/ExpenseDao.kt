package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query(
        """
            SELECT * FROM `expenses` as e
            JOIN `expense_shares` as es ON e.id = es.expense_id
        """
    )
    fun getExpenseAndExpenseShares(): Flow<Map<ExpenseEntity, List<ExpenseShareEntity>>>
}