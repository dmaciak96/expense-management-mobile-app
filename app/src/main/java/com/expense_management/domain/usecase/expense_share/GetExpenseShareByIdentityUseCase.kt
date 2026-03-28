package com.expense_management.domain.usecase.expense_share

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.core.exception.ElementNotFoundException
import com.expense_management.data.repository.ExpenseShareRepository
import com.expense_management.domain.mapper.ExpenseShareMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map
import java.util.UUID

class GetExpenseShareByIdentityUseCase @Inject constructor(private val repository: ExpenseShareRepository) {
    operator fun invoke(identity: UUID) =
        repository.getByIdentity(identity)
            .map { result ->
                when (result) {
                    is Success -> {
                        if (result.data == null) {
                            return@map Error(ElementNotFoundException("Expense share not found with identity:$identity"))
                        }
                        return@map Success(ExpenseShareMapper.toDomain(result.data))
                    }

                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}