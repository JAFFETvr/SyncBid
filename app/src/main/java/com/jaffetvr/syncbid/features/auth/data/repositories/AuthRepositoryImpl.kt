package com.jaffetvr.syncbid.features.auth.data.repositories

import com.jaffetvr.syncbid.features.auth.data.datasource.remote.api.AuthApi
import com.jaffetvr.syncbid.features.auth.domain.entities.User
import com.jaffetvr.syncbid.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            delay(1500)

            if (email == "admin@gmail.com" && password == "12345678") {
                Result.success(
                    User(
                        id = "0",
                        fullName = "Administrador",
                        email = "admin@gmail.com",
                        token = "mock_token_admin",
                        role = "ADMIN"
                    )
                )
            } else if (email == "prueba@gmail.com" && password == "12345678") {
                Result.success(
                    User(
                        id = "1",
                        fullName = "Carlos Jaffet",
                        email = "prueba@gmail.com",
                        token = "mock_token_123",
                        role = "USER"
                    )
                )
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            delay(1500)
            // Simulación de registro exitoso para cualquier dato
            Result.success(
                User(
                    id = "2",
                    fullName = fullName,
                    email = email,
                    token = "mock_token_reg",
                    role = "USER"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}