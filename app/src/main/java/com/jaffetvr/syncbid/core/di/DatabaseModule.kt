package com.jaffetvr.syncbid.core.di

import android.content.Context
import androidx.room.Room
import com.jaffetvr.syncbid.features.users.data.datasource.local.AppDatabase
import com.jaffetvr.syncbid.features.users.data.datasource.local.dao.AuctionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "syncbid_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideAuctionDao(database: AppDatabase): AuctionDao =
        database.auctionDao()
}
