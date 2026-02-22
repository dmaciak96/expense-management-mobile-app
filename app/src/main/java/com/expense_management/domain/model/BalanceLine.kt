package com.expense_management.domain.model

import java.util.UUID

data class BalanceLine(
    val fromMemberId: UUID,
    val toMemberId: UUID,
    val amount: MonetaryAmount
)
