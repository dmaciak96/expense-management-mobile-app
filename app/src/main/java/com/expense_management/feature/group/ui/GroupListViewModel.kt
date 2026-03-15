package com.expense_management.feature.group.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expense_management.R
import com.expense_management.core.common.OperationResult
import com.expense_management.domain.usecase.group.GetAllGroupsUseCase
import com.expense_management.feature.group.mapper.GroupUiMapper
import com.expense_management.feature.group.model.GroupListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val getGroupsUseCase: GetAllGroupsUseCase,
    private val groupUiMapper: GroupUiMapper
) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupListUiState(isLoading = true))
    val uiState: StateFlow<GroupListUiState> = _uiState.asStateFlow()

    init {
        observeGroups()
    }

    private fun observeGroups() {
        getGroupsUseCase()
            .onEach { result ->
                when (result) {
                    is OperationResult.Success -> {
                        _uiState.value = GroupListUiState(
                            groups = groupUiMapper.toUiStates(result.data)
                        )
                    }

                    is OperationResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true
                        )
                    }

                    is OperationResult.Error -> {
                        _uiState.value = GroupListUiState(
                            errorMessageRes = R.string.get_all_groups_error
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}