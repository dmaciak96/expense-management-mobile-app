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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.expense_management.core.component.FabConfig
import com.expense_management.core.component.TopApplicationBar
import com.expense_management.feature.expense.ui.AddExpenseRoute
import com.expense_management.feature.group.ui.AddGroupRoute
import com.expense_management.feature.group.ui.GroupDetailsRoute
import com.expense_management.feature.group.ui.GroupListRoute
import com.expense_management.feature.group_member.ui.AddGroupMemberRoute
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
    var fabConfig by remember { mutableStateOf<FabConfig>(FabConfig.None) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { ConfigureFabButton(navController, fabConfig) },
        topBar = { ConfigureTopBar(navController) },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onFabConfigChange = { fabConfig = it }
        )
    }
}

@Composable
private fun ConfigureFabButton(
    navController: NavController,
    fabConfig: FabConfig
) {
    when (fabConfig) {
        FabConfig.None -> Unit

        FabConfig.AddGroup -> {
            FabButton(
                onClick = {
                    navController.navigate(AddGroupRoute)
                }
            )
        }

        is FabConfig.AddExpense -> {
            FabButton(
                onClick = {
                    navController.navigate(
                        AddExpenseRoute(groupIdentity = fabConfig.groupIdentity.toString())
                    )
                }
            )
        }

        is FabConfig.AddGroupMember -> {
            FabButton(
                onClick = {
                    navController.navigate(
                        AddGroupMemberRoute(groupIdentity = fabConfig.groupIdentity.toString())
                    )
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
