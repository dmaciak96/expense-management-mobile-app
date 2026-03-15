package com.expense_management.feature.group.model

data class GroupListUiState(
    val groups: List<GroupUiState> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)
