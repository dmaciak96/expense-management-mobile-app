package com.expense_management.app.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.expense_management.feature.user_identity.ui.CreateUserIdentityDialog
import com.expense_management.feature.user_identity.ui.CreateUserIdentityRoute
import com.expense_management.feature.user_identity.viewmodel.CreateUserIdentityViewModel
import kotlinx.serialization.Serializable

@Serializable
object StartupRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = StartupRoute,
        modifier = modifier
    ) {
        composable<StartupRoute> {
            val viewModel: CreateUserIdentityViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.isMissingUserIdentity()
            }

            LaunchedEffect(uiState.isLoading, uiState.isMissing) {
                if (!uiState.isLoading) {
                    when {
                        uiState.isMissing -> {
                            navController.navigate(CreateUserIdentityRoute) {
                                popUpTo<StartupRoute> { inclusive = true }
                            }
                        }

                        else -> {
                            navController.navigate(GroupListRoute)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                }
            }
        }
        dialog<CreateUserIdentityRoute> {
            val viewModel: CreateUserIdentityViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val activity = LocalActivity.current

            CreateUserIdentityDialog(
                uiState = uiState,
                onNameChange = { viewModel.onNameChange(it) },
                onDismiss = { activity?.finish() },
                onConfirm = {
                    viewModel.createIdentity()
                    navController.navigate(GroupListRoute)
                }
            )
        }
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

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    navController.popBackStack()
                }
            }

            AddGroupDialog(
                uiState = uiState,
                onDismiss = { navController.popBackStack() },
                onConfirm = { viewModel.addGroup() },
                onNameChange = { viewModel.onNameChange(it) }
            )
        }
        dialog<DeleteGroupRoute> {
            val viewModel: DeleteGroupViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isDeleted) {
                if (uiState.isDeleted) {
                    navController.popBackStack()
                }
            }

            DeleteGroupDialog(
                uiState = uiState,
                onDismiss = { navController.popBackStack() },
                onConfirm = { viewModel.deleteGroup() }
            )
        }
    }
}