package com.expense_management.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.expense_management.data.dao.GroupDao
import com.expense_management.data.model.GroupEntity

@Database(
    entities = [
        GroupEntity::class
    ],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract fun groupDao(): GroupDao
}