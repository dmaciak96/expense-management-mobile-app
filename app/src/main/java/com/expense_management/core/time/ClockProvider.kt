package com.expense_management.core.time

import jakarta.inject.Inject
import java.time.Clock

interface ClockProvider {
    fun clock(): Clock
}

class DefaultClockProvider @Inject constructor() : ClockProvider {
    override fun clock(): Clock = Clock.systemUTC()
}