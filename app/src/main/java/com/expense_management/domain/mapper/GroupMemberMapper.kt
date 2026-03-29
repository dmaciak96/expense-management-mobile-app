package com.expense_management.domain.mapper

import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import com.expense_management.domain.model.GroupMember
import com.expense_management.domain.model.GroupRole
import com.expense_management.domain.model.binary.PublicKey
import java.util.UUID

object GroupMemberMapper {
    fun toEntity(groupMember: GroupMember) =
        GroupMemberEntity(
            id = groupMember.id,
            groupIdentity = groupMember.groupIdentity.toString(),
            displayName = groupMember.displayName,
            publicKey = groupMember.publicKey.toByteArray(),
            role = groupMember.role.name,
            identity = groupMember.identity.toString()
        )

    fun toDomain(groupMemberEntity: GroupMemberEntity) =
        GroupMember(
            id = groupMemberEntity.id,
            groupIdentity = UUID.fromString(groupMemberEntity.groupIdentity),
            displayName = groupMemberEntity.displayName,
            publicKey = PublicKey.from(groupMemberEntity.publicKey),
            role = GroupRole.valueOf(groupMemberEntity.role),
            identity = UUID.fromString(groupMemberEntity.identity)
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