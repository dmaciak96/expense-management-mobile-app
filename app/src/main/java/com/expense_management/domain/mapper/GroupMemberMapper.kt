package com.expense_management.domain.mapper

import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import com.expense_management.domain.model.GroupMember
import com.expense_management.domain.model.GroupRole
import com.expense_management.domain.model.PublicKey

object GroupMemberMapper {
    fun toEntity(groupMember: GroupMember) =
        GroupMemberEntity(
            id = groupMember.id,
            groupId = groupMember.groupId,
            displayName = groupMember.displayName,
            publicKey = groupMember.publicKey.toByteArray(),
            role = groupMember.role.name
        )

    fun toDomain(groupMemberEntity: GroupMemberEntity) =
        GroupMember(
            id = groupMemberEntity.id,
            groupId = groupMemberEntity.groupId,
            displayName = groupMemberEntity.displayName,
            publicKey = PublicKey.from(groupMemberEntity.publicKey),
            role = GroupRole.valueOf(groupMemberEntity.role)
        )

    fun toDomainList(groupMemberEntities: List<GroupMemberEntity>) =
        groupMemberEntities.map { toDomain(it) }

    @JvmName("toGroupMemberAndExpenseSharesResultMap")
    fun toDomainResultMap(entityResultMap: Map<GroupMemberEntity, List<ExpenseShareEntity>>) =
        entityResultMap.map { (groupMemberEntity, expenseShareEntities) ->
            toDomain(groupMemberEntity) to ExpenseShareMapper.toDomainList(expenseShareEntities)
        }.toMap()

    @JvmName("toGroupMemberAndExpensesResultMap")
    fun toDomainResultMap(entityResultMap: Map<GroupMemberEntity, List<ExpenseEntity>>) =
        entityResultMap.map { (groupMemberEntity, expenseEntities) ->
            toDomain(groupMemberEntity) to ExpenseMapper.toDomainList(expenseEntities)
        }.toMap()

    @JvmName("toGroupMemberAndOperationsResultMap")
    fun toDomainResultMap(entityResultMap: Map<GroupMemberEntity, List<OperationEntity>>) =
        entityResultMap.map { (groupMemberEntity, operationEntities) ->
            toDomain(groupMemberEntity) to OperationMapper.toDomainList(operationEntities)
        }.toMap()
}