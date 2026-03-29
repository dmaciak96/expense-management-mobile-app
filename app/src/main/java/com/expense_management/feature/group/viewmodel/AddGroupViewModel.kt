package com.expense_management.feature.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expense_management.R
import com.expense_management.domain.usecase.group.CreateGroupUseCase
import com.expense_management.feature.group.mapper.GroupUiMapper
import com.expense_management.feature.group.ui.state.AddGroupUiState
import com.expense_management.feature.group.ui.state.GroupUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddGroupViewModel @Inject constructor(
    private val groupUiMapper: GroupUiMapper,
    private val createGroupUseCase: CreateGroupUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGroupUiState())
    val uiState: StateFlow<AddGroupUiState> = _uiState.asStateFlow()

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

    fun addGroup() {
        val state = _uiState.value
        if (!state.isValid) return

        val name = state.name
        val groupUiModel = GroupUiModel(name = name)
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            runCatching {
                createGroupUseCase(groupUiMapper.toDomain(groupUiModel))
            }.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message
                    )
                }
            }
        }
        /**
         * 1. Zapisz grupę w bazie
         * 2. Utwórz dla niej operation log,
         * 3. Dodaj usera jako członka grupy,
         * 4. Dodaj do operation log GROUP_CREATED
         * 5. Dodaj do operation log MEMBER_ADDED
         */
    }
}