package com.expense_management.feature.group_member.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.expense_management.R
import com.expense_management.feature.group_member.ui.AddGroupMemberRoute
import com.expense_management.feature.group_member.ui.state.AddGroupMemberUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class AddGroupMemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val groupIdentity =
        UUID.fromString(savedStateHandle.toRoute<AddGroupMemberRoute>().groupIdentity)

    private val _uiState = MutableStateFlow(AddGroupMemberUiState())
    val uiState: StateFlow<AddGroupMemberUiState> = _uiState.asStateFlow()

    fun addGroupMember() {
        val state = _uiState.value
        if (!state.isValid) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }
        }

        try {
            Log.i(TAG, "Saving new group member inside group $groupIdentity")
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSaved = true
                )
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error when saving new group member inside group $groupIdentity: ${e.message}"
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessageId = R.string.general_error
                )
            }
        }
    }

    companion object {
        private val TAG = AddGroupMemberViewModel::class.simpleName
    }
}
