package com.expense_management.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

sealed class OperationResult<out T> {
    data class Success<out T>(val data: T) : OperationResult<T>()
    data object Loading : OperationResult<Nothing>()
    data class Error(val exception: Throwable) : OperationResult<Nothing>()
}

fun <T> OperationResult<T>.getOrThrow(): T {
    return when (this) {
        is OperationResult.Success -> data
        is OperationResult.Error -> throw exception
        is OperationResult.Loading -> throw RuntimeException("Unexpected Loading state")
    }
}

suspend fun <T> Flow<OperationResult<T>>.awaitData(): T {
    val result = firstOrNull { it !is OperationResult.Loading }
        ?: throw IllegalStateException("Flow completed before emitting Success or Error")

    return when (result) {
        is OperationResult.Success -> result.data
        is OperationResult.Error -> throw result.exception
        is OperationResult.Loading -> error("Unexpected Loading state")
    }
}
