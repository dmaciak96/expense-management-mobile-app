package com.expense_management.domain.usecase.expense

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.repository.ExpenseRepository
import com.expense_management.domain.mapper.ExpenseMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetAllExpensesAndExpenseSharesUseCase @Inject constructor(private val repository: ExpenseRepository) {
    operator fun invoke() =
        repository.getExpenseAndExpenseShares()
            .map { result ->
                when (result) {
                    is Success -> Success(ExpenseMapper.toDomainResultMap(result.data))
                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}