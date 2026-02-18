package com.expense_management.domain

import java.util.UUID

data class BalanceLine(
    val fromMemberId: UUID,
    val toMemberId: UUID,
    val amount: MonetaryAmount
)
