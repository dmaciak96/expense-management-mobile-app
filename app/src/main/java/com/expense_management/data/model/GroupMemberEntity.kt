package com.expense_management.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_members")
data class GroupMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "group_id")
    val groupId: Int,

    @ColumnInfo(name = "display_name")
    val displayName: String,

    @ColumnInfo(name = "public_key", typeAffinity = ColumnInfo.BLOB)
    val publicKey: ByteArray,

    val role: String
)
