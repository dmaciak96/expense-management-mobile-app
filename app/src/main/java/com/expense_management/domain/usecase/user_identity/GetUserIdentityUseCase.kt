package com.expense_management.domain.usecase.user_identity

import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.core.exception.ElementNotFoundException
import com.expense_management.data.repository.UserIdentityRepository
import com.expense_management.domain.mapper.UserIdentityMapper
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetUserIdentityUseCase @Inject constructor(private val repository: UserIdentityRepository) {
    operator fun invoke() =
        repository.getUserIdentity()
            .map { result ->
                when (result) {
                    is Success -> {
                        if (result.data == null) {
                            return@map Error(ElementNotFoundException("User identity not found"))
                        }
                        return@map Success(UserIdentityMapper.toDomain(result.data))
                    }

                    is Loading -> Loading
                    is Error -> Error(result.exception)
                }
            }
}