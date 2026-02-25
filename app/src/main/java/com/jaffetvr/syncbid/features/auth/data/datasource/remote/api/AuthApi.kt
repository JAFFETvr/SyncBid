package com.jaffetvr.syncbid.features.auth.data.datasource.remote.api

import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.AuthResponseDto
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.LoginRequestDto
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>
}
