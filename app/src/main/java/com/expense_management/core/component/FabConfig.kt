package com.expense_management.core.component

import java.util.UUID

sealed interface FabConfig {
    data object None : FabConfig
    data object AddGroup : FabConfig
    data class AddExpense(val groupIdentity: UUID) : FabConfig
    data class AddGroupMember(val groupIdentity: UUID) : FabConfig
}
