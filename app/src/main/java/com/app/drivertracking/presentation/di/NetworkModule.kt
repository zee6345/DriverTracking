package com.app.drivertracking.presentation.di

import com.app.drivertracking.data.api.ApiClient
import com.app.drivertracking.data.api.ApiService
import com.app.drivertracking.data.remotesource.AppRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideClient(): ApiService {
        return ApiClient.createService().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppRepo(apiService: ApiService): AppRepoImpl {
        return AppRepoImpl(apiService)
    }

}