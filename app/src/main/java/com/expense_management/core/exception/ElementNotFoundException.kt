package com.expense_management.core.exception

data class ElementNotFoundException(val msg: String) : RuntimeException(msg)