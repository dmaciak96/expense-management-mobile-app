package com.expense_management.feature.group.model

import java.time.LocalDateTime

data class GroupUiState(
    val id: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val name: String = ""
)
