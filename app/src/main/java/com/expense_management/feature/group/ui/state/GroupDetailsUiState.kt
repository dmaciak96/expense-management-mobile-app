package com.expense_management.feature.group.ui.state

data class GroupDetailsUiState(
    val group: GroupUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)
