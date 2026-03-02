package com.expense_management.domain.usecase.group_member

import com.expense_management.data.repository.GroupMemberRepository
import com.expense_management.domain.mapper.GroupMemberMapper
import com.expense_management.domain.model.GroupMember
import jakarta.inject.Inject

class DeleteGroupMemberUseCase @Inject constructor(private val repository: GroupMemberRepository) {
    // TODO: Add validation
    suspend operator fun invoke(groupMember: GroupMember) =
        repository.delete(GroupMemberMapper.toEntity(groupMember))
}