package com.expense_management.domain.mapper

import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import com.expense_management.domain.model.Group
import java.time.Instant
import java.util.UUID

object GroupMapper {
    fun toEntity(group: Group) = GroupEntity(
        id = group.id,
        createdAt = group.createdAt.toEpochMilli(),
        name = group.name,
        identity = group.identity.toString()
    )

    fun toDomain(groupEntity: GroupEntity) = Group(
        id = groupEntity.id,
        createdAt = Instant.ofEpochMilli(groupEntity.createdAt),
        name = groupEntity.name,
        identity = UUID.fromString(groupEntity.identity)
    )

    fun toDomainList(groupEntities: List<GroupEntity>) =
        groupEntities.map { toDomain(it) }

    @JvmName("toGroupAndExpensesResultMap")
    fun toDomainResultMap(entityResultMap: Map<GroupEntity, List<ExpenseEntity>>) =
        entityResultMap.map { (groupEntity, expenseEntities) ->
            toDomain(groupEntity) to ExpenseMapper.toDomainList(expenseEntities)
        }.toMap()

    @JvmName("toGroupAndMembersResultMap")
    fun toDomainResultMap(entityResultMap: Map<GroupEntity, List<GroupMemberEntity>>) =
        entityResultMap.map { (groupEntity, groupMemberEntities) ->
            toDomain(groupEntity) to GroupMemberMapper.toDomainList(groupMemberEntities)
        }.toMap()

    @JvmName("toGroupAndOperationsResultMap")
    fun toDomainResultMap(entityResultMap: Map<GroupEntity, List<OperationEntity>>) =
        entityResultMap.map { (groupEntity, operationEntities) ->
            toDomain(groupEntity) to OperationMapper.toDomainList(operationEntities)
        }.toMap()
}
