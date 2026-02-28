package com.expense_management.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations")
data class OperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "group_id")
    val groupId: Int,

    @ColumnInfo(name = "operation_author_id")
    val operationAuthorId: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "lamport_clock")
    val lamportClock: Long,

    val type: String,

    @ColumnInfo(name = "payload", typeAffinity = ColumnInfo.BLOB)
    val payload: ByteArray,

    @ColumnInfo(name = "signature", typeAffinity = ColumnInfo.BLOB)
    val signature: ByteArray
)
