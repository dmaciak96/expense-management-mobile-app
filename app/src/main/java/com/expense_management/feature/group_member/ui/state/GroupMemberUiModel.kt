package com.expense_management.feature.group_member.ui.state

import com.expense_management.domain.model.GroupRole
import com.expense_management.domain.model.binary.PublicKey
import java.util.UUID

data class GroupMemberUiModel(
    val id: Int = 0,
    val identity: UUID,
    val groupIdentity: UUID,
    val displayName: String,
    val publicKey: PublicKey,
    val role: GroupRole
)
