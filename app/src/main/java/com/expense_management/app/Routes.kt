package com.expense_management.app

object Routes {
    const val GROUPS = "groups"
    const val ADD_GROUP = "add_group"

    const val GROUP_DETAILS = "group_details"
    const val GROUP_ID_ARG = "groupId"
    const val GROUP_DETAILS_ROUTE = "$GROUP_DETAILS/{$GROUP_ID_ARG}"
    fun groupDetailsRoute(groupId: Int) = "${GROUP_DETAILS}/$groupId"
}
