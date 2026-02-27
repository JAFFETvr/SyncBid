package com.jaffetvr.syncbid.features.auth.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper genérico que el servidor devuelve en TODAS las respuestas.
 * Spring Boot: ApiResponse<T> { success, message, data, timestamp }
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?,
    @SerializedName("timestamp") val timestamp: String?
)

/**
 * POST /api/v1/auth/login
 * El servidor espera: { "email": "...", "password": "..." }
 */
data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/**
 * POST /api/v1/auth/register
 * El servidor espera: { "username": "...", "email": "...", "password": "..." }
 * NOTA: el campo es "username", NO "fullName" — así lo define UserRegisterRequest.java
 */
data class RegisterRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/**
 * Respuesta del registro — mapea UserResponse.java del servidor:
 * { id, username, email, createdAt }
 */
data class UserResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("createdAt") val createdAt: String?
)