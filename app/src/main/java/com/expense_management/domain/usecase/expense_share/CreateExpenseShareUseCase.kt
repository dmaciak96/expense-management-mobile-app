package com.expense_management.domain.usecase.expense_share

import com.expense_management.data.repository.ExpenseShareRepository
import com.expense_management.domain.mapper.ExpenseShareMapper
import com.expense_management.domain.model.ExpenseShare
import jakarta.inject.Inject

class CreateExpenseShareUseCase @Inject constructor(private val repository: ExpenseShareRepository) {
    // TODO: Add validation
    suspend operator fun invoke(expenseShare: ExpenseShare) =
        repository.insert(ExpenseShareMapper.toEntity(expenseShare))
}