package com.jaffetvr.syncbid.features.auth.data.datasource.remote.mapper

import com.jaffetvr.syncbid.features.auth.data.datasource.remote.model.AuthResponseDto
import com.jaffetvr.syncbid.features.auth.domain.entities.User

fun AuthResponseDto.toDomain(): User = User(
    id = id,
    fullName = fullName,
    email = email,
    token = token,
    role = role
)
