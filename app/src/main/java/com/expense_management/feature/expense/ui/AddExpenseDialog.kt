package com.expense_management.feature.expense.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.expense_management.R
import com.expense_management.feature.expense.ui.state.AddExpenseUiState
import kotlinx.serialization.Serializable

@Serializable
data class AddExpenseRoute(
    val groupIdentity: String,
)


@Composable
fun AddExpenseDialog(
    uiState: AddExpenseUiState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.add_expense_title))
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
