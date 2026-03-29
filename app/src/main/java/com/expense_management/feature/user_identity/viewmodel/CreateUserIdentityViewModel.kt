package com.expense_management.feature.user_identity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expense_management.R
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.core.exception.ElementNotFoundException
import com.expense_management.core.security.SecurityUtil
import com.expense_management.domain.usecase.user_identity.CreateUserIdentityUseCase
import com.expense_management.domain.usecase.user_identity.GetUserIdentityUseCase
import com.expense_management.feature.user_identity.mapper.UserIdentityUiMapper
import com.expense_management.feature.user_identity.ui.state.CreateUserIdentityUiState
import com.expense_management.feature.user_identity.ui.state.UserIdentityUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateUserIdentityViewModel @Inject constructor(
    private val userIdentityUiMapper: UserIdentityUiMapper,
    private val createUserIdentityUseCase: CreateUserIdentityUseCase,
    private val getUserIdentityUseCase: GetUserIdentityUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateUserIdentityUiState())
    val uiState: StateFlow<CreateUserIdentityUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        val error = when {
            name.isBlank() -> R.string.is_blank_error_message
            name.length > 64 -> R.string.length_error_message
            else -> null
        }
        _uiState.update {
            it.copy(
                name = name,
                errorMessageId = error,
                isValid = error == null
            )
        }
    }

    fun createIdentity() {
        val state = _uiState.value
        if (!state.isValid) return

        val userIdentityUiModel =
            UserIdentityUiModel(name = state.name, publicKey = SecurityUtil.getPublicKey())
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            runCatching {
                createUserIdentityUseCase(userIdentityUiMapper.toDomain(userIdentityUiModel))
            }.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }.onFailure { ex ->
                Log.e(TAG, "User identity creation error: ${ex.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageId = R.string.general_error
                    )
                }
            }
        }
    }

    fun isMissingUserIdentity() {
        viewModelScope.launch {
            getUserIdentityUseCase().collect { result ->
                when (result) {
                    is Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isMissing = false,
                        )
                    }

                    is Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                        )
                    }

                    is Error -> {
                        if (result.exception is ElementNotFoundException) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isMissing = true,
                            )
                        } else {
                            Log.e(TAG, "User identity getting error: ${result.exception.message}")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isMissing = false,
                                errorMessageId = R.string.general_error
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = CreateUserIdentityViewModel::class.simpleName
    }
}