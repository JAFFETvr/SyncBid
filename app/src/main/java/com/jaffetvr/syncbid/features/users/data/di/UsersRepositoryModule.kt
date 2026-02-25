package com.jaffetvr.syncbid.features.users.data.di

import com.jaffetvr.syncbid.features.users.data.repositories.AuctionRepositoryImpl
import com.jaffetvr.syncbid.features.users.domain.repositories.AuctionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UsersRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuctionRepository(
        impl: AuctionRepositoryImpl
    ): AuctionRepository
}
