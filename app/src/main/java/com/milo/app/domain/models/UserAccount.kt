package com.milo.app.domain.models

data class UserAccount(
    val id: String,
    val username: String,
    val email: String,
    val authToken: String,
    val isGuest: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val lastSyncedAtEpochMs: Long? = null
)

enum class AuthState {
    LOGGED_OUT,
    AUTHENTICATED,
    GUEST
}
