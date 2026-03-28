package com.expense_management.domain.usecase.user_identity

import com.expense_management.data.repository.UserIdentityRepository
import com.expense_management.domain.mapper.UserIdentityMapper
import com.expense_management.domain.model.UserIdentity
import jakarta.inject.Inject

class UpdateUserIdentityUseCase @Inject constructor(private val repository: UserIdentityRepository) {
    // TODO: Add validation
    suspend operator fun invoke(userIdentity: UserIdentity) =
        repository.update(UserIdentityMapper.toEntity(userIdentity))
}