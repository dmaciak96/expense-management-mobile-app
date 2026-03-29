package com.expense_management.feature.group.ui.state

import java.time.LocalDateTime
import java.util.UUID

data class GroupUiModel(
    val id: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val name: String = "",
    val identity: UUID = UUID.randomUUID(),
)
