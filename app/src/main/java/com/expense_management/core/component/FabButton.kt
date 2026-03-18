package com.expense_management.core.component

import androidx.annotation.DrawableRes
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.expense_management.R

@Composable
fun FabButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int = R.drawable.ic_add
) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = stringResource(R.string.fab_button_desc)
        )
    }
}