package com.jaffetvr.syncbid.features.auth.data.datasource.remote.mapper

import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.UserResponseDto
import com.jaffetvr.syncbid.features.auth.domain.entities.User

/**
 * Mapea la respuesta del servidor al dominio de Android.
 *
 * Servidor devuelve: { id(Long), username, email, createdAt }
 * Dominio Android:  { id(String), fullName, email, token, role }
 *
 * Nota: el token se guarda en TokenManager, NO en la entidad User.
 * El token se pasa como parámetro desde el repositorio.
 */
fun UserResponseDto.toDomain(token: String = ""): User = User(
    id = id.toString(),
    fullName = username,   // el servidor llama "username" a lo que el dominio llama "fullName"
    email = email,
    token = token,
    role = "USER"          // el servidor no devuelve rol en UserResponse, default USER
)