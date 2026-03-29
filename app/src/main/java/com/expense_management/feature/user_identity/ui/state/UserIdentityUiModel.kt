package com.expense_management.feature.user_identity.ui.state

import com.expense_management.domain.model.binary.PublicKey
import java.time.LocalDateTime
import java.util.UUID

data class UserIdentityUiModel(
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val identity: UUID = UUID.randomUUID(),
    val name: String,
    val publicKey: PublicKey,
)
