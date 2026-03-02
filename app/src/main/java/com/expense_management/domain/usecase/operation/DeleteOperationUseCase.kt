package com.expense_management.domain.usecase.operation

import com.expense_management.data.repository.OperationRepository
import com.expense_management.domain.mapper.OperationMapper
import com.expense_management.domain.model.Operation
import jakarta.inject.Inject

class DeleteOperationUseCase @Inject constructor(private val repository: OperationRepository) {
    // TODO: Add validation
    suspend operator fun invoke(operation: Operation) =
        repository.delete(OperationMapper.toEntity(operation))
}