package com.expense_management.feature.user_identity.ui.state

data class CreateUserIdentityUiState(
    val name: String = "",
    val isMissing: Boolean = false,
    val isValid: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessageId: Int? = null
)
