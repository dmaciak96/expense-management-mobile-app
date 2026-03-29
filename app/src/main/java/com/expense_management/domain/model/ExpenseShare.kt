package com.expense_management.domain.model

import java.util.UUID

data class ExpenseShare(
    val id: Int,
    val identity: UUID,
    val expenseIdentity: UUID,
    val memberIdentity: UUID,
    val sharedAmount: MonetaryAmount // How much member must pay
)