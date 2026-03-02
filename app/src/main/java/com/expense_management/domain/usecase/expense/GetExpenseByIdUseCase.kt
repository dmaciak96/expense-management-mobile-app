package com.expense_management.domain.usecase.expense

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.core.exception.ElementNotFoundException
import com.expense_management.data.repository.ExpenseRepository
import com.expense_management.domain.mapper.ExpenseMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetExpenseByIdUseCase @Inject constructor(private val repository: ExpenseRepository) {
    operator fun invoke(id: Int) =
        repository.getById(id)
            .map { result ->
                when (result) {
                    is Success -> {
                        if (result.data == null) {
                            return@map Error(ElementNotFoundException("Expense not found with id:$id"))
                        }
                        return@map Success(ExpenseMapper.toDomain(result.data))
                    }

                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}