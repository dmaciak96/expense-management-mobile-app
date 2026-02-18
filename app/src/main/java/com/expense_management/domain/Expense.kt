package com.expense_management.domain

import java.time.Instant
import java.util.UUID

data class Expense(
    val id: UUID,
    val groupId: UUID,
    val paidByMemberId: UUID,
    val createdAt: Instant,
    val name: String,
    val amount: MonetaryAmount, // How much money was spent
)

data class ExpenseShare(
    val expenseId: UUID,
    val memberId: UUID,
    val sharedAmount: MonetaryAmount // How much member must pay
)
