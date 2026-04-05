package com.expense_management.feature.group.mapper

import com.expense_management.core.time.ZoneProvider
import com.expense_management.core.time.toInstant
import com.expense_management.core.time.toLocalDateTime
import com.expense_management.domain.model.Group
import com.expense_management.feature.group.ui.state.GroupUiModel
import com.expense_management.feature.operation.mapper.OperationUiMapper
import jakarta.inject.Inject
import kotlinx.serialization.Serializable

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

    fun toByteArray(group: Group): ByteArray {
        val dto = GroupPayload(
            identity = group.identity.toString(),
            createdAt = group.createdAt.toEpochMilli(),
            name = group.name
        )
        return OperationUiMapper.JSON.encodeToString(dto).encodeToByteArray()
    }

    @Serializable
    private data class GroupPayload(
        val identity: String,
        val createdAt: Long,
        val name: String
    )
}