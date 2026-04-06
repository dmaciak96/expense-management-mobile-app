package com.expense_management.feature.group.ui.state

import androidx.annotation.StringRes
import com.expense_management.R

data class GroupDetailsUiState(
    val group: GroupUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val selectedTab: GroupDetailsTab = GroupDetailsTab.Expenses
)

enum class GroupDetailsTab(@StringRes val titleRes: Int) {
    Expenses(titleRes = R.string.expenses),
    Members(titleRes = R.string.group_members)
}
