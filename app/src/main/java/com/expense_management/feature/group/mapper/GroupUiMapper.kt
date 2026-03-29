package com.expense_management.feature.group.mapper

import com.expense_management.core.time.ZoneProvider
import com.expense_management.core.time.toInstant
import com.expense_management.core.time.toLocalDateTime
import com.expense_management.domain.model.Group
import com.expense_management.feature.group.ui.state.GroupUiModel
import jakarta.inject.Inject

class GroupUiMapper @Inject constructor(
    private val zoneProvider: ZoneProvider
) {

    fun toUiState(group: Group) = GroupUiModel(
        id = group.id,
        name = group.name,
        createdAt = group.createdAt.toLocalDateTime(zoneProvider.zoneId()),
        identity = group.identity
    )

    fun toUiStates(groups: List<Group>) = groups.map { toUiState(it) }

    fun toDomain(groupUiModel: GroupUiModel) = Group(
        id = groupUiModel.id,
        name = groupUiModel.name,
        createdAt = groupUiModel.createdAt.toInstant(zoneProvider.zoneId()),
        identity = groupUiModel.identity
    )
}