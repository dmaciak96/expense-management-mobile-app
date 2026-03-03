package com.expense_management.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before

open class DatabaseTestSuite {
    protected lateinit var db: AppDb

    @Before
    fun createInMemoryDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDb::class.java
        ).build()
    }

    @After
    fun closeInMemoryDb() {
        db.close()
    }
}