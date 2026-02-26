package com.expense_management.core.common

sealed class OperationResult<out T> {
    data class Success<out T>(val data: T) : OperationResult<T>()
    data object Loading : OperationResult<Nothing>()
    data class Error(val exception: Throwable) : OperationResult<Nothing>()
}