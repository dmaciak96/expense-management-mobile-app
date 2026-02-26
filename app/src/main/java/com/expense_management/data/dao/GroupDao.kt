package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.expense_management.data.model.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Query(
        """
            SELECT * FROM `groups`
            ORDER BY name ASC
        """
    )
    fun getAll(): Flow<List<GroupEntity>>

    @Insert
    suspend fun insert(group: GroupEntity)

    @Delete
    suspend fun delete(group: GroupEntity)
}