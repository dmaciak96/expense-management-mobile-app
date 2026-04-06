package com.expense_management.feature.group.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.expense_management.R
import com.expense_management.feature.expense.ui.ExpenseScreen
import com.expense_management.feature.group.ui.state.GroupDetailsUiState
import com.expense_management.feature.group_member.ui.GroupMemberScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailsRoute(
    val identity: String,
    val name: String
)

private enum class GroupDetailsTab(val titleRes: Int) {
    Expenses(R.string.expenses),
    Members(R.string.group_members)
}

@Composable
fun GroupDetailsScreen(uiState: GroupDetailsUiState, modifier: Modifier = Modifier) {
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
                val tabs = GroupDetailsTab.entries
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { tabs.size }
                )
                val scope = rememberCoroutineScope()

                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = pagerState.currentPage
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
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
}