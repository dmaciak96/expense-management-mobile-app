package com.expense_management.core.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.expense_management.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopApplicationBar(
    title: String,
    onIconButtonClick: (() -> Unit)? = null,
    @StringRes iconDescId: Int = R.string.back_button_desc,
    @DrawableRes iconId: Int = R.drawable.ic_back,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(title)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        navigationIcon = {
            if (onIconButtonClick != null) {
                IconButton(onClick = onIconButtonClick) {
                    Icon(
                        painter = painterResource(iconId),
                        contentDescription = stringResource(iconDescId)
                    )
                }
            }
        },
        modifier = modifier
    )
}
