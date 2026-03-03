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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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
        groupDao.getAll()
            .map<List<GroupEntity>, OperationResult<List<GroupEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all groups from db")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get all groups from db: ${it.message}")
                emit(Error(it))
            }

    fun getById(id: Int): Flow<OperationResult<GroupEntity?>> =
        groupDao.getById(id)
            .map<GroupEntity?, OperationResult<GroupEntity?>> { Success(it) }
            .onStart {
                Log.i(TAG, "Getting group by id $id")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get group by id $id: ${it.message}")
                emit(Error(it))
            }

    fun getGroupAndGroupMembers(): Flow<OperationResult<Map<GroupEntity, List<GroupMemberEntity>>>> =
        groupDao.getGroupAndGroupMembers()
            .map<Map<GroupEntity, List<GroupMemberEntity>>, OperationResult<Map<GroupEntity, List<GroupMemberEntity>>>> {
                Success(
                    it
                )
            }
            .onStart {
                Log.i(TAG, "Getting all groups with members")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get groups with members: ${it.message}")
                emit(Error(it))
            }


    fun getGroupAndExpenses(): Flow<OperationResult<Map<GroupEntity, List<ExpenseEntity>>>> =
        groupDao.getGroupAndExpenses()
            .map<Map<GroupEntity, List<ExpenseEntity>>, OperationResult<Map<GroupEntity, List<ExpenseEntity>>>> {
                Success(
                    it
                )
            }
            .onStart {
                Log.i(TAG, "Getting all groups with expenses")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get groups with expenses: ${it.message}")
                emit(Error(it))
            }

    fun getGroupAndOperations(): Flow<OperationResult<Map<GroupEntity, List<OperationEntity>>>> =
        groupDao.getGroupAndOperations()
            .map<Map<GroupEntity, List<OperationEntity>>, OperationResult<Map<GroupEntity, List<OperationEntity>>>> {
                Success(
                    it
                )
            }
            .onStart {
                Log.i(TAG, "Getting all groups with operations")
                emit(Loading)
            }.catch {
                Log.e(TAG, "Failed to get groups with operations: ${it.message}")
                emit(Error(it))
            }

    companion object {
        private val TAG = GroupRepository::class.simpleName
    }
}