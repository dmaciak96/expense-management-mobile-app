package com.expense_management.data.repository

import android.util.Log
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.dao.GroupMemberDao
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GroupMemberRepository @Inject constructor(
    private val groupMemberDao: GroupMemberDao
) {

    suspend fun insert(groupMember: GroupMemberEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Saving new group member ${groupMember.displayName} for group ${groupMember.groupId}"
        )
        groupMemberDao.insert(groupMember)
        Log.i(
            TAG,
            "Saving new group member ${groupMember.displayName} for group ${groupMember.groupId} finished successfully"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to save new group member ${groupMember.displayName} for group ${groupMember.groupId}: ${e.message}"
        )
        Error(e)
    }

    suspend fun delete(groupMember: GroupMemberEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Deleting group member ${groupMember.displayName} from group ${groupMember.groupId}"
        )
        groupMemberDao.delete(groupMember)
        Log.i(
            TAG,
            "Deleting group member ${groupMember.displayName} from group ${groupMember.groupId} finished successfully"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to delete group member ${groupMember.displayName} from group ${groupMember.groupId}: ${e.message}"
        )
        Error(e)
    }

    suspend fun update(groupMember: GroupMemberEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Updating group member ${groupMember.displayName} in group ${groupMember.groupId}"
        )
        groupMemberDao.update(groupMember)
        Log.i(
            TAG,
            "Updating group member ${groupMember.displayName} in group ${groupMember.groupId} finished successfully"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to update group member ${groupMember.displayName} in group ${groupMember.groupId}: ${e.message}"
        )
        Error(e)
    }


    fun getAll(): Flow<OperationResult<List<GroupMemberEntity>>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting all group members from db")
            groupMemberDao.getAll().collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting all group members finished successfully")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to get all group members from db: ${e.message}")
            emit(Error(e))
        }
    }

    fun getById(id: Int): Flow<OperationResult<GroupMemberEntity?>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting group member by id $id from db")
            groupMemberDao.getById(id).collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting group member by id $id finished successfully")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to get group member by id $id: ${e.message}")
            emit(Error(e))
        }
    }

    fun getGroupMembersByGroupId(groupId: Int): Flow<OperationResult<List<GroupMemberEntity>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all group members for group $groupId")
                groupMemberDao.getGroupMembersByGroupId(groupId).collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting all group members for group $groupId finished successfully")
            } catch (e: Exception) {
                Log.i(TAG, "Failed to get all group members for group $groupId: ${e.message}")
                emit(Error(e))
            }
        }

    fun getGroupMemberAndExpenses(): Flow<OperationResult<Map<GroupMemberEntity, List<ExpenseEntity>>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting group members and expenses")
                groupMemberDao.getGroupMemberAndExpenses().collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting group members and expenses finished successfully")
            } catch (e: Exception) {
                Log.i(TAG, "Failed to get group members and expenses: ${e.message}")
                emit(Error(e))
            }
        }

    fun getGroupMemberAndExpenseShares(): Flow<OperationResult<Map<GroupMemberEntity, List<ExpenseShareEntity>>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting group members and expense shares")
                groupMemberDao.getGroupMemberAndExpenseShares().collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting group members and expense shares finished successfully")
            } catch (e: Exception) {
                Log.i(TAG, "Failed to get group members and expense shares: ${e.message}")
                emit(Error(e))
            }
        }

    fun getGroupMemberAndOperations(): Flow<OperationResult<Map<GroupMemberEntity, List<OperationEntity>>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting group members and operations")
                groupMemberDao.getGroupMemberAndOperations().collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting group members and operations finished successfully")
            } catch (e: Exception) {
                Log.i(TAG, "Failed to get group members and operations: ${e.message}")
                emit(Error(e))
            }
        }

    companion object {
        private val TAG = GroupMemberRepository::class.simpleName
    }
}