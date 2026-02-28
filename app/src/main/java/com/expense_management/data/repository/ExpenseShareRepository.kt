package com.expense_management.data.repository

import android.util.Log
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.dao.ExpenseShareDao
import com.expense_management.data.model.ExpenseShareEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExpenseShareRepository @Inject constructor(
    private val expenseShareDao: ExpenseShareDao
) {

    suspend fun insert(expenseShareEntity: ExpenseShareEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Saving new expense share for expense ${expenseShareEntity.expenseId}")
        expenseShareDao.insert(expenseShareEntity)
        Log.i(TAG, "Expense share was successfully saved")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to save expense share for expense ${expenseShareEntity.expenseId}: ${e.message}"
        )
        Error(e)
    }

    suspend fun delete(expenseShareEntity: ExpenseShareEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Deleting expense share ${expenseShareEntity.id}")
        expenseShareDao.delete(expenseShareEntity)
        Log.i(TAG, "Expense share was successfully deleted")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to delete expense share ${expenseShareEntity.id}: ${e.message}"
        )
        Error(e)
    }

    suspend fun update(expenseShareEntity: ExpenseShareEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Updating expense share ${expenseShareEntity.id}")
        expenseShareDao.update(expenseShareEntity)
        Log.i(TAG, "Expense share was successfully updated")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to update expense share ${expenseShareEntity.id}: ${e.message}"
        )
        Error(e)
    }

    fun getAll(): Flow<OperationResult<List<ExpenseShareEntity>>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting all expense shares from db")
            expenseShareDao.getAll().collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting all expense shares finished successfully")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to get all expense shares from db: ${e.message}")
            emit(Error(e))
        }
    }

    fun getById(id: Int): Flow<OperationResult<ExpenseShareEntity?>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting expense share by id $id")
            expenseShareDao.getById(id).collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting expense share by id $id finished successfully")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to get expense share by id $id: ${e.message}")
            emit(Error(e))
        }
    }

    fun getExpenseSharesByExpenseId(expenseId: Int): Flow<OperationResult<List<ExpenseShareEntity>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all expense shares by expense id $expenseId")
                expenseShareDao.getExpenseSharesByExpenseId(expenseId).collect {
                    emit(Success(it))
                }
                Log.i(
                    TAG,
                    "Getting all expense shares by expense id $expenseId finished successfully"
                )
            } catch (e: Exception) {
                Log.i(
                    TAG,
                    "Failed to get all expense shares by expense id $expenseId: ${e.message}"
                )
                emit(Error(e))
            }
        }

    fun getExpenseSharesByGroupMemberId(groupMemberId: Int): Flow<OperationResult<List<ExpenseShareEntity>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all expense shares by group member id $groupMemberId")
                expenseShareDao.getExpenseSharesByGroupMemberId(groupMemberId).collect {
                    emit(Success(it))
                }
                Log.i(
                    TAG,
                    "Getting all expense shares by group member id $groupMemberId finished successfully"
                )
            } catch (e: Exception) {
                Log.i(
                    TAG,
                    "Failed to get all expense shares by group member id $groupMemberId: ${e.message}"
                )
                emit(Error(e))
            }
        }

    companion object {
        private val TAG = ExpenseShareRepository::class.simpleName
    }
}