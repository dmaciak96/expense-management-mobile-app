package com.expense_management.core.time

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeHiltModule {

    @Binds
    @Singleton
    abstract fun bindClockProvider(
        impl: DefaultClockProvider
    ): ClockProvider

    @Binds
    @Singleton
    abstract fun bindLocalZoneProvider(
        impl: LocalZoneProvider
    ): ZoneProvider
}