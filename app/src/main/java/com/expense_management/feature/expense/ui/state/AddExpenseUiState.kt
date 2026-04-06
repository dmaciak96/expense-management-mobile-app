package com.expense_management.feature.expense.ui.state

import java.util.UUID

data class AddExpenseUiState(
    val name: String = "",
    val identity: UUID = UUID.randomUUID(),
    val errorMessageId: Int? = null,
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
)
