package com.jaffetvr.syncbid.features.auth.data.repositories

import com.jaffetvr.syncbid.core.di.TokenManager
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.api.AuthApi
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.mapper.toDomain
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.LoginRequestDto
import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.RegisterRequestDto
import com.jaffetvr.syncbid.features.auth.domain.entities.User
import com.jaffetvr.syncbid.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))

            if (response.isSuccessful && response.body()?.success == true) {
                val token = response.body()!!.data
                    ?: return Result.failure(Exception("Token no recibido"))

                tokenManager.saveToken(token)
                tokenManager.saveEmail(email)

                val username = email.substringBefore("@")
                if (tokenManager.getUsername() == null) {
                    tokenManager.saveUsername(username)
                }

                Result.success(
                    User(
                        id = "",
                        fullName = tokenManager.getUsername() ?: username,
                        email = email,
                        token = token,
                        role = "USER"
                    )
                )
            } else {
                val errorMessage = response.body()?.message
                    ?: response.errorBody()?.string()
                    ?: "Credenciales incorrectas"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val response = authApi.register(
                RegisterRequestDto(
                    username = fullName,
                    email = email,
                    password = password
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val userDto = response.body()!!.data
                    ?: return Result.failure(Exception("Datos de usuario no recibidos"))

                tokenManager.saveUsername(userDto.username)
                tokenManager.saveEmail(email)
                tokenManager.saveUserId(userDto.id)
                userDto.createdAt?.let { tokenManager.saveCreatedAt(it) }

                val loginResult = login(email, password)
                if (loginResult.isSuccess) {
                    val token = tokenManager.getToken() ?: ""
                    Result.success(userDto.toDomain(token))
                } else {
                    Result.success(userDto.toDomain())
                }
            } else {
                val errorMessage = response.body()?.message
                    ?: response.errorBody()?.string()
                    ?: "Error en el registro"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}