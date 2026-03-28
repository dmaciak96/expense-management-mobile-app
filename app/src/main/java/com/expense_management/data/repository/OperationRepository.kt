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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID

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

    fun getAll(): Flow<OperationResult<List<OperationEntity>>> =
        operationDao.getAll()
            .map<List<OperationEntity>, OperationResult<List<OperationEntity>>> { Success(it) }
            .onStart {
                Log.i(TAG, "Getting all operations from db")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get all operations from db: ${it.message}")
                emit(Error(it))
            }

    fun getByIdentity(identity: UUID): Flow<OperationResult<OperationEntity?>> =
        operationDao.getByIdentity(identity.toString())
            .map<OperationEntity?, OperationResult<OperationEntity?>> { Success(it) }
            .onStart {
                Log.i(TAG, "Getting operation by identity $identity")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get operation by identity $identity: ${it.message}")
                emit(Error(it))
            }

    fun getOperationsByGroupId(groupId: Int): Flow<OperationResult<List<OperationEntity>>> =
        operationDao.getOperationsByGroupId(groupId)
            .map<List<OperationEntity>, OperationResult<List<OperationEntity>>> { Success(it) }
            .onStart {
                Log.i(TAG, "Getting all operations by group id $groupId")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get all operations by group id $groupId: ${it.message}")
                emit(Error(it))
            }

    fun getOperationsByGroupMemberId(groupMemberId: Int): Flow<OperationResult<List<OperationEntity>>> =
        operationDao.getOperationsByGroupMemberId(groupMemberId)
            .map<List<OperationEntity>, OperationResult<List<OperationEntity>>> { Success(it) }
            .onStart {
                Log.i(TAG, "Getting all operations by group member id $groupMemberId")
                emit(Loading)
            }
            .catch {
                Log.e(
                    TAG,
                    "Failed to get all operations by group member id $groupMemberId: ${it.message}"
                )
                emit(Error(it))
            }

    companion object {
        private val TAG = OperationRepository::class.simpleName
    }
}