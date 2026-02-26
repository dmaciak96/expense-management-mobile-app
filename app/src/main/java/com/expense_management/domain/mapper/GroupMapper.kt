package com.expense_management.domain.mapper

import com.expense_management.data.model.GroupEntity
import com.expense_management.domain.model.Group
import java.time.Instant

object GroupMapper {
    fun toEntity(group: Group): GroupEntity = GroupEntity(
        id = group.id,
        createdAt = group.createdAt.toEpochMilli(),
        name = group.name
    )

    fun toDomain(groupEntity: GroupEntity): Group = Group(
        id = groupEntity.id,
        createdAt = Instant.ofEpochMilli(groupEntity.createdAt),
        name = groupEntity.name
    )

    fun toDomainList(groupEntities: List<GroupEntity>): List<Group> =
        groupEntities.map { toDomain(it) }
}