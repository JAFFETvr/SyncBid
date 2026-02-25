package com.jaffetvr.syncbid.features.auth.domain.entities

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val token: String,
    val role: String
)
