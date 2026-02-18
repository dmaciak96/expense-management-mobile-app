package com.expense_management.domain

data class MonetaryAmount(
    val minorUnits: Long,
    val currency: CurrencyCode
)

enum class CurrencyCode {
    PLN,
    EUR,
    USD
}
