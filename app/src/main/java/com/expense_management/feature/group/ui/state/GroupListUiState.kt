package com.expense_management.feature.group.ui.state

data class GroupListUiState(
    val groups: List<GroupUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)
