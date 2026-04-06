package com.expense_management.feature.group_member.ui.state

data class AddGroupMemberUiState(
    val errorMessageId: Int? = null,
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
)
