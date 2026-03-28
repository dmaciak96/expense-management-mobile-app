package com.expense_management.domain.model

import java.time.Instant
import java.util.UUID

data class Expense(
    val id: Int,
    val identity: UUID,
    val groupId: Int,
    val paidByMemberId: Int,
    val createdAt: Instant,
    val name: String,
    val amount: MonetaryAmount, // How much money was spent
)
