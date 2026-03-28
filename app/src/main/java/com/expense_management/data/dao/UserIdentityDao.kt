package com.expense_management.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.expense_management.core.exception.ElementAlreadyExistsException
import com.expense_management.data.model.UserIdentityEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserIdentityDao {

    suspend fun insertIfNotExists(userIdentityEntity: UserIdentityEntity) {
        val inserted = insert(userIdentityEntity)
        if (inserted == -1L) {
            throw ElementAlreadyExistsException("User identity already exists on this device")
        }
    }

    @Query(
        """
        SELECT * FROM `user_identity` WHERE id = 1
    """
    )
    abstract fun getUserIdentity(): Flow<UserIdentityEntity?>

    @Update
    abstract suspend fun update(userIdentityEntity: UserIdentityEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insert(userIdentityEntity: UserIdentityEntity): Long
}