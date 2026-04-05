package com.expense_management.feature.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expense_management.R
import com.expense_management.core.common.awaitData
import com.expense_management.core.common.getOrThrow
import com.expense_management.core.security.SecurityUtil
import com.expense_management.domain.model.GroupRole
import com.expense_management.domain.model.OperationType
import com.expense_management.domain.model.SignedPayload
import com.expense_management.domain.model.binary.Payload
import com.expense_management.domain.model.binary.PublicKey
import com.expense_management.domain.model.binary.Signature
import com.expense_management.domain.usecase.group.CreateGroupUseCase
import com.expense_management.domain.usecase.group.GetGroupByIdentityUseCase
import com.expense_management.domain.usecase.group_member.CreateGroupMemberUseCase
import com.expense_management.domain.usecase.group_member.GetGroupMemberByIdentityUseCase
import com.expense_management.domain.usecase.operation.CreateOperationUseCase
import com.expense_management.domain.usecase.user_identity.GetUserIdentityUseCase
import com.expense_management.feature.group.mapper.GroupUiMapper
import com.expense_management.feature.group.ui.state.AddGroupUiState
import com.expense_management.feature.group.ui.state.GroupUiModel
import com.expense_management.feature.group_member.mapper.GroupMemberUiMapper
import com.expense_management.feature.group_member.ui.state.GroupMemberUiModel
import com.expense_management.feature.operation.mapper.OperationUiMapper
import com.expense_management.feature.operation.ui.state.OperationUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class AddGroupViewModel @Inject constructor(
    private val groupUiMapper: GroupUiMapper,
    private val createGroupUseCase: CreateGroupUseCase,
    private val getGroupByIdentityUseCase: GetGroupByIdentityUseCase,
    private val createGroupMemberUseCase: CreateGroupMemberUseCase,
    private val getGroupMemberByIdentityUseCase: GetGroupMemberByIdentityUseCase,
    private val createOperationUseCase: CreateOperationUseCase,
    private val getUserIdentityUseCase: GetUserIdentityUseCase,
    private val operationUiMapper: OperationUiMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGroupUiState())
    val uiState: StateFlow<AddGroupUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        val error = when {
            name.isBlank() -> R.string.is_blank_error_message
            name.length > 64 -> R.string.length_error_message
            else -> null
        }
        _uiState.update {
            it.copy(
                name = name,
                errorMessageId = error,
                isValid = error == null
            )
        }
    }

    fun addGroup() {
        val state = _uiState.value
        if (!state.isValid) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val currentUser = getUserIdentityUseCase().awaitData()
                val groupUiModel = GroupUiModel(name = state.name)
                val groupMemberUiModel = GroupMemberUiModel(
                    identity = currentUser.identity,
                    groupIdentity = groupUiModel.identity,
                    displayName = currentUser.name,
                    publicKey = PublicKey.from(currentUser.publicKey.toByteArray()),
                    role = GroupRole.Owner
                )

                createGroupUseCase(
                    groupUiMapper.toDomain(groupUiModel)
                ).getOrThrow()

                createGroupMemberUseCase(GroupMemberUiMapper.toDomain(groupMemberUiModel))
                    .getOrThrow()

                val group = getGroupByIdentityUseCase(groupUiModel.identity).awaitData()
                createOperation(
                    payload = groupUiMapper.toByteArray(group),
                    groupIdentity = groupUiModel.identity,
                    authorIdentity = currentUser.identity,
                    lamportClock = 1L,
                    operationType = OperationType.ADD_GROUP
                )

                val groupMember =
                    getGroupMemberByIdentityUseCase(groupMemberUiModel.identity).awaitData()
                createOperation(
                    payload = GroupMemberUiMapper.toByteArray(groupMember),
                    groupIdentity = groupUiModel.identity,
                    authorIdentity = currentUser.identity,
                    lamportClock = 2L,
                    operationType = OperationType.ADD_MEMBER
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private suspend fun createOperation(
        payload: ByteArray,
        groupIdentity: UUID,
        authorIdentity: UUID,
        lamportClock: Long,
        operationType: OperationType,
    ) {
        val operation = OperationUiModel(
            groupIdentity = groupIdentity,
            operationAuthorIdentity = authorIdentity,
            lamportClock = lamportClock,
            type = operationType,
            signedPayload = SignedPayload(
                payload = Payload.from(payload),
                signature = Signature.from(SecurityUtil.sign(payload))
            )
        )

        createOperationUseCase(operationUiMapper.toDomain(operation))
            .getOrThrow()
    }
}