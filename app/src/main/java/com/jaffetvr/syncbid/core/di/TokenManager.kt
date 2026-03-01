package com.jaffetvr.syncbid.core.di

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("syncbid_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun saveUserId(id: Long) {
        prefs.edit().putLong(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)

    fun saveCreatedAt(createdAt: String) {
        prefs.edit().putString(KEY_CREATED_AT, createdAt).apply()
    }

    fun getCreatedAt(): String? = prefs.getString(KEY_CREATED_AT, null)

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_CREATED_AT = "created_at"
    }
}