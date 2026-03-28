package com.expense_management.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_identity")
data class UserIdentityEntity(
    @PrimaryKey
    val id: Int = 1,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    val identity: String,

    val name: String,

    @ColumnInfo(name = "public_key", typeAffinity = ColumnInfo.BLOB)
    val publicKey: ByteArray
)
