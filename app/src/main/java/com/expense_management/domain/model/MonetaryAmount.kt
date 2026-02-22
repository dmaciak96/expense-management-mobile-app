package com.expense_management.domain.model

data class MonetaryAmount(
    val minorUnits: Long,
    val currency: CurrencyCode
)

enum class CurrencyCode {
    PLN,
    EUR,
    USD
}
