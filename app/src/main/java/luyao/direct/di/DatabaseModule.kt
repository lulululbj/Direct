package luyao.direct.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import luyao.direct.model.AppDatabase
import javax.inject.Singleton

/**
 * ================================================
 * Copyright (c) 2020 All rights reserved
 * Description：
 * Author: luyao
 * Date： Date: 2021/12/1
 * ================================================
 */
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideAppDao(appDatabase: AppDatabase) = appDatabase.appDao()

    @Singleton
    @Provides
    fun provideDirectDao(appDatabase: AppDatabase) = appDatabase.directDao()

    @Singleton
    @Provides
    fun provideContactDao(appDatabase: AppDatabase) = appDatabase.contactDao()

    @Singleton
    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase) = appDatabase.searchHistoryDao()

    @Singleton
    @Provides
    fun provideNewDirectDao(appDatabase: AppDatabase) = appDatabase.newDirectDao()
}