package com.expense_management.domain.usecase.expense

import com.expense_management.data.repository.ExpenseRepository
import com.expense_management.domain.mapper.ExpenseMapper
import com.expense_management.domain.model.Expense
import jakarta.inject.Inject

class UpdateExpenseUseCase @Inject constructor(private val repository: ExpenseRepository) {
    // TODO: Add validation
    suspend operator fun invoke(expense: Expense) =
        repository.update(ExpenseMapper.toEntity(expense))
}