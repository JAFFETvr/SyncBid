package com.jaffetvr.syncbid.features.auth.domain.useCases

import com.jaffetvr.syncbid.features.auth.domain.entities.User
import com.jaffetvr.syncbid.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.login(email, password)
}
