package com.expense_management.domain.mapper

import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import com.expense_management.domain.model.Group
import java.time.Instant

object GroupMapper {
    fun toEntity(group: Group) = GroupEntity(
        id = group.id,
        createdAt = group.createdAt.toEpochMilli(),
        name = group.name
    )

    fun toDomain(groupEntity: GroupEntity) = Group(
        id = groupEntity.id,
        createdAt = Instant.ofEpochMilli(groupEntity.createdAt),
        name = groupEntity.name
    )

    fun toDomainList(groupEntities: List<GroupEntity>) =
        groupEntities.map { toDomain(it) }

    fun toDomainResultMap(entityResultMap: Map<GroupEntity, List<ExpenseEntity>>) =
        entityResultMap.map { (groupEntity, expenseEntities) ->
            toDomain(groupEntity) to ExpenseMapper.toDomainList(expenseEntities)
        }.toMap()

    fun toDomainResultMap(entityResultMap: Map<GroupEntity, List<GroupMemberEntity>>) =
        entityResultMap.map { (groupEntity, groupMemberEntities) ->
            toDomain(groupEntity) to GroupMemberMapper.toDomainList(groupMemberEntities)
        }.toMap()

    fun toDomainResultMap(entityResultMap: Map<GroupEntity, List<OperationEntity>>) =
        entityResultMap.map { (groupEntity, operationEntities) ->
            toDomain(groupEntity) to OperationMapper.toDomainList(operationEntities)
        }.toMap()
}
