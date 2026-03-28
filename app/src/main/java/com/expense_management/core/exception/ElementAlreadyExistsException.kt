package com.expense_management.core.exception

data class ElementAlreadyExistsException(val msg: String) : RuntimeException(msg)
