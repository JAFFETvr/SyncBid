package com.jaffetvr.syncbid.features.auth.domain.useCases

import com.jaffetvr.syncbid.features.auth.domain.entities.User
import com.jaffetvr.syncbid.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String
    ): Result<User> = authRepository.register(fullName, email, password)
}
