package com.expense_management.domain.model

data class GroupMember(
    val id: Int,
    val groupId: Int,
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