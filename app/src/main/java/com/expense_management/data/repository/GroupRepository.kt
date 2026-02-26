package com.expense_management.data.repository

import android.util.Log
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.dao.GroupDao
import com.expense_management.data.model.GroupEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {
    fun getAll(): Flow<OperationResult<List<GroupEntity>>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting all groups from db")
            groupDao.getAll().collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting all groups finished successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get all groups from db: ${e.message}")
            emit(Error(e))
        }
    }

    suspend fun insert(group: GroupEntity): OperationResult<Unit> {
        return try {
            Log.i(TAG, "Saving new group ${group.name} in db")
            groupDao.insert(group)
            Log.i(TAG, "Group ${group.name} was successfully saved in db")
            Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save new group ${group.name} in db: ${e.message}")
            Error(e)
        }
    }

    suspend fun delete(group: GroupEntity): OperationResult<Unit> {
        return try {
            Log.i(TAG, "Deleting group ${group.name} from db")
            groupDao.delete(group)
            Log.i(TAG, "Group ${group.name} successfully deleted from db")
            Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete group ${group.name} from db: ${e.message}")
            Error(e)
        }
    }

    companion object {
        private val TAG = GroupRepository::class.simpleName
    }
}