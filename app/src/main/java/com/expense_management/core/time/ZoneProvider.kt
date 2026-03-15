package com.expense_management.core.time

import jakarta.inject.Inject
import java.time.ZoneId

interface ZoneProvider {
    fun zoneId(): ZoneId
}

class LocalZoneProvider @Inject constructor() : ZoneProvider {
    override fun zoneId(): ZoneId = ZoneId.systemDefault()
}