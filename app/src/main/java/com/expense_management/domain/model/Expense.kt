package com.expense_management.domain.model

import java.time.Instant
import java.util.UUID

data class Expense(
    val id: Int,
    val identity: UUID,
    val groupIdentity: UUID,
    val paidByMemberIdentity: UUID,
    val createdAt: Instant,
    val name: String,
    val amount: MonetaryAmount, // How much money was spent
)
