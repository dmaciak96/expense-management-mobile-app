package com.expense_management.domain.usecase.operation

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.repository.OperationRepository
import com.expense_management.domain.mapper.OperationMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map
import java.util.UUID

class GetAllOperationsByGroupIdentityUseCase @Inject constructor(private val repository: OperationRepository) {
    operator fun invoke(groupIdentity: UUID) =
        repository.getOperationsByGroupIdentity(groupIdentity)
            .map { result ->
                when (result) {
                    is Success -> Success(OperationMapper.toDomainList(result.data))
                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}