package com.expense_management.data.repository

import android.util.Log
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.dao.ExpenseDao
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {

    suspend fun insert(expenseEntity: ExpenseEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Saving new expense ${expenseEntity.name} in db")
        expenseDao.insert(expenseEntity)
        Log.i(TAG, "Expense ${expenseEntity.name} was successfully saved in db")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to save new expense ${expenseEntity.name} in db: ${e.message}")
        Error(e)
    }

    suspend fun delete(expenseEntity: ExpenseEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Deleting expense ${expenseEntity.name} from db")
        expenseDao.delete(expenseEntity)
        Log.i(TAG, "Expense ${expenseEntity.name} successfully deleted from db")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to delete expense ${expenseEntity.name} from db: ${e.message}")
        Error(e)
    }

    suspend fun update(expenseEntity: ExpenseEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Updating expense ${expenseEntity.id}:${expenseEntity.name}")
        expenseDao.update(expenseEntity)
        Log.i(TAG, "Expense ${expenseEntity.id}:${expenseEntity.name} successfully updated")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to update expense ${expenseEntity.id}:${expenseEntity.name}: ${e.message}"
        )
        Error(e)
    }

    fun getAll(): Flow<OperationResult<List<ExpenseEntity>>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting all expenses from db")
            expenseDao.getAll().collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting all expenses finished successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get all expenses from db: ${e.message}")
            emit(Error(e))
        }
    }

    fun getById(id: Int): Flow<OperationResult<ExpenseEntity?>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting expense by id $id")
            expenseDao.getById(id).collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting expense by id $id finished successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get expense by id $id: ${e.message}")
            emit(Error(e))
        }
    }

    fun getExpenseAndExpenseShares(): Flow<OperationResult<Map<ExpenseEntity, List<ExpenseShareEntity>>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all expenses with shares")
                expenseDao.getExpenseAndExpenseShares().collect {
                    emit(Success(it))
                }
                Log.i(TAG, "Getting all expenses with shares finished successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get expenses with shares: ${e.message}")
                emit(Error(e))
            }
        }

    fun getExpensesByGroupId(groupId: Int): Flow<OperationResult<List<ExpenseEntity>>> = flow {
        emit(Loading)
        try {
            Log.i(TAG, "Getting all expenses from group $groupId")
            expenseDao.getExpensesByGroupId(groupId).collect {
                emit(Success(it))
            }
            Log.i(TAG, "Getting all expenses from group $groupId finished successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get expenses from group $groupId: ${e.message}")
            emit(Error(e))
        }
    }

    fun getExpensesByGroupMemberId(groupMemberId: Int): Flow<OperationResult<List<ExpenseEntity>>> =
        flow {
            emit(Loading)
            try {
                Log.i(TAG, "Getting all expenses by group member $groupMemberId")
                expenseDao.getExpensesByGroupMemberId(groupMemberId).collect {
                    emit(Success(it))
                }
                Log.i(
                    TAG,
                    "Getting all expenses by group member $groupMemberId finished successfully"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get expenses by group member $groupMemberId: ${e.message}")
                emit(Error(e))
            }
        }

    companion object {
        private val TAG = ExpenseRepository::class.simpleName
    }
}