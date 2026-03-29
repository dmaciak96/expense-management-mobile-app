package com.expense_management.domain.mapper

import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.domain.model.CurrencyCode
import com.expense_management.domain.model.ExpenseShare
import com.expense_management.domain.model.MonetaryAmount
import java.util.UUID

object ExpenseShareMapper {
    fun toEntity(expenseShare: ExpenseShare) =
        ExpenseShareEntity(
            id = expenseShare.id,
            expenseIdentity = expenseShare.expenseIdentity.toString(),
            memberIdentity = expenseShare.memberIdentity.toString(),
            minorUnits = expenseShare.sharedAmount.minorUnits,
            currency = expenseShare.sharedAmount.currency.name,
            identity = expenseShare.identity.toString()
        )

    fun toDomain(expenseShareEntity: ExpenseShareEntity) =
        ExpenseShare(
            id = expenseShareEntity.id,
            expenseIdentity = UUID.fromString(expenseShareEntity.expenseIdentity),
            memberIdentity = UUID.fromString(expenseShareEntity.memberIdentity),
            sharedAmount = MonetaryAmount(
                minorUnits = expenseShareEntity.minorUnits,
                currency = CurrencyCode.valueOf(expenseShareEntity.currency)
            ),
            identity = UUID.fromString(expenseShareEntity.identity)
        )

    fun toDomainList(expenseShareEntities: List<ExpenseShareEntity>) =
        expenseShareEntities.map { toDomain(it) }
}