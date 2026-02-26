package com.expense_management.domain.model

data class BalanceLine(
    val fromMemberId: Int,
    val toMemberId: Int,
    val amount: MonetaryAmount
)
