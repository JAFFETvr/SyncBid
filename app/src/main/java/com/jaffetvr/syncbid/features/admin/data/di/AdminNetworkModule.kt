package com.jaffetvr.syncbid.features.admin.data.di

import com.jaffetvr.syncbid.core.di.AuctionRetrofit
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.api.AdminApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminNetworkModule {

    @Provides
    @Singleton
    fun provideAdminApi(@AuctionRetrofit retrofit: Retrofit): AdminApi =
        retrofit.create(AdminApi::class.java)
}
