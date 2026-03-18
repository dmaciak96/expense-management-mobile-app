package com.expense_management.feature.group.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.expense_management.R
import com.expense_management.feature.group.model.GroupListUiState
import com.expense_management.feature.group.model.GroupUiModel
import kotlinx.serialization.Serializable

@Serializable
object GroupListRoute

@Composable
fun GroupsScreen(
    uiState: GroupListUiState,
    onGroupClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.errorMessageRes != null -> {
                Text(
                    text = stringResource(uiState.errorMessageRes),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.groups.isEmpty() -> {
                Text(
                    text = stringResource(R.string.empty_groups),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.groups,
                        key = { it.id }
                    ) { group ->
                        GroupItem(group = group, onClick = { onGroupClick(group.id, group.name) })
                    }
                }
            }
        }
    }
}

@Composable
fun GroupItem(
    group: GroupUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium
            )

            // TODO: Add balance calculation
        }
    }
}