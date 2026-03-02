package com.expense_management.domain.usecase.group_member

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.repository.GroupMemberRepository
import com.expense_management.domain.mapper.GroupMemberMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetAllGroupMembersAndExpensesUseCase @Inject constructor(private val repository: GroupMemberRepository) {
    operator fun invoke() =
        repository.getGroupMemberAndExpenses()
            .map { result ->
                when (result) {
                    is Success -> Success(GroupMemberMapper.toDomainResultMap(result.data))
                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}