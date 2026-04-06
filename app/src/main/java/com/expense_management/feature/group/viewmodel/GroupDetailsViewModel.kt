package com.expense_management.feature.group.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.expense_management.R
import com.expense_management.core.common.OperationResult
import com.expense_management.domain.usecase.group.GetGroupByIdentityUseCase
import com.expense_management.feature.group.mapper.GroupUiMapper
import com.expense_management.feature.group.ui.GroupDetailsRoute
import com.expense_management.feature.group.ui.state.GroupDetailsTab
import com.expense_management.feature.group.ui.state.GroupDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGroupByIdentityUseCase: GetGroupByIdentityUseCase,
    private val groupUiMapper: GroupUiMapper,
) : ViewModel() {
    private val groupIdentity: UUID =
        UUID.fromString(savedStateHandle.toRoute<GroupDetailsRoute>().identity)
    private val _uiState = MutableStateFlow(GroupDetailsUiState(isLoading = true))
    val uiState: StateFlow<GroupDetailsUiState> = _uiState.asStateFlow()

    init {
        observeGroup()
    }

    private fun observeGroup() {
        viewModelScope.launch {
            getGroupByIdentityUseCase(groupIdentity)
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

    fun onTabSelected(tab: GroupDetailsTab) {
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }
}
