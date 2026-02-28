package com.expense_management.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.expense_management.data.dao.ExpenseDao
import com.expense_management.data.dao.ExpenseShareDao
import com.expense_management.data.dao.GroupDao
import com.expense_management.data.dao.GroupMemberDao
import com.expense_management.data.dao.OperationDao
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity

@Database(
    entities = [
        ExpenseEntity::class,
        ExpenseShareEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        OperationEntity::class
    ],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseShareDao(): ExpenseShareDao
    abstract fun groupDao(): GroupDao
    abstract fun groupMemberDao(): GroupMemberDao
    abstract fun operationDao(): OperationDao
}