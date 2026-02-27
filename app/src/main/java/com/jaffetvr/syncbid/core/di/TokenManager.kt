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

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USERNAME = "username"
    }
}