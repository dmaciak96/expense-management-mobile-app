package com.expense_management.domain.usecase.group

import com.expense_management.core.common.OperationResult
import com.expense_management.data.repository.GroupRepository
import com.expense_management.domain.mapper.GroupMapper
import com.expense_management.domain.model.Group
import jakarta.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(group: Group): OperationResult<Unit> {
        return repository.insert(GroupMapper.toEntity(group))
    }
}