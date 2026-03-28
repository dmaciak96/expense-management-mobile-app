package com.expense_management.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.expense_management.R
import com.expense_management.app.ui.theme.AppTheme
import com.expense_management.core.component.FabButton
import com.expense_management.core.component.TopApplicationBar
import com.expense_management.feature.group.ui.screen.AddGroupDialog
import com.expense_management.feature.group.ui.screen.AddGroupRoute
import com.expense_management.feature.group.ui.screen.DeleteGroupDialog
import com.expense_management.feature.group.ui.screen.DeleteGroupRoute
import com.expense_management.feature.group.ui.screen.GroupDetailsRoute
import com.expense_management.feature.group.ui.screen.GroupDetailsScreen
import com.expense_management.feature.group.ui.screen.GroupListRoute
import com.expense_management.feature.group.ui.screen.GroupsScreen
import com.expense_management.feature.group.ui.viewmodel.AddGroupViewModel
import com.expense_management.feature.group.ui.viewmodel.DeleteGroupViewModel
import com.expense_management.feature.group.ui.viewmodel.GroupDetailsViewModel
import com.expense_management.feature.group.ui.viewmodel.GroupListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { ConfigureFabButton(navController) },
        topBar = { ConfigureTopBar(navController) },
        // TODO: Add Bottom Bar Configuration for Group Details View
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

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
        composable<GroupDetailsRoute> {
            val viewModel: GroupDetailsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            GroupDetailsScreen(uiState = uiState)
        }
    }
}

@Composable
private fun ConfigureFabButton(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val destination = backStackEntry?.destination

    when {
        destination?.hasRoute<GroupListRoute>() == true -> {
            FabButton(
                onClick = {
                    navController.navigate(AddGroupRoute)
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ConfigureTopBar(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val destination = backStackEntry?.destination

    when {
        destination?.hasRoute<GroupListRoute>() == true -> TopApplicationBar(stringResource(R.string.groups_top_bar_title))
        destination?.hasRoute<GroupDetailsRoute>() == true -> {
            val groupDetailsRoute = backStackEntry.toRoute<GroupDetailsRoute>()
            TopApplicationBar(
                title = groupDetailsRoute.name,
                onIconButtonClick = { navController.popBackStack() })
        }
    }
}
