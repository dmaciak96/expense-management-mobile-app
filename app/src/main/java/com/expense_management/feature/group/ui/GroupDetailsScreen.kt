package com.expense_management.feature.group.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.expense_management.R
import com.expense_management.feature.group.ui.state.GroupDetailsUiState
import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailsRoute(
    val identity: String,
    val name: String
)

@Composable
fun GroupDetailsScreen(uiState: GroupDetailsUiState) {
    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.group == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.group_not_found))
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // TODO: Remove after tests
                Text(
                    text = uiState.group.name,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}