package com.expense_management.feature.group.mapper

import com.expense_management.core.time.ZoneProvider
import com.expense_management.core.time.toInstant
import com.expense_management.core.time.toLocalDateTime
import com.expense_management.domain.model.Group
import com.expense_management.feature.group.model.GroupUiState
import jakarta.inject.Inject

class GroupUiMapper @Inject constructor(
    private val zoneProvider: ZoneProvider
) {

    fun toUiState(group: Group) = GroupUiState(
        id = group.id,
        name = group.name,
        createdAt = group.createdAt.toLocalDateTime(zoneProvider.zoneId())
    )

    fun toUiStates(groups: List<Group>) = groups.map { toUiState(it) }

    fun toDomain(groupUiState: GroupUiState) = Group(
        id = groupUiState.id,
        name = groupUiState.name,
        createdAt = groupUiState.createdAt.toInstant(zoneProvider.zoneId())
    )
}