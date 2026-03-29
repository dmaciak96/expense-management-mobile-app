package com.expense_management.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations")
data class OperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val identity: String,

    @ColumnInfo(name = "group_identity")
    val groupIdentity: String,

    @ColumnInfo(name = "operation_author_identity")
    val operationAuthorIdentity: String,

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
