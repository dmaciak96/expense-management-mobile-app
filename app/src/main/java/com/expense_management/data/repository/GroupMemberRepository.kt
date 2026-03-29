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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID

class GroupMemberRepository @Inject constructor(
    private val groupMemberDao: GroupMemberDao
) {

    suspend fun insert(groupMember: GroupMemberEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Saving new group member ${groupMember.displayName} for group ${groupMember.groupIdentity}"
        )
        groupMemberDao.insert(groupMember)
        Log.i(
            TAG,
            "Saving new group member ${groupMember.displayName} for group ${groupMember.groupIdentity} finished successfully"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to save new group member ${groupMember.displayName} for group ${groupMember.groupIdentity}: ${e.message}"
        )
        Error(e)
    }

    suspend fun delete(groupMember: GroupMemberEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Deleting group member ${groupMember.displayName} from group ${groupMember.groupIdentity}"
        )
        groupMemberDao.delete(groupMember)
        Log.i(
            TAG,
            "Deleting group member ${groupMember.displayName} from group ${groupMember.groupIdentity} finished successfully"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to delete group member ${groupMember.displayName} from group ${groupMember.groupIdentity}: ${e.message}"
        )
        Error(e)
    }

    suspend fun update(groupMember: GroupMemberEntity): OperationResult<Unit> = try {
        Log.i(
            TAG,
            "Updating group member ${groupMember.displayName} in group ${groupMember.groupIdentity}"
        )
        groupMemberDao.update(groupMember)
        Log.i(
            TAG,
            "Updating group member ${groupMember.displayName} in group ${groupMember.groupIdentity} finished successfully"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to update group member ${groupMember.displayName} in group ${groupMember.groupIdentity}: ${e.message}"
        )
        Error(e)
    }


    fun getAll(): Flow<OperationResult<List<GroupMemberEntity>>> =
        groupMemberDao.getAll()
            .map<List<GroupMemberEntity>, OperationResult<List<GroupMemberEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all group members from db")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get all group members from db: ${it.message}")
                emit(Error(it))
            }

    fun getByIdentity(identity: UUID): Flow<OperationResult<GroupMemberEntity?>> =
        groupMemberDao.getByIdentity(identity.toString())
            .map<GroupMemberEntity?, OperationResult<GroupMemberEntity?>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting group member by identity $identity from db")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get group member by identity $identity: ${it.message}")
                emit(Error(it))
            }

    fun getGroupMembersByGroupIdentity(groupIdentity: UUID): Flow<OperationResult<List<GroupMemberEntity>>> =
        groupMemberDao.getGroupMembersByGroupIdentity(groupIdentity.toString())
            .map<List<GroupMemberEntity>, OperationResult<List<GroupMemberEntity>>> {
                Success(it)
            }
            .onStart {
                Log.i(TAG, "Getting all group members for group $groupIdentity")
                emit(Loading)
            }
            .catch {
                Log.e(
                    TAG,
                    "Failed to get all group members for group $groupIdentity: ${it.message}"
                )
                emit(Error(it))
            }

    fun getGroupMemberAndExpenses(): Flow<OperationResult<Map<GroupMemberEntity, List<ExpenseEntity>>>> =
        groupMemberDao.getGroupMemberAndExpenses()
            .map<Map<GroupMemberEntity, List<ExpenseEntity>>, OperationResult<Map<GroupMemberEntity, List<ExpenseEntity>>>> { rawMap ->
                Success(
                    rawMap.entries
                        .groupBy { it.key.identity }
                        .mapValues { (_, entries) ->
                            entries.flatMap { it.value }
                        }
                        .map { (_, expenses) ->
                            val representativeKey = rawMap.keys.first { key ->
                                key.identity == expenses.firstOrNull()?.paidByMemberIdentity || rawMap[key] == expenses
                            }
                            representativeKey to expenses
                        }
                        .toMap()
                )
            }
            .onStart {
                Log.i(TAG, "Getting group members and expenses")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get group members and expenses: ${it.message}")
                emit(Error(it))
            }

    fun getGroupMemberAndExpenseShares(): Flow<OperationResult<Map<GroupMemberEntity, List<ExpenseShareEntity>>>> =
        groupMemberDao.getGroupMemberAndExpenseShares()
            .map<Map<GroupMemberEntity, List<ExpenseShareEntity>>, OperationResult<Map<GroupMemberEntity, List<ExpenseShareEntity>>>> { rawMap ->
                Success(
                    rawMap.entries
                        .groupBy { it.key.identity }
                        .mapValues { (_, entries) ->
                            entries.flatMap { it.value }
                        }
                        .map { (memberIdentity, shares) ->
                            val representativeKey =
                                rawMap.keys.first { it.identity == memberIdentity }
                            representativeKey to shares
                        }
                        .toMap()
                )
            }
            .onStart {
                Log.i(TAG, "Getting group members and expense shares")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get group members and expense shares: ${it.message}")
                emit(Error(it))
            }

    fun getGroupMemberAndOperations(): Flow<OperationResult<Map<GroupMemberEntity, List<OperationEntity>>>> =
        groupMemberDao.getGroupMemberAndOperations()
            .map<Map<GroupMemberEntity, List<OperationEntity>>, OperationResult<Map<GroupMemberEntity, List<OperationEntity>>>> { rawMap ->
                Success(
                    rawMap.entries
                        .groupBy { it.key.identity }
                        .mapValues { (_, entries) ->
                            entries.flatMap { it.value }
                        }
                        .map { (memberIdentity, operations) ->
                            val representativeKey =
                                rawMap.keys.first { it.identity == memberIdentity }
                            representativeKey to operations
                        }
                        .toMap()
                )
            }
            .onStart {
                Log.i(TAG, "Getting group members and operations")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get group members and operations: ${it.message}")
                emit(Error(it))
            }

    companion object {
        private val TAG = GroupMemberRepository::class.simpleName
    }
}