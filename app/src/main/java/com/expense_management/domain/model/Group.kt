package com.expense_management.domain.model

import java.time.Instant
import java.util.UUID

data class Group(
    val id: UUID,
    val createdAt: Instant,
    val name: String
)
