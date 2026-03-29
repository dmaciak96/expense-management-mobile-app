package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupMemberDao {

    @Insert
    suspend fun insert(groupMember: GroupMemberEntity)

    @Delete
    suspend fun delete(groupMember: GroupMemberEntity)

    @Update
    suspend fun update(groupMember: GroupMemberEntity)

    @Query(
        """
            SELECT * FROM `group_members`
        """
    )
    fun getAll(): Flow<List<GroupMemberEntity>>

    @Query(
        """
            SELECT * FROM `group_members` as g
            WHERE g.identity = :identity
        """
    )
    fun getByIdentity(identity: String): Flow<GroupMemberEntity?>

    @Query(
        """
            SELECT * FROM `group_members` as g
            WHERE g.group_identity = :groupIdentity
        """
    )
    fun getGroupMembersByGroupIdentity(groupIdentity: String): Flow<List<GroupMemberEntity>>

    @Query(
        """
            SELECT * FROM `group_members` as m
            JOIN `expenses` as e ON m.identity = e.paid_by_member_identity
        """
    )
    fun getGroupMemberAndExpenses(): Flow<Map<GroupMemberEntity, List<ExpenseEntity>>>

    @Query(
        """
            SELECT * FROM `group_members` as m
            JOIN `expense_shares` as e ON m.identity = e.member_identity
        """
    )
    fun getGroupMemberAndExpenseShares(): Flow<Map<GroupMemberEntity, List<ExpenseShareEntity>>>

    @Query(
        """
            SELECT * FROM `group_members` as m
            JOIN `operations` as o ON m.identity = o.operation_author_identity
        """
    )
    fun getGroupMemberAndOperations(): Flow<Map<GroupMemberEntity, List<OperationEntity>>>
}