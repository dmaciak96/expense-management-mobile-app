package com.expense_management.domain.usecase.group

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.repository.GroupRepository
import com.expense_management.domain.mapper.GroupMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetAllGroupsAndExpensesUseCase @Inject constructor(private val repository: GroupRepository) {
    operator fun invoke() =
        repository.getGroupAndExpenses()
            .map { result ->
                when (result) {
                    is Success -> Success(GroupMapper.toDomainResultMap(result.data))
                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}
