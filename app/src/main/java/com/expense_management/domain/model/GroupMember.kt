package com.expense_management.domain.model

import java.util.UUID

data class GroupMember(
    val id: UUID,
    val groupId: UUID,
    val displayName: String,
    val publicKey: PublicKey,
    val role: GroupRole
)

class PublicKey private constructor(bytes: ByteArray) : BinaryValue(bytes) {
    companion object {
        fun from(bytes: ByteArray) = PublicKey(bytes.copyOf())
    }
}

enum class GroupRole {
    Owner, Member
}