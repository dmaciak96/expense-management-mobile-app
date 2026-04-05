package com.expense_management.feature.group_member.mapper

import android.util.Base64
import com.expense_management.domain.model.GroupMember
import com.expense_management.domain.model.binary.PublicKey
import com.expense_management.feature.group_member.ui.state.GroupMemberUiModel
import com.expense_management.feature.operation.mapper.OperationUiMapper
import kotlinx.serialization.Serializable

object GroupMemberUiMapper {

    fun toUiState(groupMember: GroupMember) = GroupMemberUiModel(
        id = groupMember.id,
        identity = groupMember.identity,
        groupIdentity = groupMember.groupIdentity,
        displayName = groupMember.displayName,
        publicKey = PublicKey.from(groupMember.publicKey.toByteArray()),
        role = groupMember.role
    )

    fun toDomain(groupMemberUiModel: GroupMemberUiModel) = GroupMember(
        id = groupMemberUiModel.id,
        identity = groupMemberUiModel.identity,
        groupIdentity = groupMemberUiModel.groupIdentity,
        displayName = groupMemberUiModel.displayName,
        publicKey = PublicKey.from(groupMemberUiModel.publicKey.toByteArray()),
        role = groupMemberUiModel.role
    )

    fun toByteArray(groupMember: GroupMember): ByteArray {
        val dto = GroupMemberPayload(
            identity = groupMember.identity.toString(),
            groupIdentity = groupMember.groupIdentity.toString(),
            displayName = groupMember.displayName,
            publicKey = Base64.encodeToString(groupMember.publicKey.toByteArray(), Base64.NO_WRAP),
            role = groupMember.role.name
        )
        return OperationUiMapper.JSON.encodeToString(dto).encodeToByteArray()
    }

    @Serializable
    private data class GroupMemberPayload(
        val identity: String,
        val groupIdentity: String,
        val displayName: String,
        val publicKey: String,
        val role: String
    )
}