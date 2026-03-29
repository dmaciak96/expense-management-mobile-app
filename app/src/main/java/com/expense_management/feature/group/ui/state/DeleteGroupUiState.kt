package com.expense_management.feature.group.ui.state

data class DeleteGroupUiState(
    val group: GroupUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)
