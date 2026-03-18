package com.expense_management.feature.group.model

data class AddGroupUiState(
    val name: String = "",
    val errorMessageId: Int? = null,
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
