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
            SELECT * FROM `groups` AS g
            WHERE g.id = :id
        """
    )
    fun getById(id: Int): Flow<GroupEntity?>

    @Query(
        """
            SELECT * FROM `groups` as g
            JOIN `group_members` as m ON g.id = m.group_id
        """
    )
    fun getGroupAndGroupMembers(): Flow<Map<GroupEntity, List<GroupMemberEntity>>>

    @Query(
        """
            SELECT * FROM `groups` as g
            JOIN `expenses` as e ON g.id = e.group_id
        """
    )
    fun getGroupAndExpenses(): Flow<Map<GroupEntity, List<ExpenseEntity>>>

    @Query(
        """
            SELECT * FROM `groups` as g
            JOIN `operations` as o ON g.id = o.group_id
        """
    )
    fun getGroupAndOperations(): Flow<Map<GroupEntity, List<OperationEntity>>>
}