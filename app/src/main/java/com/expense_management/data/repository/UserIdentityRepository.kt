package com.expense_management.data.repository

import android.util.Log
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.dao.UserIdentityDao
import com.expense_management.data.model.UserIdentityEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart


class UserIdentityRepository @Inject constructor(
    private val userIdentityDao: UserIdentityDao
) {

    suspend fun insertIfNotExists(userIdentityEntity: UserIdentityEntity): OperationResult<Unit> =
        try {
            Log.i(TAG, "Saving new user identity in db")
            userIdentityDao.insertIfNotExists(userIdentityEntity)
            Log.i(TAG, "user identity was successfully saved in db")
            Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to save new user identity in db: ${e.message}"
            )
            Error(e)
        }

    fun getUserIdentity(): Flow<OperationResult<UserIdentityEntity?>> =
        userIdentityDao.getUserIdentity()
            .map<UserIdentityEntity?, OperationResult<UserIdentityEntity?>> { Success(it) }
            .onStart {
                Log.i(TAG, "Getting user identity")
                emit(Loading)
            }
            .catch {
                Log.e(TAG, "Failed to get user identity: ${it.message}")
                emit(Error(it))
            }

    suspend fun update(userIdentityEntity: UserIdentityEntity): OperationResult<Unit> = try {
        Log.i(TAG, "Updating user identity")
        userIdentityDao.update(userIdentityEntity)
        Log.i(
            TAG,
            "user identity successfully updated"
        )
        Success(Unit)
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Failed to update user identity: ${e.message}"
        )
        Error(e)
    }

    companion object {
        private val TAG = UserIdentityRepository::class.simpleName
    }
}