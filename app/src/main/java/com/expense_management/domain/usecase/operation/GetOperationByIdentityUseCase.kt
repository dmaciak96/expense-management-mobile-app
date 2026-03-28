package com.expense_management.domain.usecase.operation

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.core.exception.ElementNotFoundException
import com.expense_management.data.repository.OperationRepository
import com.expense_management.domain.mapper.OperationMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map
import java.util.UUID

class GetOperationByIdentityUseCase @Inject constructor(private val repository: OperationRepository) {
    operator fun invoke(identity: UUID) =
        repository.getByIdentity(identity)
            .map { result ->
                when (result) {
                    is Success -> {
                        if (result.data == null) {
                            return@map Error(ElementNotFoundException("Operation not found with identity:$identity"))
                        }
                        return@map Success(OperationMapper.toDomain(result.data))
                    }

                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}