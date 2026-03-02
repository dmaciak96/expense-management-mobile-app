package com.expense_management.domain.usecase.group

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.core.exception.ElementNotFoundException
import com.expense_management.data.repository.GroupRepository
import com.expense_management.domain.mapper.GroupMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetGroupByIdUseCase @Inject constructor(private val repository: GroupRepository) {
    operator fun invoke(id: Int) =
        repository.getById(id)
            .map { result ->
                when (result) {
                    is Success -> {
                        if (result.data == null) {
                            return@map Error(ElementNotFoundException("Group not found with id:$id"))
                        }
                        return@map Success(GroupMapper.toDomain(result.data))
                    }

                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}