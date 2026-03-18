package com.expense_management.feature.group.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.expense_management.app.Routes
import com.expense_management.feature.group.ui.screen.AddGroupDialog
import com.expense_management.feature.group.ui.screen.GroupDetailsScreen
import com.expense_management.feature.group.ui.screen.GroupsScreen
import com.expense_management.feature.group.ui.viewmodel.AddGroupViewModel
import com.expense_management.feature.group.ui.viewmodel.GroupDetailsViewModel
import com.expense_management.feature.group.ui.viewmodel.GroupListViewModel

@Composable
fun GroupsRoute(
    navController: NavController,
    viewModel: GroupListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GroupsScreen(uiState = uiState, onGroupClick = { groupId ->
        navController.navigate(Routes.groupDetailsRoute(groupId))
    })
}

@Composable
fun AddGroupRoute(
    navController: NavController,
    viewModel: AddGroupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AddGroupDialog(
        uiState = uiState,
        onDismiss = { navController.popBackStack() },
        onConfirm = {
            viewModel.addGroup()
            navController.popBackStack()
        },
        onNameChange = { viewModel.onNameChange(it) }
    )
}

@Composable
fun GroupDetailsRoute(
    viewModel: GroupDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GroupDetailsScreen(
        uiState = uiState,
    )
}

