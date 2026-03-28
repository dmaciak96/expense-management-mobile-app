package com.expense_management.domain.model

import com.expense_management.domain.model.binary.PublicKey
import java.time.Instant
import java.util.UUID

data class UserIdentity(
    val createdAt: Instant,
    val identity: UUID,
    val name: String,
    val publicKey: PublicKey,
)
