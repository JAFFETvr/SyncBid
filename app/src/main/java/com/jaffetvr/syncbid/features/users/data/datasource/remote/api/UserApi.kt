package com.jaffetvr.syncbid.features.users.data.datasource.remote.api

import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.ApiResponse
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.UserProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("api/v1/users/{id}")
    suspend fun getUserProfile(@Path("id") id: Long): Response<ApiResponse<UserProfileDto>>
}
