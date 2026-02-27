package com.jaffetvr.syncbid.core.di

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor que añade automáticamente el JWT a todas las peticiones
 * que NO sean de autenticación (/api/v1/auth/).
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // Las rutas de auth no necesitan token
        if (url.contains("/api/v1/auth/")) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getToken()

        // Si no hay token, dejamos pasar (el servidor responderá 401)
        if (token == null) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}