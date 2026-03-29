package com.expense_management.domain.model

import java.util.UUID

data class BalanceLine(
    val fromMemberIdentity: UUID,
    val toMemberIdentity: UUID,
    val amount: MonetaryAmount
)
