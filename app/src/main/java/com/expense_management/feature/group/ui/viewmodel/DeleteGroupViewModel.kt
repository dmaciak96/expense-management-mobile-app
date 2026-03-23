package com.expense_management.feature.group.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.expense_management.R
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.domain.usecase.group.DeleteGroupUseCase
import com.expense_management.domain.usecase.group.GetGroupByIdUseCase
import com.expense_management.feature.group.mapper.GroupUiMapper
import com.expense_management.feature.group.model.DeleteGroupUiState
import com.expense_management.feature.group.ui.screen.DeleteGroupRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DeleteGroupViewModel @Inject constructor(
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val groupUiMapper: GroupUiMapper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val groupId = savedStateHandle.toRoute<DeleteGroupRoute>().id
    private val _uiState = MutableStateFlow(DeleteGroupUiState())
    val uiState: StateFlow<DeleteGroupUiState> = _uiState.asStateFlow()

    init {
        observeGroup()
    }

    private fun observeGroup() {
        viewModelScope.launch {
            getGroupByIdUseCase(groupId)
                .collect { result ->
                    when (result) {
                        is Success -> {
                            _uiState.value = DeleteGroupUiState(
                                group = groupUiMapper.toUiState(result.data)
                            )
                        }

                        is Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true
                            )
                        }

                        is Error -> {
                            _uiState.value = DeleteGroupUiState(
                                errorMessageRes = R.string.general_error
                            )
                        }
                    }
                }
        }
    }

    fun deleteGroup() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessageRes = null)
            }
            runCatching {
                val group = _uiState.value.group
                if (group != null) {
                    deleteGroupUseCase(
                        group = groupUiMapper.toDomain(group)
                    )
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }.onFailure { ex ->
                Log.e(TAG, "Group deletion failed: ${ex.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.general_error
                    )
                }
            }
        }
        /**
         * 1. Usuń członków grupy
         * 2. Usuń wydatki
         * 3. Usuń expense share
         * 4. Usuń grupę,
         * 5. Dodaj Operację
         */
    }

    companion object {
        private val TAG = DeleteGroupViewModel::class.simpleName
    }
}