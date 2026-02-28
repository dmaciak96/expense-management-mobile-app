package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupMemberDao {

    @Query(
        """
            SELECT * FROM `group_members` as m
            JOIN `expenses` as e ON m.id = e.paid_by_member_id
        """
    )
    fun getGroupMemberAndExpenses(): Flow<Map<GroupMemberEntity, List<ExpenseEntity>>>

    @Query(
        """
            SELECT * FROM `group_members` as m
            JOIN `expense_shares` as e ON m.id = e.member_id
        """
    )
    fun getGroupMemberAndExpenseShares(): Flow<Map<GroupMemberEntity, List<ExpenseShareEntity>>>

    @Query(
        """
            SELECT * FROM `group_members` as m
            JOIN `operations` as o ON m.id = o.operation_author_id
        """
    )
    fun getGroupMemberAndOperations(): Flow<Map<GroupMemberEntity, List<OperationEntity>>>
}