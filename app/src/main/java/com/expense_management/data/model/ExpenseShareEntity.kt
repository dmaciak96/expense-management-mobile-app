package com.expense_management.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_shares")
data class ExpenseShareEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    // TODO: Implement many to one
    @ColumnInfo(name = "expense_id")
    val expenseId: Int,

    // TODO: Implement many to one
    @ColumnInfo(name = "member_id")
    val memberId: Int,

    @ColumnInfo(name = "minor_units")
    val minorUnits: Long,

    val currency: String
)
