package com.expense_management.domain.model

import java.time.Instant

data class Group(
    val id: Int,
    val createdAt: Instant,
    val name: String
)
