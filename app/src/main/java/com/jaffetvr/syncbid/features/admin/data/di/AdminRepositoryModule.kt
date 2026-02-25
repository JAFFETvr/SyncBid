package com.jaffetvr.syncbid.features.admin.data.di

import com.jaffetvr.syncbid.features.admin.data.repositories.AdminRepositoryImpl
import com.jaffetvr.syncbid.features.admin.domain.repositories.AdminRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        impl: AdminRepositoryImpl
    ): AdminRepository
}
