package com.expense_management.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_shares")
data class ExpenseShareEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val identity: String,

    @ColumnInfo(name = "expense_identity")
    val expenseIdentity: String,

    @ColumnInfo(name = "member_identity")
    val memberIdentity: String,

    @ColumnInfo(name = "minor_units")
    val minorUnits: Long,

    val currency: String
)
