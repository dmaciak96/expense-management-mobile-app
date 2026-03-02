package com.expense_management.domain.usecase.expense_share

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.repository.ExpenseShareRepository
import com.expense_management.domain.mapper.ExpenseShareMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetAllExpenseSharesByExpenseIdUseCase @Inject constructor(private val repository: ExpenseShareRepository) {
    operator fun invoke(expenseId: Int) =
        repository.getExpenseSharesByExpenseId(expenseId)
            .map { result ->
                when (result) {
                    is Success -> Success(ExpenseShareMapper.toDomainList(result.data))
                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}