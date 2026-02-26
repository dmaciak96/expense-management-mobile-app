package com.expense_management.domain.usecase.group

import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.repository.GroupRepository
import com.expense_management.domain.mapper.GroupMapper
import com.expense_management.domain.model.Group
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllGroupsUseCase @Inject constructor(private val repository: GroupRepository) {
    operator fun invoke(): Flow<OperationResult<List<Group>>> =
        repository.getAll()
            .map { result ->
                when (result) {
                    is Success -> Success(GroupMapper.toDomainList(result.data))
                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}