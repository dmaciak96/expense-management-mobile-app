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
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.expense_management.R
import com.expense_management.app.navigation.AppNavHost
import com.expense_management.app.ui.theme.AppTheme
import com.expense_management.core.component.FabButton
import com.expense_management.core.component.TopApplicationBar
import com.expense_management.feature.group.ui.AddGroupRoute
import com.expense_management.feature.group.ui.GroupDetailsRoute
import com.expense_management.feature.group.ui.GroupListRoute
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
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
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
