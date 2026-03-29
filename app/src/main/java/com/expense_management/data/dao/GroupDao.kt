package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert
    suspend fun insert(group: GroupEntity)

    @Delete
    suspend fun delete(group: GroupEntity)

    @Update
    suspend fun update(group: GroupEntity)

    @Query(
        """
            SELECT * FROM `groups`
            ORDER BY name ASC
        """
    )
    fun getAll(): Flow<List<GroupEntity>>

    @Query(
        """
            SELECT * FROM `groups` as g
            WHERE g.identity = :identity
        """
    )
    fun getByIdentity(identity: String): Flow<GroupEntity?>

    @Query(
        """
            SELECT * FROM `groups` as g
            JOIN `group_members` as m ON g.identity = m.group_identity
        """
    )
    fun getGroupAndGroupMembers(): Flow<Map<GroupEntity, List<GroupMemberEntity>>>

    @Query(
        """
            SELECT * FROM `groups` as g
            JOIN `expenses` as e ON g.identity = e.group_identity
        """
    )
    fun getGroupAndExpenses(): Flow<Map<GroupEntity, List<ExpenseEntity>>>

    @Query(
        """
            SELECT * FROM `groups` as g
            JOIN `operations` as o ON g.identity = o.group_identity
        """
    )
    fun getGroupAndOperations(): Flow<Map<GroupEntity, List<OperationEntity>>>
}