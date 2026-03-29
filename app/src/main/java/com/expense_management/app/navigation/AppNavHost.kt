package com.expense_management.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.expense_management.feature.group.ui.AddGroupDialog
import com.expense_management.feature.group.ui.AddGroupRoute
import com.expense_management.feature.group.ui.DeleteGroupDialog
import com.expense_management.feature.group.ui.DeleteGroupRoute
import com.expense_management.feature.group.ui.GroupDetailsRoute
import com.expense_management.feature.group.ui.GroupDetailsScreen
import com.expense_management.feature.group.ui.GroupListRoute
import com.expense_management.feature.group.ui.GroupsScreen
import com.expense_management.feature.group.viewmodel.AddGroupViewModel
import com.expense_management.feature.group.viewmodel.DeleteGroupViewModel
import com.expense_management.feature.group.viewmodel.GroupDetailsViewModel
import com.expense_management.feature.group.viewmodel.GroupListViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = GroupListRoute,
        modifier = modifier
    ) {
        composable<GroupListRoute> {
            val viewModel: GroupListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            GroupsScreen(
                uiState = uiState,
                onClick = { identity, name ->
                    navController.navigate(GroupDetailsRoute(identity, name))
                },
                onSwipe = { identity ->
                    navController.navigate(DeleteGroupRoute(identity))
                }
            )
        }
        composable<GroupDetailsRoute> {
            val viewModel: GroupDetailsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            GroupDetailsScreen(uiState = uiState)
        }
        dialog<AddGroupRoute> {
            val viewModel: AddGroupViewModel = hiltViewModel()
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
        dialog<DeleteGroupRoute> {
            val viewModel: DeleteGroupViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            DeleteGroupDialog(
                uiState = uiState,
                onDismiss = { navController.popBackStack() },
                onConfirm = {
                    viewModel.deleteGroup()
                    navController.popBackStack()
                }
            )
        }
    }
}