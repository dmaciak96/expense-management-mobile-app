package com.expense_management.feature.group.model

data class GroupDetailsUiState(
    val group: GroupUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)
