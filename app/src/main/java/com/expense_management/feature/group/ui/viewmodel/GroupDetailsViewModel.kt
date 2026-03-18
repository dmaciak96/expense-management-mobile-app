package com.expense_management.feature.group.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expense_management.R
import com.expense_management.app.Routes
import com.expense_management.core.common.OperationResult
import com.expense_management.domain.usecase.group.GetGroupByIdUseCase
import com.expense_management.feature.group.mapper.GroupUiMapper
import com.expense_management.feature.group.model.GroupDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val groupUiMapper: GroupUiMapper,
) : ViewModel() {
    private val groupId: Int =
        checkNotNull(savedStateHandle[Routes.GROUP_ID_ARG]).toString().trim().toInt()

    private val _uiState = MutableStateFlow(GroupDetailsUiState(isLoading = true))
    val uiState: StateFlow<GroupDetailsUiState> = _uiState.asStateFlow()

    init {
        observeGroup()
    }

    private fun observeGroup() {
        viewModelScope.launch {
            getGroupByIdUseCase(groupId)
                .collect { result ->
                    when (result) {
                        is OperationResult.Success -> {
                            _uiState.value = GroupDetailsUiState(
                                group = groupUiMapper.toUiState(result.data)
                            )
                        }

                        is OperationResult.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true
                            )
                        }

                        is OperationResult.Error -> {
                            _uiState.value = GroupDetailsUiState(
                                errorMessageRes = R.string.general_error
                            )
                        }
                    }
                }
        }
    }
}