package com.expense_management.core.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SwipeableListItem(
    backgroundContent: SwipeBackgroundContent,
    content: @Composable RowScope.() -> Unit,
    onStartToEnd: () -> Unit,
    onEndToStart: () -> Unit,
    modifier: Modifier = Modifier,
    onSettled: () -> Unit = {},
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    )

    val coroutineScope = rememberCoroutineScope()
    SwipeToDismissBox(
        state = dismissState,
        onDismiss = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    coroutineScope.launch {
                        dismissState.reset()
                        onStartToEnd()
                    }
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    coroutineScope.launch {
                        dismissState.reset()
                        onEndToStart()
                    }
                }

                SwipeToDismissBoxValue.Settled -> {
                    coroutineScope.launch {
                        onSettled()
                    }
                }
            }
        },
        backgroundContent = {
            BackgroundSwipeContent(
                swipeDirection = dismissState.dismissDirection,
                swipeBackgroundContent = backgroundContent
            )
        },
        content = content,
        modifier = modifier
    )
}

data class SwipeBackgroundContent(
    val startToEndContent: SwipeDirectionContent,
    val endToStartContent: SwipeDirectionContent,
    val settledContent: SwipeDirectionContent,
)

data class SwipeDirectionContent(
    val backgroundColor: Color = Color.Transparent,
    @DrawableRes val iconId: Int,
    @StringRes val iconContentDescriptionId: Int

)

@Composable
private fun BackgroundSwipeContent(
    swipeDirection: SwipeToDismissBoxValue,
    swipeBackgroundContent: SwipeBackgroundContent
) {
    val color by animateColorAsState(
        when (swipeDirection) {
            SwipeToDismissBoxValue.StartToEnd -> swipeBackgroundContent.startToEndContent.backgroundColor
            SwipeToDismissBoxValue.EndToStart -> swipeBackgroundContent.endToStartContent.backgroundColor
            SwipeToDismissBoxValue.Settled -> swipeBackgroundContent.settledContent.backgroundColor
        },
    )

    val iconResId = when (swipeDirection) {
        SwipeToDismissBoxValue.StartToEnd -> swipeBackgroundContent.startToEndContent.iconId
        SwipeToDismissBoxValue.EndToStart -> swipeBackgroundContent.endToStartContent.iconId
        SwipeToDismissBoxValue.Settled -> swipeBackgroundContent.settledContent.iconId
    }

    val alignment = when (swipeDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.Settled -> Alignment.Center
    }

    val contentDescriptionId = when (swipeDirection) {
        SwipeToDismissBoxValue.StartToEnd -> swipeBackgroundContent.startToEndContent.iconContentDescriptionId
        SwipeToDismissBoxValue.EndToStart -> swipeBackgroundContent.endToStartContent.iconContentDescriptionId
        SwipeToDismissBoxValue.Settled -> swipeBackgroundContent.settledContent.iconContentDescriptionId
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = stringResource(contentDescriptionId)
        )
    }
}