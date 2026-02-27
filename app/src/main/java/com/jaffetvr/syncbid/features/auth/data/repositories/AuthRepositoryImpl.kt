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

    /**
     * Login real contra el servidor.
     * El servidor devuelve ApiResponse<String> donde data = JWT token.
     */
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))

            if (response.isSuccessful && response.body()?.success == true) {
                val token = response.body()!!.data
                    ?: return Result.failure(Exception("Token no recibido"))

                // Guardamos el JWT para que AuthInterceptor lo use en futuras peticiones
                tokenManager.saveToken(token)

                // Construimos el User del dominio con los datos disponibles
                // (el login solo devuelve el token, no el perfil completo)
                Result.success(
                    User(
                        id = "",
                        fullName = email.substringBefore("@"),
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

    /**
     * Registro real contra el servidor.
     * El servidor devuelve ApiResponse<UserResponseDto>.
     * Después del registro, hacemos login automático para obtener el token.
     */
    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val response = authApi.register(
                RegisterRequestDto(
                    username = fullName,  // el servidor espera "username"
                    email = email,
                    password = password
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val userDto = response.body()!!.data
                    ?: return Result.failure(Exception("Datos de usuario no recibidos"))

                // Auto-login después del registro para obtener el JWT
                val loginResult = login(email, password)
                if (loginResult.isSuccess) {
                    val token = tokenManager.getToken() ?: ""
                    Result.success(userDto.toDomain(token))
                } else {
                    // Registro exitoso pero login falló — devolvemos el usuario sin token
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