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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID

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

    fun getAll(): Flow<OperationResult<List<ExpenseEntity>>> =
        expenseDao.getAll()
            .map<List<ExpenseEntity>, OperationResult<List<ExpenseEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all expenses from db")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get all expenses from db: ${it.message}")
                emit(Error(it))
            }

    fun getByIdentity(identity: UUID): Flow<OperationResult<ExpenseEntity?>> =
        expenseDao.getByIdentity(identity.toString())
            .map<ExpenseEntity?, OperationResult<ExpenseEntity?>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting expense by identity $identity")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get expense by identity $identity: ${it.message}")
                emit(Error(it))
            }

    fun getExpenseAndExpenseShares(): Flow<OperationResult<Map<ExpenseEntity, List<ExpenseShareEntity>>>> =
        expenseDao.getExpenseAndExpenseShares()
            .map<Map<ExpenseEntity, List<ExpenseShareEntity>>, OperationResult<Map<ExpenseEntity, List<ExpenseShareEntity>>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all expenses with shares")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get expenses with shares: ${it.message}")
                emit(Error(it))
            }

    fun getExpensesByGroupIdentity(groupIdentity: UUID): Flow<OperationResult<List<ExpenseEntity>>> =
        expenseDao.getExpensesByGroupIdentity(groupIdentity.toString())
            .map<List<ExpenseEntity>, OperationResult<List<ExpenseEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all expenses from group $groupIdentity")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get expenses from group $groupIdentity: ${it.message}")
                emit(Error(it))
            }

    fun getExpensesByGroupMemberIdentity(groupMemberIdentity: UUID): Flow<OperationResult<List<ExpenseEntity>>> =
        expenseDao.getExpensesByGroupMemberIdentity(groupMemberIdentity.toString())
            .map<List<ExpenseEntity>, OperationResult<List<ExpenseEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all expenses by group member $groupMemberIdentity")
                emit(Loading)
            }
            .catch {
                Log.e(
                    TAG,
                    "Failed to get expenses by group member $groupMemberIdentity: ${it.message}"
                )
                emit(Error(it))
            }

    companion object {
        private val TAG = ExpenseRepository::class.simpleName
    }
}