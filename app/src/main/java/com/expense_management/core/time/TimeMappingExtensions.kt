package com.expense_management.core.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Instant.toLocalDateTime(zoneId: ZoneId): LocalDateTime =
    LocalDateTime.ofInstant(this, zoneId)

fun LocalDateTime.toInstant(zoneId: ZoneId): Instant =
    this.atZone(zoneId).toInstant()