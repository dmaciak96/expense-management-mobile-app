package com.expense_management.domain.model

import java.util.UUID

data class ExpenseShare(
    val id: Int,
    val identity: UUID,
    val expenseId: Int,
    val memberId: Int,
    val sharedAmount: MonetaryAmount // How much member must pay
)