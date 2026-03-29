package com.expense_management.feature.group.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.expense_management.R
import com.expense_management.feature.group.ui.state.DeleteGroupUiState
import kotlinx.serialization.Serializable

@Serializable
data class DeleteGroupRoute(val identity: String)

@Composable
fun DeleteGroupDialog(
    uiState: DeleteGroupUiState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.delete_group_title))
        },
        text = {
            Text(text = stringResource(R.string.confirm_text).format(uiState.group?.name))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.yes_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.no_label))
            }
        },
        modifier = modifier
    )
}