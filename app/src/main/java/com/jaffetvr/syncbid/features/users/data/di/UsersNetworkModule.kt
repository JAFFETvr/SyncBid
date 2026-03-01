package com.jaffetvr.syncbid.features.users.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jaffetvr.syncbid.core.di.AuctionRetrofit
import com.jaffetvr.syncbid.features.users.data.datasource.remote.api.AuctionApi
import com.jaffetvr.syncbid.features.users.data.datasource.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsersNetworkModule {

    @Provides
    @Singleton
    fun provideAuctionApi(@AuctionRetrofit retrofit: Retrofit): AuctionApi =
        retrofit.create(AuctionApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(@AuctionRetrofit retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()
}
