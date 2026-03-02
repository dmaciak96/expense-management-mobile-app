package com.expense_management.domain.usecase.group

import com.expense_management.data.repository.GroupRepository
import com.expense_management.domain.mapper.GroupMapper
import com.expense_management.domain.model.Group
import jakarta.inject.Inject

class CreateGroupUseCase @Inject constructor(private val repository: GroupRepository) {
    // TODO: Add validation
    suspend operator fun invoke(group: Group) =
        repository.insert(GroupMapper.toEntity(group))
}