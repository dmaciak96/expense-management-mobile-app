package com.expense_management.feature.group.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.expense_management.R
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Loading
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.core.common.awaitData
import com.expense_management.core.common.getOrThrow
import com.expense_management.core.security.SecurityUtil
import com.expense_management.domain.model.OperationType
import com.expense_management.domain.model.SignedPayload
import com.expense_management.domain.model.binary.Payload
import com.expense_management.domain.model.binary.Signature
import com.expense_management.domain.usecase.expense.DeleteExpenseUseCase
import com.expense_management.domain.usecase.expense.GetAllExpensesByGroupIdentityUseCase
import com.expense_management.domain.usecase.expense_share.DeleteExpenseShareUseCase
import com.expense_management.domain.usecase.expense_share.GetAllExpenseSharesByGroupMemberIdentityUseCase
import com.expense_management.domain.usecase.group.DeleteGroupUseCase
import com.expense_management.domain.usecase.group.GetGroupByIdentityUseCase
import com.expense_management.domain.usecase.group_member.DeleteGroupMemberUseCase
import com.expense_management.domain.usecase.group_member.GetAllGroupMembersByGroupIdentityUseCase
import com.expense_management.domain.usecase.operation.CreateOperationUseCase
import com.expense_management.domain.usecase.operation.GetAllOperationsByGroupIdentityUseCase
import com.expense_management.domain.usecase.user_identity.GetUserIdentityUseCase
import com.expense_management.feature.group.mapper.GroupUiMapper
import com.expense_management.feature.group.ui.DeleteGroupRoute
import com.expense_management.feature.group.ui.state.DeleteGroupUiState
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
class DeleteGroupViewModel @Inject constructor(
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val getGroupByIdentityUseCase: GetGroupByIdentityUseCase,
    private val groupUiMapper: GroupUiMapper,
    private val operationUiMapper: OperationUiMapper,
    private val getUserIdentityUseCase: GetUserIdentityUseCase,
    private val getAllGroupMembersByGroupIdentityUseCase: GetAllGroupMembersByGroupIdentityUseCase,
    private val deleteGroupMemberUseCase: DeleteGroupMemberUseCase,
    private val getAllExpensesByGroupIdentityUseCase: GetAllExpensesByGroupIdentityUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getAllExpenseSharesByGroupMemberIdentityUseCase: GetAllExpenseSharesByGroupMemberIdentityUseCase,
    private val deleteExpenseShareUseCase: DeleteExpenseShareUseCase,
    private val getAllOperationsByGroupIdentityUseCase: GetAllOperationsByGroupIdentityUseCase,
    private val createOperationUseCase: CreateOperationUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val groupIdentity =
        UUID.fromString(savedStateHandle.toRoute<DeleteGroupRoute>().identity)
    private val _uiState = MutableStateFlow(DeleteGroupUiState())
    val uiState: StateFlow<DeleteGroupUiState> = _uiState.asStateFlow()

    init {
        observeGroup()
    }

    private fun observeGroup() {
        viewModelScope.launch {
            getGroupByIdentityUseCase(groupIdentity)
                .collect { result ->
                    when (result) {
                        is Success -> {
                            _uiState.value = DeleteGroupUiState(
                                group = groupUiMapper.toUiState(result.data)
                            )
                        }

                        is Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true
                            )
                        }

                        is Error -> {
                            _uiState.value = DeleteGroupUiState(
                                errorMessageRes = R.string.general_error
                            )
                        }
                    }
                }
        }
    }

    fun deleteGroup() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            try {
                val group = _uiState.value.group
                if (group == null) {
                    _uiState.update {
                        it.copy(isLoading = false, isDeleted = true)
                    }
                    return@launch
                }
                val currentUser = getUserIdentityUseCase().awaitData()

                getAllGroupMembersByGroupIdentityUseCase(group.identity).awaitData()
                    .forEach { groupMember ->
                        getAllExpenseSharesByGroupMemberIdentityUseCase(groupMember.identity).awaitData()
                            .forEach { expenseShare -> deleteExpenseShareUseCase(expenseShare) }
                        deleteGroupMemberUseCase(groupMember)
                    }

                getAllExpensesByGroupIdentityUseCase(group.identity).awaitData()
                    .forEach { expense ->
                        deleteExpenseUseCase(expense)
                    }

                deleteGroupUseCase(
                    group = groupUiMapper.toDomain(group)
                ).getOrThrow()

                val currentLamportClock =
                    getAllOperationsByGroupIdentityUseCase(group.identity).awaitData()
                        .maxOfOrNull { it.lamportClock } ?: 0L

                val groupPayload = groupUiMapper.toByteArray(group)
                val operation = OperationUiModel(
                    groupIdentity = groupIdentity,
                    operationAuthorIdentity = currentUser.identity,
                    lamportClock = currentLamportClock + 1,
                    type = OperationType.REMOVE_GROUP,
                    signedPayload = SignedPayload(
                        payload = Payload.from(groupPayload),
                        signature = Signature.from(SecurityUtil.sign(groupPayload))
                    )
                )

                createOperationUseCase(operationUiMapper.toDomain(operation))
                    .getOrThrow()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDeleted = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.general_error
                    )
                }
            }
        }
    }

    companion object {
        private val TAG = DeleteGroupViewModel::class.simpleName
    }
}