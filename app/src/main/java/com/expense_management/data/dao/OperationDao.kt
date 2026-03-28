package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.expense_management.data.model.OperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {

    @Insert
    suspend fun insert(operation: OperationEntity)

    @Query(
        """
            SELECT * FROM `operations`
        """
    )
    fun getAll(): Flow<List<OperationEntity>>

    @Query(
        """
            SELECT * FROM `operations` as o
            WHERE o.identity = :identity
        """
    )
    fun getByIdentity(identity: String): Flow<OperationEntity?>

    @Query(
        """
            SELECT * FROM `operations` AS o
            WHERE o.group_id = :groupId
        """
    )
    fun getOperationsByGroupId(groupId: Int): Flow<List<OperationEntity>>

    @Query(
        """
            SELECT * FROM `operations` AS o
            WHERE o.operation_author_id = :groupMemberId
        """
    )
    fun getOperationsByGroupMemberId(groupMemberId: Int): Flow<List<OperationEntity>>
}