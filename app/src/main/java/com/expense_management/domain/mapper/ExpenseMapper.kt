package com.expense_management.domain.mapper

import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.domain.model.CurrencyCode
import com.expense_management.domain.model.Expense
import com.expense_management.domain.model.MonetaryAmount
import java.time.Instant
import java.util.UUID

object ExpenseMapper {
    fun toEntity(expense: Expense) = ExpenseEntity(
        id = expense.id,
        groupIdentity = expense.groupIdentity.toString(),
        paidByMemberIdentity = expense.paidByMemberIdentity.toString(),
        createdAt = expense.createdAt.toEpochMilli(),
        name = expense.name,
        minorUnits = expense.amount.minorUnits,
        currency = expense.amount.currency.name,
        identity = expense.identity.toString()
    )

    fun toDomain(expenseEntity: ExpenseEntity) = Expense(
        id = expenseEntity.id,
        groupIdentity = UUID.fromString(expenseEntity.groupIdentity),
        paidByMemberIdentity = UUID.fromString(expenseEntity.paidByMemberIdentity),
        createdAt = Instant.ofEpochMilli(expenseEntity.createdAt),
        name = expenseEntity.name,
        amount = MonetaryAmount(
            expenseEntity.minorUnits,
            CurrencyCode.valueOf(expenseEntity.currency)
        ),
        identity = UUID.fromString(expenseEntity.identity)
    )

    fun toDomainList(expenseEntities: List<ExpenseEntity>) =
        expenseEntities.map { toDomain(it) }

    fun toDomainResultMap(entityResultMap: Map<ExpenseEntity, List<ExpenseShareEntity>>) =
        entityResultMap.map { (expenseEntity, expenseShareEntities) ->
            toDomain(expenseEntity) to ExpenseShareMapper.toDomainList(expenseShareEntities)
        }.toMap()
}
