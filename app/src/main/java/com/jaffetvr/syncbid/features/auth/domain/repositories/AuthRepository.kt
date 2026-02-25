package com.jaffetvr.syncbid.features.auth.domain.repositories

import com.jaffetvr.syncbid.features.auth.domain.entities.User

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(fullName: String, email: String, password: String): Result<User>
}
