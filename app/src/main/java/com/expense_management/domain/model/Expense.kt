package com.expense_management.domain.model

import java.time.Instant

data class Expense(
    val id: Int,
    val groupId: Int,
    val paidByMemberId: Int,
    val createdAt: Instant,
    val name: String,
    val amount: MonetaryAmount, // How much money was spent
)

data class ExpenseShare(
    val id: Int,
    val expenseId: Int,
    val memberId: Int,
    val sharedAmount: MonetaryAmount // How much member must pay
)
