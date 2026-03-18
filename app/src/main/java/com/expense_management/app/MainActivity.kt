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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.expense_management.R
import com.expense_management.app.ui.theme.AppTheme
import com.expense_management.core.component.FabButton
import com.expense_management.core.component.TopApplicationBar
import com.expense_management.feature.group.ui.AddGroupRoute
import com.expense_management.feature.group.ui.GroupDetailsRoute
import com.expense_management.feature.group.ui.GroupsRoute
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
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { ConfigureFabButton(currentRoute, navController) },
        topBar = { ConfigureTopBar(currentRoute, navController) },
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
        startDestination = Routes.GROUPS,
        modifier = modifier
    ) {
        composable(Routes.GROUPS) {
            GroupsRoute(navController)
        }
        dialog(Routes.ADD_GROUP) {
            AddGroupRoute(navController)
        }
        composable(Routes.GROUP_DETAILS_ROUTE) {
            GroupDetailsRoute()
        }
    }
}

@Composable
private fun ConfigureFabButton(currentRoute: String?, navHostController: NavHostController) {
    when (currentRoute) {
        Routes.GROUPS -> {
            FabButton(
                onClick = {
                    navHostController.navigate(Routes.ADD_GROUP)
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ConfigureTopBar(currentRoute: String?, navHostController: NavHostController) {
    when {
        currentRoute == Routes.GROUPS -> TopApplicationBar(stringResource(R.string.groups_top_bar_title))
        currentRoute?.startsWith(Routes.GROUP_DETAILS) == true -> TopApplicationBar(
            title = stringResource(
                R.string.group_details_top_bar_title
            ), onIconButtonClick = { navHostController.popBackStack() })
    }
}
