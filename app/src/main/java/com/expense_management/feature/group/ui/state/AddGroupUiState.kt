package com.expense_management.feature.group.ui.state

import java.util.UUID

data class AddGroupUiState(
    val name: String = "",
    val identity: UUID = UUID.randomUUID(),
    val errorMessageId: Int? = null,
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
