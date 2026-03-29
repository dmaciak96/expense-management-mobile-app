package com.expense_management.domain.model

import com.expense_management.domain.model.binary.PublicKey
import java.util.UUID

data class GroupMember(
    val id: Int,
    val identity: UUID,
    val groupIdentity: UUID,
    val displayName: String,
    val publicKey: PublicKey,
    val role: GroupRole
)

enum class GroupRole {
    Owner, Member
}