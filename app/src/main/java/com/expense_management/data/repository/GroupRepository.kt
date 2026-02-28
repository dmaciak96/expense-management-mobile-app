package com.expense_management.data.repository

import android.util.Log
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.dao.GroupDao
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {

    suspend fun insert(group: GroupEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Saving new group ${group.name} in db")
        groupDao.insert(group)
        Log.i(TAG, "Group ${group.name} was successfully saved in db")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to save new group ${group.name} in db: ${e.message}")
        Error(e)
    }

    suspend fun delete(group: GroupEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Deleting group ${group.name} from db")
        groupDao.delete(group)
        Log.i(TAG, "Group ${group.name} successfully deleted from db")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to delete group ${group.name} from db: ${e.message}")
        Error(e)
    }

    suspend fun update(group: GroupEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Updating group ${group.id}:${group.name}")
        groupDao.update(group)
        Log.i(TAG, "Group ${group.id}:${group.name} successfully updated")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to update group  ${group.id}:${group.name}: ${e.message}")
        Error(e)
    }

    fun getAll(): Flow<OperationResult<List<GroupEntity>>> =
        flow {
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

    fun getById(id: Int): Flow<OperationResult<GroupEntity?>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting group by id $id")
                groupDao.getById(id).collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting group by id $id finished successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get group by id $id: ${e.message}")
                emit(Error(e))
            }
        }

    fun getGroupAndGroupMembers(): Flow<OperationResult<Map<GroupEntity, List<GroupMemberEntity>>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all groups with members")
                groupDao.getGroupAndGroupMembers().collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting all groups with members finished successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get groups with members: ${e.message}")
                emit(Error(e))
            }
        }

    fun getGroupAndExpenses(): Flow<OperationResult<Map<GroupEntity, List<ExpenseEntity>>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all groups with expenses")
                groupDao.getGroupAndExpenses().collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting all groups with expenses finished successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get groups with expenses: ${e.message}")
                emit(Error(e))
            }
        }

    fun getGroupAndOperations(): Flow<OperationResult<Map<GroupEntity, List<OperationEntity>>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all groups with operations")
                groupDao.getGroupAndOperations().collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting all groups with operations finished successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get groups with operations: ${e.message}")
                emit(Error(e))
            }
        }

    companion object {
        private val TAG = GroupRepository::class.simpleName
    }
}