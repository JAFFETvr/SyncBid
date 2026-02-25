package com.jaffetvr.syncbid.features.auth.data.repositories

import com.jaffetvr.syncbid.features.auth.data.datasource.remote.api.AuthApi
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.mapper.toDomain
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.LoginRequestDto
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.RegisterRequestDto
import com.jaffetvr.syncbid.features.auth.domain.entities.User
import com.jaffetvr.syncbid.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> =
        try {
            val response = authApi.login(LoginRequestDto(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(response.message() ?: "Error de autenticación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<User> =
        try {
            val response = authApi.register(RegisterRequestDto(fullName, email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(response.message() ?: "Error al registrar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
