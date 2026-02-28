package com.expense_management.data

import android.content.Context
import androidx.room.Room
import com.expense_management.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseHiltModule {

    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context): AppDb {
        return Room.databaseBuilder(
            context,
            AppDb::class.java,
            name = context.getString(R.string.app_name)
        ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideExpenseDao(appDb: AppDb) = appDb.expenseDao()

    @Provides
    fun provideExpenseShareDao(appDb: AppDb) = appDb.expenseShareDao()

    @Provides
    fun provideGroupDao(appDb: AppDb) = appDb.groupDao()

    @Provides
    fun provideGroupMemberDao(appDb: AppDb) = appDb.groupMemberDao()

    @Provides
    fun provideOperationDao(appDb: AppDb) = appDb.operationDao()
}