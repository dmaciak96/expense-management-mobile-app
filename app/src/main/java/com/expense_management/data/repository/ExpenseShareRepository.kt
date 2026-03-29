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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID

class ExpenseShareRepository @Inject constructor(
    private val expenseShareDao: ExpenseShareDao
) {

    suspend fun insert(expenseShareEntity: ExpenseShareEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Saving new expense share for expense ${expenseShareEntity.expenseIdentity}")
        expenseShareDao.insert(expenseShareEntity)
        Log.i(TAG, "Expense share was successfully saved")
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to save expense share for expense ${expenseShareEntity.expenseIdentity}: ${e.message}"
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

    fun getAll(): Flow<OperationResult<List<ExpenseShareEntity>>> =
        expenseShareDao.getAll()
            .map<List<ExpenseShareEntity>, OperationResult<List<ExpenseShareEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all expense shares from db")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get all expense shares from db: ${it.message}")
                emit(Error(it))
            }

    fun getByIdentity(identity: UUID): Flow<OperationResult<ExpenseShareEntity?>> =
        expenseShareDao.getByIdentity(identity.toString())
            .map<ExpenseShareEntity?, OperationResult<ExpenseShareEntity?>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting expense share by identity $identity")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get expense share by identity $identity: ${it.message}")
                emit(Error(it))
            }

    fun getExpenseSharesByExpenseIdentity(expenseIdentity: UUID): Flow<OperationResult<List<ExpenseShareEntity>>> =
        expenseShareDao.getExpenseSharesByExpenseIdentity(expenseIdentity.toString())
            .map<List<ExpenseShareEntity>, OperationResult<List<ExpenseShareEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all expense shares by expense id $expenseIdentity")
                emit(Loading)
            }.catch {
                Log.e(
                    TAG,
                    "Failed to get all expense shares by expense id $expenseIdentity: ${it.message}"
                )
                emit(Error(it))
            }

    fun getExpenseSharesByGroupMemberIdentity(groupMemberIdentity: UUID): Flow<OperationResult<List<ExpenseShareEntity>>> =
        expenseShareDao.getExpenseSharesByGroupMemberIdentity(groupMemberIdentity.toString())
            .map<List<ExpenseShareEntity>, OperationResult<List<ExpenseShareEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all expense shares by group member id $groupMemberIdentity")
                emit(Loading)
            }
            .catch {
                Log.e(
                    TAG,
                    "Failed to get all expense shares by group member id $groupMemberIdentity: ${it.message}"
                )
                emit(Error(it))
            }

    companion object {
        private val TAG = ExpenseShareRepository::class.simpleName
    }
}