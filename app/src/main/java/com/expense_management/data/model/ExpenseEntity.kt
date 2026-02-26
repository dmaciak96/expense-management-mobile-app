package com.expense_management.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "group_id")
    // TODO: Implement many to one
    val groupId: Int,

    @ColumnInfo(name = "paid_by_member_id")
    // TODO: Implement many to one
    val paidByMemberId: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    val name: String,

    @ColumnInfo(name = "minor_units")
    val minorUnits: Long,

    val currency: String
)
