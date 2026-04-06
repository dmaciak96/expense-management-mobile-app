package com.expense_management.feature.group.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.expense_management.R
import com.expense_management.feature.expense.ui.ExpenseScreen
import com.expense_management.feature.group.ui.state.GroupDetailsTab
import com.expense_management.feature.group.ui.state.GroupDetailsUiState
import com.expense_management.feature.group_member.ui.GroupMemberScreen
import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailsRoute(
    val identity: String,
    val name: String
)

@Composable
fun GroupDetailsScreen(
    uiState: GroupDetailsUiState,
    onTabSelected: (GroupDetailsTab) -> Unit,
    modifier: Modifier = Modifier
) {
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
            val tabs = GroupDetailsTab.entries
            val selectedTabIndex = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0)

            val pagerState = rememberPagerState(
                initialPage = selectedTabIndex,
                pageCount = { tabs.size }
            )
            LaunchedEffect(uiState.selectedTab) {
                val index = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0)
                if (pagerState.currentPage != index) {
                    pagerState.animateScrollToPage(index)
                }
            }

            LaunchedEffect(pagerState.currentPage) {
                val currentTab = tabs.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
                if (currentTab != uiState.selectedTab) {
                    onTabSelected(currentTab)
                }
            }

            Column(
                modifier = modifier.fillMaxSize()
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabs.forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = {
                                onTabSelected(tab)
                            },
                            text = {
                                Text(text = stringResource(tab.titleRes))
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (tabs[page]) {
                        GroupDetailsTab.Expenses -> {
                            ExpenseScreen(
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        GroupDetailsTab.Members -> {
                            GroupMemberScreen(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
