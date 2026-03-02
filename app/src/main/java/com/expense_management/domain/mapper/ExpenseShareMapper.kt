package com.expense_management.domain.mapper

import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.domain.model.CurrencyCode
import com.expense_management.domain.model.ExpenseShare
import com.expense_management.domain.model.MonetaryAmount

object ExpenseShareMapper {
    fun toEntity(expenseShare: ExpenseShare) =
        ExpenseShareEntity(
            id = expenseShare.id,
            expenseId = expenseShare.expenseId,
            memberId = expenseShare.memberId,
            minorUnits = expenseShare.sharedAmount.minorUnits,
            currency = expenseShare.sharedAmount.currency.name
        )

    fun toDomain(expenseShareEntity: ExpenseShareEntity) =
        ExpenseShare(
            id = expenseShareEntity.id,
            expenseId = expenseShareEntity.expenseId,
            memberId = expenseShareEntity.memberId,
            sharedAmount = MonetaryAmount(
                minorUnits = expenseShareEntity.minorUnits,
                currency = CurrencyCode.valueOf(expenseShareEntity.currency)
            )
        )

    fun toDomainList(expenseShareEntities: List<ExpenseShareEntity>) =
        expenseShareEntities.map { toDomain(it) }
}