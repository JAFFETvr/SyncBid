package com.jaffetvr.syncbid.features.auth.data.datasource.remote.api

import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.ApiResponse
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.LoginRequestDto
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.RegisterRequestDto
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.UserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    /**
     * POST http://10.0.2.2:8080/api/v1/auth/login
     * Devuelve: ApiResponse<String> donde data = JWT token
     */
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiResponse<String>>

    /**
     * POST http://10.0.2.2:8080/api/v1/auth/register
     * Devuelve: ApiResponse<UserResponseDto>
     */
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<ApiResponse<UserResponseDto>>
}