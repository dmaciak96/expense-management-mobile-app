package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.expense_management.data.model.ExpenseShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseShareDao {

    @Insert
    suspend fun insert(expenseShare: ExpenseShareEntity)

    @Delete
    suspend fun delete(expenseShare: ExpenseShareEntity)

    @Update
    suspend fun update(expenseShare: ExpenseShareEntity)

    @Query(
        """
            SELECT * FROM `expense_shares`
        """
    )
    fun getAll(): Flow<List<ExpenseShareEntity>>

    @Query(
        """
            SELECT * FROM `expense_shares` AS e
            WHERE e.id = :id
        """
    )
    fun getById(id: Int): Flow<ExpenseShareEntity?>

    @Query(
        """
            SELECT * FROM `expense_shares` AS e
            WHERE e.expense_id = :expenseId
        """
    )
    fun getExpenseSharesByExpenseId(expenseId: Int): Flow<List<ExpenseShareEntity>>

    @Query(
        """
            SELECT * FROM `expense_shares` AS e
            WHERE e.member_id = :groupMemberId
        """
    )
    fun getExpenseSharesByGroupMemberId(groupMemberId: Int): Flow<List<ExpenseShareEntity>>
}