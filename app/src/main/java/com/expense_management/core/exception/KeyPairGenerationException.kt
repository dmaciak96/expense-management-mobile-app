package com.expense_management.core.exception

data class KeyPairGenerationException(val msg: String) : RuntimeException(msg)