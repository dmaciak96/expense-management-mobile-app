package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expenseEntity: ExpenseEntity)

    @Delete
    suspend fun delete(expenseEntity: ExpenseEntity)

    @Update
    suspend fun update(expenseEntity: ExpenseEntity)

    @Query(
        """
            SELECT * from `expenses`   
        """
    )
    fun getAll(): Flow<List<ExpenseEntity>>

    @Query(
        """
            SELECT * FROM `expenses` as e
            WHERE e.identity = :identity
        """
    )
    fun getByIdentity(identity: String): Flow<ExpenseEntity?>

    @Query(
        """
            SELECT * FROM `expenses` as e
            JOIN `expense_shares` as es ON e.identity = es.expense_identity
        """
    )
    fun getExpenseAndExpenseShares(): Flow<Map<ExpenseEntity, List<ExpenseShareEntity>>>

    @Query(
        """
            SELECT * FROM `expenses` as e
            WHERE e.group_identity = :groupIdentity
        """
    )
    fun getExpensesByGroupIdentity(groupIdentity: String): Flow<List<ExpenseEntity>>

    @Query(
        """
            SELECT * FROM `expenses` AS e
            WHERE e.paid_by_member_identity = :groupMemberIdentity
        """
    )
    fun getExpensesByGroupMemberIdentity(groupMemberIdentity: String): Flow<List<ExpenseEntity>>
}