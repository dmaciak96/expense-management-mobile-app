package com.expense_management.data.repository

import android.util.Log
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.dao.OperationDao
import com.expense_management.data.model.OperationEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OperationRepository @Inject constructor(
    private val operationDao: OperationDao
) {

    suspend fun insert(operationEntity: OperationEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Saving new operation ${operationEntity.type} for group ${operationEntity.groupId}"
        )
        operationDao.insert(operationEntity)
        Log.i(
            TAG,
            "Operation ${operationEntity.type} for group ${operationEntity.groupId} was successfully saved"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to save ${operationEntity.type} for group ${operationEntity.groupId}: ${e.message}"
        )
        Error(e)
    }

    suspend fun delete(operationEntity: OperationEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Deleting operation ${operationEntity.type} for group ${operationEntity.groupId}"
        )
        operationDao.delete(operationEntity)
        Log.i(
            TAG,
            "Operation ${operationEntity.type} for group ${operationEntity.groupId} was successfully deleted"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to delete operation ${operationEntity.type} for group ${operationEntity.groupId}: ${e.message}"
        )
        Error(e)
    }

    suspend fun update(operationEntity: OperationEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Updating operation ${operationEntity.type} for group ${operationEntity.groupId}"
        )
        operationDao.update(operationEntity)
        Log.i(
            TAG,
            "Operation ${operationEntity.type} for group ${operationEntity.groupId} was successfully updated"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to update operation ${operationEntity.type} for group ${operationEntity.groupId}: ${e.message}"
        )
        Error(e)
    }

    fun getAll(): Flow<OperationResult<List<OperationEntity>>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting all operations from db")
            operationDao.getAll().collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting all operations finished successfully")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to get all operations from db: ${e.message}")
            emit(Error(e))
        }
    }

    fun getById(id: Int): Flow<OperationResult<OperationEntity?>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting operation by id $id")
            operationDao.getById(id).collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting operation by id $id finished successfully")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to get operation by id $id: ${e.message}")
            emit(Error(e))
        }
    }

    fun getOperationsByGroupId(groupId: Int): Flow<OperationResult<List<OperationEntity>>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting all operations by group id $groupId")
            operationDao.getOperationsByGroupId(groupId).collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting all operations by group id $groupId finished successfully")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to get all operations by group id $groupId: ${e.message}")
            emit(Error(e))
        }
    }

    fun getOperationsByGroupMemberId(groupMemberId: Int): Flow<OperationResult<List<OperationEntity>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all operations by group member id $groupMemberId")
                operationDao.getOperationsByGroupMemberId(groupMemberId).collect {
                    emit(Success(it))
                }
                Log.i(
                    TAG,
                    "Getting all operations by group member id $groupMemberId finished successfully"
                )
            } catch (e: Exception) {
                Log.i(
                    TAG,
                    "Failed to get all operations by group member id $groupMemberId: ${e.message}"
                )
                emit(Error(e))
            }
        }

    companion object {
        private val TAG = OperationRepository::class.simpleName
    }
}