package com.expense_management.feature.group.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.expense_management.R
import com.expense_management.feature.group.model.AddGroupUiState
import kotlinx.serialization.Serializable

@Serializable
object AddGroupRoute

@Composable
fun AddGroupDialog(
    uiState: AddGroupUiState,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.add_group_title))
        },
        text = {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.group_name_label)) },
                isError = uiState.errorMessageId != null,
                supportingText = {
                    uiState.errorMessageId?.let {
                        Text(stringResource(it))
                    }
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = uiState.isValid,
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.ok_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel_label))
            }
        },
        modifier = modifier
    )
}